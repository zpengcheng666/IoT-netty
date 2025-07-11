package com.sydh.rule.parser.bus;

import com.sydh.rule.parser.entity.node.Node;
import com.yomahub.liteflow.builder.el.AndELWrapper;
import com.yomahub.liteflow.builder.el.ELBus;

public class ELBusAnd extends BaseELBus {

    public static AndELWrapper node(Object... objects){
        return ELBus.and(objects);
    }

    public static AndELWrapper node(){
        return ELBus.and();
    }

    public static AndELWrapper node(Node node){
        AndELWrapper wrapper = ELBus.and();
        setId(wrapper, node);
        setTag(wrapper, node);
//        setMaxWaitSeconds(wrapper, node);
        return wrapper;
    }

}
