package com.sydh.modbus.tcp.core;

import com.alibaba.fastjson2.JSON;
import com.sydh.common.constant.SYDHConstant;
import com.sydh.common.enums.FunctionReplyStatus;
import com.sydh.common.enums.ServerType;
import com.sydh.common.extend.core.domin.mq.DeviceReportBo;
import com.sydh.common.extend.core.domin.mq.message.ModbusDevice;
import com.sydh.common.utils.DateUtils;
import com.sydh.common.extend.core.domin.mq.ModbusResponse;
import com.sydh.iot.domain.FunctionLog;
import com.sydh.iot.service.IFunctionLogService;
import com.sydh.modbus.tcp.model.RequestContext;
import com.sydh.modbus.tcp.utils.TransactionManager;
import com.sydh.mq.producer.MessageProducer;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@ChannelHandler.Sharable
@Slf4j
@Component
public class ModbusClientHandler extends SimpleChannelInboundHandler<ModbusResponse> {


    private static final ExecutorService businessExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()/2);

    private final TransactionManager transactionManager;
    private final IFunctionLogService functionLogService;

    public ModbusClientHandler(TransactionManager transactionManager, IFunctionLogService functionLogService) {
        this.transactionManager = transactionManager;
        this.functionLogService = functionLogService;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ModbusResponse response) {

        businessExecutor.execute(() -> {
            ModbusDevice modbusDevice = ctx.channel().attr(AttributeKey.<ModbusDevice>valueOf("device")).get();
            RequestContext requestContext = transactionManager.getContext(response.getTransactionId(), modbusDevice.getDeviceId());
            if (requestContext == null) {
                log.error("[事务丢失],设备:{},事务ID：{},已经超时 ",response.getClientId(),response.getTransactionId());
                return;
            }

            if (!requestContext.getDeviceId().equals(modbusDevice.getDeviceId())){
                log.error("[设备不匹配],事务ID：{}，预期设备:{},实际设备:{}",
                        response.getTransactionId(),
                        requestContext.getDeviceId(),
                        modbusDevice.getDeviceId());
            }

            if (!justCode(response.getCode())){ //分区 读写指令来处理
                //拼接数据返回
                response.setClientId(modbusDevice.getDeviceIp());
                response.setRegister(requestContext.getRegisterId());
                response.setQuantity(requestContext.getQuantity());
                log.info("[数据接收] 设备:{},从机地址:{},寄存器:{} 收到响应数据:{}",
                        modbusDevice.getDeviceIp(),response.getAddress(),response.getRegister(),  JSON.toJSONString(response));

                //转发到MQ处理
                DeviceReportBo reportBo = new DeviceReportBo();
                reportBo.setSerialNumber(response.getClientId());
                reportBo.setServerType(ServerType.OTHER);
                reportBo.setPlatformDate(DateUtils.getNowDate());
                reportBo.setIsReply(false);
                reportBo.setData(ByteBufUtil.decodeHexDump(response.getRawHex()));
                reportBo.setSources(response.getRawHex());
                reportBo.setResponse(response);
                reportBo.setProtocolCode(SYDHConstant.PROTOCOL.ModbusTcp);
                MessageProducer.sendPublishMsg(reportBo);
            }else {
                updateFunctionResult(response.getTransactionId(), 1,null);
            }

            transactionManager.removeContext(response.getTransactionId(), modbusDevice.getDeviceId());
        });

    }


    private void updateFunctionResult(int transactionId, int success, Throwable cause) {
        FunctionReplyStatus status = success == 1 ?
                FunctionReplyStatus.SUCCESS : FunctionReplyStatus.FAIL;

        FunctionLog log = new FunctionLog()
                .setMessageId(String.valueOf(transactionId))
                .setResultCode(status.getCode())
                .setResultMsg(cause != null ? cause.getMessage() : status.getMessage())
                .setReplyTime(DateUtils.getNowDate());

        functionLogService.updateByMessageId(log);
    }


    private boolean justCode(int functionCode) {
        return functionCode == 5 || functionCode == 6
                || functionCode == 15 || functionCode == 16;
    }

}
