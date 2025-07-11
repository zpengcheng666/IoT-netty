package com.sydh.rule.parser.bus;

import com.sydh.rule.parser.entity.node.Node;
import com.sydh.rule.parser.execption.LiteFlowELException;
import com.yomahub.liteflow.builder.el.ELBus;
import com.yomahub.liteflow.builder.el.NodeELWrapper;

public class ELBusNode extends BaseELBus {

    public static NodeELWrapper node(String nodeId) {
        return ELBus.node(nodeId);
    }

    public static NodeELWrapper node(Node node) throws LiteFlowELException {
        NodeELWrapper wrapper = ELBus.node(node.getType());

        // 设置节点参数 node-> nodeData
        setId(wrapper, node);
        setTag(wrapper, node);

        // 设置节点属性
        setData(wrapper,  node.getId(), node.getData());
        // setData(wrapper, node);
        // setMaxWaitSeconds(wrapper, node);
        // setRetry(wrapper, node);
        return wrapper;
    }

}
