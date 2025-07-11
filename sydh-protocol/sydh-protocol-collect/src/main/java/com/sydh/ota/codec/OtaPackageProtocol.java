package com.sydh.ota.codec;

import com.sydh.common.annotation.SysProtocol;
import com.sydh.common.constant.SYDHConstant;
import com.sydh.common.extend.core.domin.mq.DeviceReport;
import com.sydh.common.extend.core.domin.mq.MQSendMessageBo;
import com.sydh.common.extend.core.domin.mq.message.DeviceData;
import com.sydh.common.extend.core.domin.mq.message.FunctionCallBackBo;
import com.sydh.common.extend.core.domin.ota.OtaPackageCode;
import com.sydh.ota.model.OtaCheckUtils;
import com.sydh.ota.model.OtaPackage;

import com.sydh.protocol.base.protocol.IProtocol;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 包装过的modbus-rtu协议
 *
 * @author gsb
 * @date 2022/11/15 11:16
 */
@Slf4j
@Component
@SysProtocol(name = "OTA升级协议", protocolCode = SYDHConstant.PROTOCOL.NetOTA, description = "OTA升级协议")
public class OtaPackageProtocol implements IProtocol {

    @Resource
    private OtaPackageDecoder netModbusDecoder;
    @Resource
    private OtaPackageEncoder netModbusEncoder;

    @Override
    public DeviceData decode(String message) {
        ByteBuf buf = Unpooled.wrappedBuffer(ByteBufUtil.decodeHexDump(message));
        OtaPackage netModbusRtu = netModbusDecoder.decode(buf);
        buf.release();
        DeviceData deviceData = DeviceData.builder().build();
        deviceData.setNetModbusCode(OtaPackageCode.getInstance(netModbusRtu.getCode()));
        deviceData.setData(netModbusRtu.getData());
        return deviceData;
    }

    @Override
    public byte[] encode(DeviceData data) {
        OtaPackage otaPackage = new OtaPackage();
        otaPackage.setCode(data.getNetModbusCode().getCode());
        otaPackage.setBitCount(data.getBitCount());
        otaPackage.setData(data.getData());
        ByteBuf out = netModbusEncoder.encode(otaPackage);
        byte[] result = new byte[out.writerIndex()];
        out.readBytes(result);
        ReferenceCountUtil.release(out);
        return OtaCheckUtils.addSumCheck(result);
    }

    @Override
    public DeviceReport decode(DeviceData deviceData, String clientId) {
        return null;
    }

    @Override
    public FunctionCallBackBo encode(MQSendMessageBo bo) {
        return null;
    }

}
