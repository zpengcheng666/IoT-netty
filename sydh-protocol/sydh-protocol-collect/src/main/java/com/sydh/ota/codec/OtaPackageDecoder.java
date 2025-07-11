package com.sydh.ota.codec;


import com.sydh.ota.model.OtaPackage;
import com.sydh.protocol.WModelManager;
import com.sydh.protocol.base.model.ActiveModel;
import com.sydh.protocol.util.ArrayMap;
import io.netty.buffer.ByteBuf;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@NoArgsConstructor
public class OtaPackageDecoder {

    @Autowired
    private WModelManager modelManager;
    private ArrayMap<ActiveModel> headerSchemaMap;

    public OtaPackageDecoder(String...basePackages) {
        this.modelManager = new WModelManager(basePackages);
        this.headerSchemaMap = this.modelManager.getActiveMap(OtaPackage.class);
    }

    public OtaPackage decode(ByteBuf in){
        this.build();
        ByteBuf copy = in.duplicate();
        byte[] bytes = new byte[in.writerIndex()];
        copy.readBytes(bytes);
        // 截掉校验位
        ByteBuf inSub = in.slice(0, in.readableBytes() - 1);
        ActiveModel<OtaPackage> activeModel = headerSchemaMap.get(0);
        OtaPackage message = new OtaPackage();
        message.setPayload(inSub);
        activeModel.mergeFrom(inSub,message,null);
        log.info("=>解析:[{}]",message);
        return message;
    }


    private void build(){
        if (this.headerSchemaMap == null) {
            this.headerSchemaMap = this.modelManager.getActiveMap(OtaPackage.class);
        }
    }

}
