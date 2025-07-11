package com.sydh.common.core.page;

import com.sydh.common.core.text.Convert;
import com.sydh.common.utils.ServletUtils;


public class TableSupport {
    public static final String PAGE_NUM = "pageNum";
    public static final String PAGE_SIZE = "pageSize";
    public static final String ORDER_BY_COLUMN = "orderByColumn";
    public static final String IS_ASC = "isAsc";
    public static final String REASONABLE = "reasonable";

    public static PageDomain getPageDomain() {
        PageDomain pageDomain = new PageDomain();
        pageDomain.setPageNum(Convert.toInt(ServletUtils.getParameter("pageNum"), Integer.valueOf(1)));
        pageDomain.setPageSize(Convert.toInt(ServletUtils.getParameter("pageSize"), Integer.valueOf(10)));
        pageDomain.setOrderByColumn(ServletUtils.getParameter("orderByColumn"));
        pageDomain.setIsAsc(ServletUtils.getParameter("isAsc"));
        pageDomain.setReasonable(ServletUtils.getParameterToBool("reasonable"));
        return pageDomain;
    }


    public static PageDomain buildPageRequest() {
        return getPageDomain();
    }
}
