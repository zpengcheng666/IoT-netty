package com.fastbee.rule.parser.graph;

import com.fastbee.rule.parser.entity.node.Node;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FlowInfo {

    private Node node;

    private Boolean isBreak;
    private Boolean isContinue;

}
