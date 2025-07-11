package com.sydh.common.mybatis.utils;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sydh.common.core.domain.PageParam;
import com.sydh.common.core.domain.SortingField;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;


public class MyBatisUtils {
    private static final String aj = "`";

    public static <T> Page<T> buildPage(PageParam pageParam) {
        return buildPage(pageParam, null);
    }


    public static <T> Page<T> buildPage(PageParam pageParam, Collection<SortingField> sortingFields) {
        Page<T> page = new Page(pageParam.getPageNo().intValue(), pageParam.getPageSize().intValue());

        if (!CollectionUtil.isEmpty(sortingFields)) {
            page.addOrder((List) sortingFields.stream().map(paramSortingField -> "asc".equals(paramSortingField.getOrder()) ? OrderItem.asc(paramSortingField.getField()) : OrderItem.desc(paramSortingField.getField()))

                    .collect(Collectors.toList()));
        }
        return page;
    }


    public static void addInterceptor(MybatisPlusInterceptor interceptor, InnerInterceptor inner, int index) {
        ArrayList<InnerInterceptor> arrayList = new ArrayList(interceptor.getInterceptors());
        arrayList.add(index, inner);
        interceptor.setInterceptors(arrayList);
    }


    public static String getTableName(Table table) {
        String str = table.getName();
        if (str.startsWith("`") && str.endsWith("`")) {
            str = str.substring(1, str.length() - 1);
        }
        return str;
    }


    public static Column buildColumn(String tableName, Alias tableAlias, String column) {
        if (tableAlias != null) {
            tableName = tableAlias.getName();
        }
        return new Column(tableName + "." + column);
    }
}
