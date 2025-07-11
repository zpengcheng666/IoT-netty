package com.sydh.register;

import com.sydh.common.annotation.SysProtocol;
import com.sydh.common.constant.SYDHConstant;
import com.sydh.common.extend.core.domin.mq.DeviceReport;
import com.sydh.common.extend.core.domin.mq.MQSendMessageBo;
import com.sydh.common.extend.core.domin.mq.message.DeviceData;
import com.sydh.common.extend.core.domin.mq.message.FunctionCallBackBo;
import com.sydh.protocol.base.protocol.IProtocol;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import org.springframework.stereotype.Component;

/**
 * @author gsb
 * @date 2025/3/31 10:50
 */
@Component
@SysProtocol(name = "通用注册包", protocolCode = SYDHConstant.PROTOCOL.RJ45, description = "通用注册包")
public class ModbusRegister implements IProtocol {



    @Override
    public DeviceReport decode(DeviceData data, String clientId) {
        DeviceReport deviceReport = new DeviceReport();
        ByteBuf buf = data.getBuf();
        String hexDump = ByteBufUtil.hexDump(buf);
         clientId = hexDump.substring(6);
         deviceReport.setClientId(clientId);
         deviceReport.setIsPackage(false);
         deviceReport.setSerialNumber(clientId);
        return deviceReport;
    }

    @Override
    public FunctionCallBackBo encode(MQSendMessageBo message) {
        return null;
    }
}
