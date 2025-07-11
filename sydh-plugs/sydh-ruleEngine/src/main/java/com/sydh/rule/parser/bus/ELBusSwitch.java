package com.sydh.rule.parser.bus;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.sydh.rule.parser.entity.line.Line;
import com.sydh.rule.parser.entity.node.Node;
import com.sydh.rule.parser.entity.node.NodeData;
import com.sydh.rule.parser.entity.node.NodeDataSwitch;
import com.sydh.rule.parser.enums.RuleEnums;
import com.sydh.rule.parser.execption.LiteFlowELException;
import com.sydh.rule.parser2.GraphEL;
import com.yomahub.liteflow.builder.el.ELBus;
import com.yomahub.liteflow.builder.el.ELWrapper;
import com.yomahub.liteflow.builder.el.SwitchELWrapper;

import java.util.List;

public class ELBusSwitch extends BaseELBus {

    public static SwitchELWrapper node(String node){
        SwitchELWrapper switchELWrapper = ELBus.switchOpt(ELBusNode.node(node));
        return switchELWrapper;
    }

    public static SwitchELWrapper node(Node node, GraphEL graphEL) throws LiteFlowELException {
        return node(node, graphEL.findSpecialJoinNode(node), graphEL);
    }

    public static SwitchELWrapper node(Node node, Node specialJoinNode, GraphEL graphEL) throws LiteFlowELException {
        //获取特殊路径聚合点
        //Node specialJoinNode = graphEL.findSpecialJoinNode(node);
        //获取特殊路径聚合点及其后面所有节点
        List<Node> specialJoinNodeList = null;
        if(specialJoinNode != null){
            specialJoinNodeList = graphEL.path2List(graphEL.findAllPaths(specialJoinNode));
        }

        SwitchELWrapper switchELWrapper = node(node);
        //获取 to 节点集合
        //List<Node> commonNodeList = graphEL.getSpecialPathNodes(node, IvyEnums.PATH_ENUM.common_path);
        List<Node> toNodeList = graphEL.getSpecialPathNodes(node, RuleEnums.PATH_ENUM.to_path);
        //移除特殊路径聚合点及其后的所有节点
        if(CollUtil.isNotEmpty(specialJoinNodeList)){
            toNodeList.removeAll(specialJoinNodeList);
        }
        GraphEL toGraph = new GraphEL(toNodeList, graphEL.getLines(toNodeList));
        List<List<List<Node>>> groupFilterPaths = toGraph.getGroupPaths();
        for (List<List<Node>> groupFilterPath : groupFilterPaths){
            List<Node> nodeList = graphEL.path2List(groupFilterPath);
            GraphEL toItemGraph = new GraphEL(nodeList, graphEL.getLines(nodeList));
            ELWrapper wrapper = toItemGraph.toELWrapper();
            Line edge = graphEL.getLine(node, toItemGraph.getStartNodes().get(0));
            if(nodeList.size() == 1 && StrUtil.isBlank(edge.getData().getId()) && StrUtil.isBlank(edge.getData().getTag())){
                switchELWrapper.to(ELBusNode.node(nodeList.get(0)));
            }else{
                if(edge != null){
                    wrapper.id(edge.getData().getId());
                    wrapper.tag(edge.getData().getTag());
                }
                switchELWrapper.to(wrapper);
            }
        }

        //获取 default 节点集合
        List<Node> defaultNodeList = graphEL.getSpecialPathNodes(node, RuleEnums.PATH_ENUM.default_path);
        if(CollUtil.isNotEmpty(specialJoinNodeList)){
            defaultNodeList.removeAll(specialJoinNodeList);
        }
        if(CollUtil.isNotEmpty(defaultNodeList)){
            if(defaultNodeList.size() == 1){
                switchELWrapper.defaultOpt(ELBusNode.node(defaultNodeList.get(0)));
            }else{
                GraphEL defaultGraph = new GraphEL(defaultNodeList, graphEL.getLines(defaultNodeList));
                switchELWrapper.defaultOpt(defaultGraph.toELWrapper());
            }
        }
        return switchELWrapper;
    }

    public static SwitchELWrapper node(Node node) throws LiteFlowELException {
        SwitchELWrapper wrapper = ELBus.switchOpt(ELBusNode.node(node));
        NodeData data = node.getNodeData();
        NodeDataSwitch nodeDataSwitch = data.getNodeDataSwitch();
        if(nodeDataSwitch != null){
            String nodeDataSwitchTo = nodeDataSwitch.getToOpt();
            if(StrUtil.isNotBlank(nodeDataSwitchTo)){
                if(nodeDataSwitchTo.contains(",")){
                    Object[] toArray = nodeDataSwitchTo.split(",");
                    wrapper.to(toArray);
                }else{
                    wrapper.to(nodeDataSwitchTo);
                }
            }
            if(StrUtil.isNotBlank(nodeDataSwitch.getDefaultOpt())) {
                wrapper.defaultOpt(nodeDataSwitch.getDefaultOpt());
            }
        }
        return wrapper;
    }

}
