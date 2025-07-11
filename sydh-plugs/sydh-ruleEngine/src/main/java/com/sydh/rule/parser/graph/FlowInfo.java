package com.sydh.rule.parser.graph;

import com.sydh.rule.parser.entity.node.Node;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FlowInfo {

    private Node node;

    private Boolean isBreak;
    private Boolean isContinue;

}
