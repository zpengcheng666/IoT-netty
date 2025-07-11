package com.sydh.rule.parser.entity.node;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NodeDataContext {

    //别名
    private String asName;
    //bean
    private String fullClassName;
    //class
    private Class<?> clazz;
    //参数
    private Map<String,Object> params;

}
