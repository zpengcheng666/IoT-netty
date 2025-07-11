package com.sydh.flowdev.codec;

import com.sydh.flowdev.model.FlowDev;
import com.sydh.protocol.WModelManager;
import com.sydh.protocol.base.model.ActiveModel;
import com.sydh.protocol.util.ArrayMap;
import com.sydh.protocol.util.ExplainUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.PooledByteBufAllocator;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author gsb
 * @date 2023/5/17 16:45
 */
@Slf4j
@Component
@NoArgsConstructor
public class FlowDevEncoder {

    private static final ByteBufAllocator ALLOC = PooledByteBufAllocator.DEFAULT;

    @Resource
    private WModelManager modelManager;
    private ArrayMap<ActiveModel> headerSchemaMap;

    public FlowDevEncoder(String...basePackages){
        this.modelManager = new WModelManager(basePackages);
        this.headerSchemaMap = this.modelManager.getActiveMap(FlowDev.class);
    }

    public ByteBuf encode(FlowDev message, ExplainUtils explain){
        this.build();
        ByteBuf buf = ALLOC.buffer();
        ActiveModel activeModel = headerSchemaMap.get(1);
        activeModel.writeTo(buf,message,explain);
        return buf;
    }

    private void build() {
        if (this.headerSchemaMap == null) {
            this.headerSchemaMap = this.modelManager.getActiveMap(FlowDev.class);
        }
    }
}
