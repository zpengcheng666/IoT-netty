package com.sydh.flowdev.codec;

import com.sydh.common.exception.ServiceException;
import com.sydh.common.extend.core.domin.mq.message.DeviceData;
import com.sydh.common.utils.MessageUtils;
import com.sydh.common.utils.gateway.CRC8Utils;
import com.sydh.flowdev.model.FlowDev;
import com.sydh.protocol.WModelManager;
import com.sydh.protocol.base.model.ActiveModel;
import com.sydh.protocol.util.ArrayMap;
import com.sydh.protocol.util.ExplainUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author gsb
 * @date 2023/5/17 16:37
 */
@Slf4j
@Component
@NoArgsConstructor
public class FlowDevDecoder {

    @Resource
    private WModelManager modelManager;
    private ArrayMap<ActiveModel> headerSchemaMap;

    public FlowDevDecoder(String...basePackages){
        this.modelManager = new WModelManager(basePackages);
        this.headerSchemaMap = this.modelManager.getActiveMap(FlowDev.class);
    }

    public FlowDev decode(DeviceData deviceData, ExplainUtils explain){
        this.build();
        ByteBuf in = deviceData.getBuf();
        verify(in);
        ActiveModel<FlowDev> activeModel = headerSchemaMap.get(0);
        FlowDev flowDev = new FlowDev();
        activeModel.mergeFrom(in,flowDev,explain);
        log.info("=>流量计数据解析:[{}]",flowDev);
        return flowDev;
    }

    /*CRC校验*/
    private void verify(ByteBuf in){
        ByteBuf copy = in.duplicate();
        byte[] source = new byte[in.writerIndex()];
        copy.readBytes(source);
        //取倒数第二位校验CRC8
        byte checkBytes = source[source.length -2];
        byte[] sourceCheck = ArrayUtils.subarray(source,3,source.length -2);
        byte crc = CRC8Utils.calcCrc8_E5(sourceCheck);
        if (crc != checkBytes){
            log.warn("=>CRC校验异常,报文={}",ByteBufUtil.hexDump(source));
            throw new ServiceException(MessageUtils.message("modbus.crc.check.abnormal"));
        }
    }

    private void build(){
        if (this.headerSchemaMap == null) {
            this.headerSchemaMap = this.modelManager.getActiveMap(FlowDev.class);
        }
    }
}
