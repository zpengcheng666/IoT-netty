package com.sydh.rule.cmp.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConditionData {
    private String modelId;
    private List<Expression> expressions;

    @Data
    public static class Expression {
        private String from;
        private String to;
        private String operator;
        private String value;
    }
}
