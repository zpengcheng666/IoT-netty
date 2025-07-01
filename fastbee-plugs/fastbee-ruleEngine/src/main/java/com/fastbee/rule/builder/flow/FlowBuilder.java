package com.fastbee.rule.builder.flow;

import com.fastbee.rule.parser.entity.FlowData;
import com.fastbee.rule.parser.entity.line.Line;
import com.fastbee.rule.parser.entity.node.Node;

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
