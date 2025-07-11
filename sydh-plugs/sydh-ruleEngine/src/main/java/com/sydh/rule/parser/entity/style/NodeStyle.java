package com.sydh.rule.parser.entity.style;

import lombok.Data;

import java.util.List;

@Data
public class NodeStyle {

    private NodeStyleToolbar toolbar;
    private List<NodeStyleHandles> handles;
    private List<NodeStyleHandles> extendHandles;

}
