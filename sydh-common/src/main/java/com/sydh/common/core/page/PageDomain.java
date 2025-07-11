package com.sydh.common.core.page;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sydh.common.exception.ServiceException;
import com.sydh.common.utils.StringUtils;
import com.sydh.common.utils.sql.SqlUtil;

import java.util.ArrayList;
import java.util.List;


public class PageDomain {
    private Integer pageNum;
    private Integer pageSize;
    private String b;
    private String c = "asc";


    private Boolean d = Boolean.valueOf(true);


    public static final int DEFAULT_PAGE_NUM = 1;


    public static final int DEFAULT_PAGE_SIZE = 2147483647;


    public String getOrderBy() {
        if (StringUtils.isEmpty(this.b)) {
            return "";
        }
        return StringUtils.toUnderScoreCase(this.b) + " " + this.c;
    }


    public <T> Page<T> build() {
        Integer integer1 = (Integer) ObjectUtil.defaultIfNull(getPageNum(), Integer.valueOf(1));
        Integer integer2 = (Integer) ObjectUtil.defaultIfNull(getPageSize(), Integer.valueOf(2147483647));
        if (integer1.intValue() <= 0) {
            integer1 = Integer.valueOf(1);
        }
        Page<T> page = new Page(integer1.intValue(), integer2.intValue());
        List<OrderItem> list = a();
        if (CollUtil.isNotEmpty(list)) {
            page.addOrder(list);
        }
        return page;
    }


    private List<OrderItem> a() {
        if (StringUtils.isBlank(this.b) || StringUtils.isBlank(this.c)) {
            return null;
        }
        String str = SqlUtil.escapeOrderBySql(this.b);
        str = StringUtils.toUnderScoreCase(str);


        this.c = StringUtils.replaceEach(this.c, new String[]{"ascending", "descending"}, new String[]{"asc", "desc"});

        String[] arrayOfString1 = str.split("/");
        String[] arrayOfString2 = this.c.split("/");
        if (arrayOfString2.length != 1 && arrayOfString2.length != arrayOfString1.length) {
            throw new ServiceException("排序参数有误");
        }

        ArrayList<OrderItem> arrayList = new ArrayList();

        for (byte b = 0; b < arrayOfString1.length; b++) {
            String str1 = arrayOfString1[b];
            String str2 = (arrayOfString2.length == 1) ? arrayOfString2[0] : arrayOfString2[b];
            if ("asc".equals(str2)) {
                arrayList.add(OrderItem.asc(str1));
            } else if ("desc".equals(str2)) {
                arrayList.add(OrderItem.desc(str1));
            } else {
                throw new ServiceException("排序参数有误");
            }
        }
        return arrayList;
    }


    public Integer getPageNum() {
        return this.pageNum;
    }


    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }


    public Integer getPageSize() {
        return this.pageSize;
    }


    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }


    public String getOrderByColumn() {
        return this.b;
    }


    public void setOrderByColumn(String orderByColumn) {
        this.b = orderByColumn;
    }


    public String getIsAsc() {
        return this.c;
    }


    public void setIsAsc(String isAsc) {
        if (StringUtils.isNotEmpty(isAsc)) {


            if ("ascending".equals(isAsc)) {

                isAsc = "asc";
            } else if ("descending".equals(isAsc)) {

                isAsc = "desc";
            }
            this.c = isAsc;
        }
    }


    public Boolean getReasonable() {
        if (StringUtils.isNull(this.d)) {
            return Boolean.TRUE;
        }
        return this.d;
    }


    public void setReasonable(Boolean reasonable) {
        this.d = reasonable;
    }
}
