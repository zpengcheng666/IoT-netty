package com.sydh.common.filter;

import com.alibaba.fastjson2.filter.SimplePropertyPreFilter;


public class PropertyPreExcludeFilter
        extends SimplePropertyPreFilter {
    public PropertyPreExcludeFilter() {
        super(new String[0]);
    }


    public PropertyPreExcludeFilter addExcludes(String... filters) {
        for (byte b = 0; b < filters.length; b++) {
            getExcludes().add(filters[b]);
        }
        return this;
    }
}
