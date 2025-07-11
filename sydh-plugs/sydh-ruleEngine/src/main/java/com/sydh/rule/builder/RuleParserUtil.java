package com.sydh.rule.builder;


import com.sydh.rule.builder.flow.FlowBuilder;
import com.sydh.rule.parser.entity.FlowData;
import com.sydh.rule.parser.execption.LiteFlowELException;
import com.sydh.rule.parser.graph.Graph;
import com.sydh.rule.parser.graph.GraphInfo;

public class RuleParserUtil {


    public static GraphInfo parserFlow(FlowBuilder builder) throws LiteFlowELException {
        return parserFlow(builder.getFlowData());
    }


    public static GraphInfo parserFlow(String flowJson) throws LiteFlowELException {
        return new Graph(flowJson).toELInfo();
    }

    public static GraphInfo parserFlow(FlowData flowData) throws LiteFlowELException {
        return new Graph(flowData).toELInfo();
    }
}
