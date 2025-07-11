package com.sydh.rule.parser.entity;

import com.sydh.rule.parser.entity.node.Node;
import lombok.Data;

import java.util.List;

@Data
public class ConditionGraphData {
    Node trigger;
    Node target;
    List<Node> condition;
}
