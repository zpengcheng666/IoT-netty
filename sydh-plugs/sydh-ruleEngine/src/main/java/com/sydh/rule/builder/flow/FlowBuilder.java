package com.sydh.rule.builder.flow;

import com.sydh.rule.parser.entity.FlowData;
import com.sydh.rule.parser.entity.line.Line;
import com.sydh.rule.parser.entity.node.Node;

import java.util.List;

public interface FlowBuilder {


    FlowBuilder defaultData();

    FlowBuilder addNode(Node... nodes);

    FlowBuilder addLine(Line... edges);

    FlowBuilder format(boolean format);

    String build();

    FlowData getFlowData();
    List<Node> getNodes();
    List<Line> getLines();
    Node getNodeByCmpId(String cmpId);
    Node getNodeById(String id);

}
