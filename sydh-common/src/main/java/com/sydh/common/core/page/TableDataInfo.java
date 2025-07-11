package com.sydh.common.core.page;

import com.baomidou.mybatisplus.core.metadata.IPage;

import java.io.Serializable;
import java.util.List;


public class TableDataInfo
        implements Serializable {
    private static final long i = 1L;
    private long f;
    private List<?> g;
    private int code;
    private String msg;

    public TableDataInfo() {
    }

    public TableDataInfo(List<?> list, int total) {
        this.g = list;
        this.f = total;
    }


    public static <T> TableDataInfo build(IPage<T> page) {
        TableDataInfo tableDataInfo = new TableDataInfo();
        tableDataInfo.setCode(200);
        tableDataInfo.setMsg("查询成功");
        tableDataInfo.setRows(page.getRecords());
        tableDataInfo.setTotal(page.getTotal());
        return tableDataInfo;
    }


    public static <T> TableDataInfo build(List<T> list) {
        TableDataInfo tableDataInfo = new TableDataInfo();
        tableDataInfo.setCode(200);
        tableDataInfo.setMsg("查询成功");
        tableDataInfo.setRows(list);
        tableDataInfo.setTotal(list.size());
        return tableDataInfo;
    }


    public static <T> TableDataInfo build() {
        TableDataInfo tableDataInfo = new TableDataInfo();
        tableDataInfo.setCode(200);
        tableDataInfo.setMsg("查询成功");
        return tableDataInfo;
    }


    public long getTotal() {
        return this.f;
    }


    public void setTotal(long total) {
        this.f = total;
    }


    public List<?> getRows() {
        return this.g;
    }


    public void setRows(List<?> rows) {
        this.g = rows;
    }


    public int getCode() {
        return this.code;
    }


    public void setCode(int code) {
        this.code = code;
    }


    public String getMsg() {
        return this.msg;
    }


    public void setMsg(String msg) {
        this.msg = msg;
    }
}
