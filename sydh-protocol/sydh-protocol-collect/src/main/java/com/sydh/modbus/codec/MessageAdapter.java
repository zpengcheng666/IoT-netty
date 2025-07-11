package com.sydh.modbus.codec;

import com.sydh.base.codec.MessageDecoder;
import com.sydh.base.codec.MessageEncoder;
import com.sydh.common.ProtocolColl;
import com.sydh.common.constant.SYDHConstant;
import com.sydh.common.exception.ServiceException;
import com.sydh.common.extend.core.domin.mq.DeviceReport;
import com.sydh.common.extend.core.domin.mq.message.DeviceData;
import com.sydh.common.extend.core.protocol.Message;
import com.sydh.common.utils.StringUtils;
import com.sydh.iot.model.ProductCode;
import com.sydh.iot.service.IDeviceService;
import com.sydh.protocol.base.protocol.IProtocol;
import com.sydh.protocol.service.IProtocolManagerService;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 消息编解码适配器
 *
 * @author bill
 */
@Slf4j
@Component
public class MessageAdapter implements MessageDecoder, MessageEncoder{


    @Autowired
    private IProtocolManagerService managerService;
    @Resource
    private IDeviceService deviceService;

    /**
     * modbus消息解码
     *
     * @param buf     原数据
     * @param  clientId 客户端id
     * @return 解析后bean
     */
    @Override
    public DeviceReport decode(ByteBuf buf, String clientId) {
        IProtocol protocol = null;
        Long productId = null;
        String devNum = new String(ByteBufUtil.getBytes(buf));
        String dump = ByteBufUtil.hexDump(buf);
        log.info("=>上报hex数据:{}", dump);

        //这里兼容一下TCP整包发送的数据(整包==设备编号，数据一起发送)
        if (clientId == null){
            if (StringUtils.isNotEmpty(dump)) {
                assert dump != null;
                if (dump.startsWith("68") && dump.endsWith("16")) {
                    protocol = managerService.getProtocolByProtocolCode(SYDHConstant.PROTOCOL.FlowMeter);
                }else if ((dump.startsWith("7e")&& dump.endsWith("7e"))){
                    protocol = managerService.getProtocolByProtocolCode(SYDHConstant.PROTOCOL.ModbusRtu);
                }else if (devNum.startsWith("{") && devNum.endsWith("}")){
                    protocol = managerService.getProtocolByProtocolCode(SYDHConstant.PROTOCOL.SGZ);
                }else if (devNum.startsWith("[") && devNum.endsWith("]")){
                    protocol = managerService.getProtocolByProtocolCode(SYDHConstant.PROTOCOL.JsonArray);
                }else if (dump.startsWith("fedc")){
                    protocol = managerService.getProtocolByProtocolCode(SYDHConstant.PROTOCOL.CH);
                }else if ( dump.startsWith("500100")){
                    protocol = managerService.getProtocolByProtocolCode(SYDHConstant.PROTOCOL.RJ45);
                }
            }

        }else {
            ProtocolColl coll = selectedProtocol(clientId);
            protocol = coll.getProtocol();
            productId = coll.getProductId();
        }
        DeviceData deviceData = DeviceData.builder()
                .buf(buf)
                .productId(productId)
                .serialNumber(clientId)
                .data(ByteBufUtil.getBytes(buf))
                .build();
        return protocol.decode(deviceData, clientId);
    }


    /**
     * modbus消息编码
     *
     * @param message modbusbean
     * @return 编码指令
     */
    @Override
    public ByteBuf encode(Message message, String clientId) {
        String serialNumber = StringUtils.isNotEmpty(clientId) ? clientId : message.getClientId();
        ProtocolColl protocol = selectedProtocol(serialNumber);
        IProtocol instance = protocol.getProtocol();
        DeviceData data = DeviceData.builder().body(message).build();
        byte[] out = instance.encodeCallBack(data);
        log.info("应答设备,clientId=[{}],指令=[{}]", serialNumber, ByteBufUtil.hexDump(out));
        return Unpooled.wrappedBuffer(out);
    }

    private ProtocolColl selectedProtocol(String serialNumber) {
        ProtocolColl protocolColl = new ProtocolColl();
        try {
            ProductCode protocol = deviceService.getProtocolBySerialNumber(serialNumber);
            if (protocol == null || StringUtils.isEmpty(protocol.getProtocolCode())) {
                protocol.setProtocolCode(SYDHConstant.PROTOCOL.ModbusRtu);
            }
//            } else if (ServerType.TCP.getCode().equals(protocol.getTransport()) && SYDHConstant.PROTOCOL.ModbusRtu.equals(protocol.getProtocolCode())) {
//                protocol.setProtocolCode(SYDHConstant.PROTOCOL.ModbusRtuTcp);
//            }
            IProtocol code = managerService.getProtocolByProtocolCode(protocol.getProtocolCode());
            protocolColl.setProtocol(code);
            protocolColl.setProductId(protocol.getProductId());
            return protocolColl;
        } catch (Exception e) {
            throw new ServiceException(e.getMessage());
        }
    }
}
