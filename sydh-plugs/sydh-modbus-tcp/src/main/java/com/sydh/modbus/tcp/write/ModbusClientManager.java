package com.sydh.modbus.tcp.write;

import com.sydh.common.core.domain.AjaxResult;
import com.sydh.common.enums.FunctionReplyStatus;
import com.sydh.common.extend.core.domin.model.device.DeviceAndProtocol;
import com.sydh.common.extend.core.domin.mq.message.ModbusDevice;
import com.sydh.common.extend.core.protocol.modbus.ModbusCode;
import com.sydh.common.utils.DateUtils;
import com.sydh.iot.domain.FunctionLog;
import com.sydh.iot.service.IDeviceService;
import com.sydh.iot.service.IFunctionLogService;
import com.sydh.modbus.tcp.codec.ModbusTcpCodec;
import com.sydh.modbus.tcp.core.ModbusClientHandler;
import com.sydh.modbus.tcp.model.ModbusCommand;
import com.sydh.modbus.tcp.model.RequestContext;
import com.sydh.modbus.tcp.utils.TransactionManager;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Modbus客户端处理
 *
 * @author bill
 */
@Component
@Slf4j
public class ModbusClientManager {

    // 设备连接缓存 (Key: "ip:port")
    private final ConcurrentMap<String, Channel> channelCache = new ConcurrentHashMap<>();

    private final EventLoopGroup workerGroup = new NioEventLoopGroup();
    private final Bootstrap bootstrap = new Bootstrap();
    private final TransactionManager transactionManager;
    private final IFunctionLogService functionLogService;
    private final IDeviceService deviceService;

    private static final AttributeKey<ModbusDevice> DEVICE_ATTR = AttributeKey.valueOf("device");


    public ModbusClientManager(TransactionManager transactionManager,
                               IFunctionLogService functionLogService,
                               ModbusClientHandler modbusClientHandler, IDeviceService deviceService) {
        this.transactionManager = transactionManager;
        this.functionLogService = functionLogService;
        this.deviceService = deviceService;

        bootstrap.group(workerGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ch.pipeline().addLast(
                                new ModbusTcpCodec.Decoder(),
                                modbusClientHandler
                        );
                    }
                });
    }

    //发送对外接口
    public CompletableFuture<AjaxResult> executeCommand(String clientId, ModbusCommand command) {
        try {
            int result = this.sendCommand(clientId, command);
            return CompletableFuture.completedFuture(AjaxResult.success(result));
        }catch (Exception e){
            throw new RuntimeException("modbusTCP下发指令失败:" + e.getMessage());
        }

    }

    // 发送Modbus指令
    public int sendCommand(String clientId, ModbusCommand command) {

        DeviceAndProtocol ctx = deviceService.getDeviceProtocolDetail(clientId);

        // 生成事务ID并存储上下文
        int transactionId = transactionManager.nextTransactionId(ctx.getDeviceId());
        command.setTransactionId(transactionId);

        RequestContext requestContext = createRequestContext(ctx, command);
        transactionManager.putContext(transactionId, requestContext);

        //指令日志 ,读指令不记录
        setCommandLog(transactionId, ctx.getSerialNumber(), command);

        byte[] request = buildModbusFrame(command, ctx);

        sendRequest(ctx, request, transactionId, command.getCode());

        return transactionId;


    }

    // 指令上下文
    private RequestContext createRequestContext(DeviceAndProtocol ctx, ModbusCommand command) {
        return new RequestContext(
                ctx.getDeviceId(),
                command.getQuantity(),
                command.getRegister(),
                System.currentTimeMillis()
        );
    }

    //构建指令下发报文
    private void setCommandLog(int transactionId, String clientId, ModbusCommand command) {
        if (justCode(command.getCode())) {
            String writeValue = ByteBufUtil.hexDump(command.getData());
            FunctionLog log = new FunctionLog()
                    .setCreateTime(DateUtils.getNowDate())
                    .setFunValue(writeValue)
                    .setModelName("modbusTCP-" + transactionId)
                    .setMessageId(String.valueOf(transactionId)) //modbusTCP协议的事务id
                    .setSerialNumber(clientId)
                    .setIdentify(String.valueOf(command.getRegister()))
                    .setShowValue(writeValue)
                    .setResultMsg(FunctionReplyStatus.NO_REPLY.getMessage())
                    .setResultCode(FunctionReplyStatus.NO_REPLY.getCode())
                    .setFunType(1);
            functionLogService.insertFunctionLog(log);
        }
    }


    private byte[] buildModbusFrame(ModbusCommand command, DeviceAndProtocol ctx) {
        ByteBuf buffer = Unpooled.buffer();

        // MBAP Header
        buffer.writeShort(command.getTransactionId()); //事务id
        buffer.writeShort(0x0000); // 协议id
        buffer.writeShort(0x0000); // 长度
        buffer.writeByte(command.getAddress());  //地址

        // 功能代码处理
        buffer.writeByte(command.getCode());

        switch (ModbusCode.getInstance(command.getCode())) {
            case Read01:  // 读线圈
            case Read02:  // 读离散出入
            case Read03:  // 读持有寄存器
            case Read04:  // 读取输入寄存器
                buffer.writeShort(command.getRegister());
                buffer.writeShort(command.getQuantity());
                break;

            case Write05:  // 写单线线圈
                handleWriteSingleCoil(command, buffer);
                break;

            case Write06:  // 写单寄存器
                handleWriteSingleRegister(command, buffer);
                break;

            case Write0F: // 写多个线圈
                handleWriteMultipleCoils(command, buffer);
                break;

            case Write10: // 写多个寄存器
                handleWriteMultipleRegisters(command, buffer);
                break;

            default:
                throw new IllegalArgumentException("不支持的功能码: " + command.getCode());
        }

        int length = buffer.readableBytes() - 6;
        buffer.setShort(4, length);

        byte[] result = new byte[buffer.readableBytes()];
        buffer.readBytes(result);
        return result;
    }

    private void handleWriteSingleCoil(ModbusCommand command, ByteBuf buffer) {
        buffer.writeShort(command.getRegister());
        int value = (command.getData()[0] != 0) ? 0xFF00 : 0x0000;
        buffer.writeShort(value);
    }

    private void handleWriteSingleRegister(ModbusCommand command, ByteBuf buffer) {
        buffer.writeShort(command.getRegister());
        buffer.writeShort(bytesToShort(command.getData()));
    }

    private void handleWriteMultipleCoils(ModbusCommand command, ByteBuf buffer) {
        buffer.writeShort(command.getRegister());
        buffer.writeShort(command.getQuantity());

        byte[] data = command.getData();
        buffer.writeByte(data.length);
        buffer.writeBytes(data);
    }

    private void handleWriteMultipleRegisters(ModbusCommand command, ByteBuf buffer) {
        buffer.writeShort(command.getRegister());
        buffer.writeShort(command.getQuantity());
        buffer.writeByte(command.getData().length);
        buffer.writeBytes(command.getData());
    }

    private short bytesToShort(byte[] bytes) {
        return (short) (((bytes[0] & 0xFF) << 8) | (bytes[1] & 0xFF));
    }


    private void sendRequest(DeviceAndProtocol ctx, byte[] request,
                                                  int transactionId, int functionCode) {
        String target = ctx.getDeviceIp() + ":" + ctx.getDevicePort();
        Channel channel = channelCache.get(target);

        if (channel != null && channel.isActive()) {
            ModbusDevice modbusDevice = new ModbusDevice()
                    .setDeviceIp(ctx.getSerialNumber())
                    .setDeviceId(ctx.getDeviceId())
                    .setDeviceIp(ctx.getDeviceIp());
            channel.attr(DEVICE_ATTR).set(modbusDevice);

            channel.writeAndFlush(Unpooled.wrappedBuffer(request));
            log.info("[连接复用] 设备连接成功 | 指令已发送 | 事务ID={}, 目标地址={},指令内容={}", transactionId, target,ByteBufUtil.hexDump(request));

        }else {
            bootstrap.connect(ctx.getDeviceIp(), ctx.getDevicePort()).addListener((ChannelFutureListener) f -> {
                if (f.isSuccess()) {

                    ModbusDevice modbusDevice = new ModbusDevice()
                            .setSerialNumber(ctx.getSerialNumber())
                            .setDeviceId(ctx.getDeviceId())
                            .setDeviceIp(ctx.getDeviceIp())
                            .setDevicePort(ctx.getDevicePort());
                    f.channel().attr(DEVICE_ATTR).set(modbusDevice);

                    channelCache.put(target,f.channel());
                    f.channel().writeAndFlush(Unpooled.wrappedBuffer(request));

                    f.channel().closeFuture().addListener(cf -> channelCache.remove(target));
                    log.info("设备连接成功 | 指令已发送 | 事务ID={}, 目标地址={},指令内容={}", transactionId, target,ByteBufUtil.hexDump(request));

                } else {
                    log.error("设备连接失败 | 事务ID={}, 目标地址={}, 错误原因={}",
                            transactionId, target, f.cause().getMessage());
                    updateFunctionResult(transactionId, f.cause(), functionCode);
                }
            });
        }


    }


    private void updateFunctionResult(int transactionId, Throwable cause, int functionCode) {

        if (justCode(functionCode)) {
            FunctionLog functionLog = new FunctionLog()
                    .setResultMsg(cause.getMessage())
                    .setResultCode(FunctionReplyStatus.FAIL.getCode())
                    .setMessageId(String.valueOf(transactionId))
                    .setReplyTime(DateUtils.getNowDate());
            functionLogService.updateByMessageId(functionLog);
        }

    }

    private boolean justCode(int functionCode) {
        return functionCode == 5 || functionCode == 6
                || functionCode == 15 || functionCode == 16;
    }

    @PreDestroy
    public void shutdown() {
        workerGroup.shutdownGracefully();
    }
}
