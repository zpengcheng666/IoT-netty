package com.sydh.rule.parser.entity;

import com.sydh.rule.parser.entity.line.Line;
import com.sydh.rule.parser.entity.node.Node;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlowData {
    private String name;
    private List<Node> nodes;
    private List<Line> lines;

    private List<Double> position;
    private Double zoom;
    private Viewport viewport;
    private Boolean format = false;

    public FlowData(List<Node> nodes, List<Line> lines) {
        this.nodes = nodes;
        this.lines = lines;
    }
}
