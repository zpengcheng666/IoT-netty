package com.fastbee.modbustcp.codec.tcpIp;

import com.fastbee.common.exception.ServiceException;
import com.fastbee.common.extend.core.domin.mq.message.DeviceData;
import com.fastbee.common.extend.core.protocol.modbus.ModbusCode;
import com.fastbee.modbus.pak.TcpDtu;
import com.fastbee.modbustcp.model.ModbusTcp;
import com.fastbee.protocol.WModelManager;
import com.fastbee.protocol.base.model.ActiveModel;
import com.fastbee.protocol.util.ArrayMap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


/**
 * modbus-tcp协议解码器
 *
 * @author bill
 */
@Slf4j
@Component
@NoArgsConstructor
public class ModbusTcpIpDecoder {

    @Resource
    private WModelManager modelManager;
    private ArrayMap<ActiveModel> headerSchemaMap;

    public ModbusTcpIpDecoder(String... basePackages) {
        this.modelManager = new WModelManager(basePackages);
        this.headerSchemaMap = this.modelManager.getActiveMap(ModbusTcp.class);
    }

    public ModbusTcp decode(DeviceData deviceData) {
        try {
            if (null == deviceData.getCode() && null != deviceData.getResponse()) {
                ModbusCode code = ModbusCode.getInstance(deviceData.getResponse().getCode());
                deviceData.setCode(code);
            }
            this.build();
              int currentMessageId =0;
            if (deviceData.getCode() == ModbusCode.Write06 || deviceData.getCode() == ModbusCode.Write05 ) {
                //设备回复06解析
                currentMessageId = 2;
            } else if (deviceData.getCode() == ModbusCode.Read01 || deviceData.getCode() == ModbusCode.Read02 ) {
                // 01、02解析
                currentMessageId = 4;
            }

            ActiveModel<ModbusTcp> activeModel = headerSchemaMap.get(currentMessageId);
            ModbusTcp message = new ModbusTcp();
            activeModel.mergeFrom(Unpooled.wrappedBuffer(deviceData.getData()), message, null);
            return message;
        } catch (Exception e) {
            log.error("解析报错:{}",deviceData.getResponse().getRawHex());
            return null;
        }
    }

    private void build() {
        if (this.headerSchemaMap == null) {
            this.headerSchemaMap = this.modelManager.getActiveMap(ModbusTcp.class);
        }
    }


}
