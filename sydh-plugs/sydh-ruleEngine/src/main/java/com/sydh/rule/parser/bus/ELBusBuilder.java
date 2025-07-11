package com.sydh.rule.parser.bus;

import com.sydh.rule.parser.entity.line.Line;
import com.yomahub.liteflow.builder.el.ELWrapper;

public class ELBusBuilder extends BaseELBus {

    public static void setId(ELWrapper wrapper, Line edge) {
        if(edge != null && edge.getData() != null){
            setId(wrapper, edge.getData().getId());
        }
    }

    public static void setTag(ELWrapper wrapper, Line edge) {
        if(edge != null && edge.getData() != null){
            setTag(wrapper, edge.getData().getTag());
        }
    }
}
