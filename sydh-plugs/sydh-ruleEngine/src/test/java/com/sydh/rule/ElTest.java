package com.sydh.rule;

import com.sydh.rule.parser.graph.Graph;
import com.sydh.rule.parser.graph.GraphInfo;
import com.sydh.rule.parser.convert.FlowConvert;
import com.sydh.rule.util.RuleConvertUtil;
import com.sydh.rule.parser2.GraphEL;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ElTest {
    //@Test
    public void jsonToEL() throws Exception {
        String data = "";
        Graph graph = new Graph(data);
        GraphInfo graphInfo = graph.toELInfo();
        String result = graphInfo.toString();
        log.info(result);
    }

    //@Test
    public void jsonToEL2() throws Exception {
        String data = "";
        GraphEL graphEL = new GraphEL(data);
        log.info("GraphEL: {}", graphEL.toEL(true));
    }


    //@Test
    public void elToJson() throws Exception {
        String el = "WHEN(THEN(node(\"b\"),node(\"c\")),node(\"a\"));";
        FlowConvert convert = RuleConvertUtil.convert();
        log.info("elToJson: {}", convert.el2Json(el));
    }

}
