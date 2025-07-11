package com.sydh.pakModbus.codec;

import com.sydh.common.exception.ServiceException;
import com.sydh.common.utils.MessageUtils;
import com.sydh.common.utils.gateway.CRC16Utils;
import com.sydh.pakModbus.model.PakModbusRtu;
import com.sydh.protocol.WModelManager;
import com.sydh.protocol.base.model.ActiveModel;
import com.sydh.protocol.util.ArrayMap;
import com.sydh.protocol.util.ExplainUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * modbus-rtu协议解码器
 * @author bill
 */
@Slf4j
@Component
@NoArgsConstructor
public class ModbusRtuPakDecoder {

    @Autowired
    private  WModelManager modelManager;
    private  ArrayMap<ActiveModel> headerSchemaMap;

    public ModbusRtuPakDecoder(String...basePackages) {
        this.modelManager = new WModelManager(basePackages);
        this.headerSchemaMap = this.modelManager.getActiveMap(PakModbusRtu.class);
    }

    public PakModbusRtu decode(ByteBuf in){
        return decode(in,null);
    }

    public PakModbusRtu decode(ByteBuf in, ExplainUtils explain){
        this.build();
        ByteBuf copy = in.duplicate();
        byte[] bytes = new byte[in.writerIndex()];
        copy.readBytes(bytes);
        verify(bytes);
        ActiveModel<PakModbusRtu> activeModel = headerSchemaMap.get(0);
        PakModbusRtu message = new PakModbusRtu();
        message.setPayload(in);
        message.setVerified(false);
        activeModel.mergeFrom(in,message,explain);
        log.info("=>解析:[{}]",message);
        return message;
    }

    public PakModbusRtu decodeMessage(ByteBuf in, int version){
        this.build();
        ByteBuf copy = in.duplicate();
        byte[] bytes = new byte[in.writerIndex()];
        copy.readBytes(bytes);
        ActiveModel<PakModbusRtu> activeModel = headerSchemaMap.get(version);
        PakModbusRtu message = new PakModbusRtu();
        message.setPayload(in);
        message.setVerified(false);
        activeModel.mergeFrom(in,message,null);
        log.info("=>解析:[{}]",message);
        return message;
    }

    private void verify(byte[] source){
        byte[] checkBytes = {source[source.length - 3],source[source.length -2]};
        byte[] sourceCheck = ArrayUtils.subarray(source,4,source.length -3);
        String crc = CRC16Utils.getCRC(sourceCheck);
        if (!crc.equalsIgnoreCase(ByteBufUtil.hexDump(checkBytes))){
            log.warn("=>CRC校验异常,报文={}",ByteBufUtil.hexDump(source));
            throw new ServiceException(MessageUtils.message("modbus.crc.check.abnormal"));
        }
    }

    private void build(){
        if (this.headerSchemaMap == null) {
            this.headerSchemaMap = this.modelManager.getActiveMap(PakModbusRtu.class);
        }
    }


}
