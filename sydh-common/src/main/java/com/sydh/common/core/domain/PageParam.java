package com.sydh.common.core.domain;


import java.io.Serializable;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class PageParam implements Serializable {
    private static final Integer PAGE_NO = 1;
    private static final Integer PAGE_SIZE = 10;
    private @NotNull(
            message = "页码不能为空"
    ) @Min(
            value = 1L,
            message = "页码最小值为 1"
    ) Integer pageNo;
    private @NotNull(
            message = "每页条数不能为空"
    ) @Min(
            value = 1L,
            message = "每页条数最小值为 1"
    ) @Max(
            value = 100L,
            message = "每页条数最大值为 100"
    ) Integer pageSize;

    public PageParam() {
        this.pageNo = PAGE_NO;
        this.pageSize = PAGE_SIZE;
    }

    public Integer getPageNo() {
        return this.pageNo;
    }

    public Integer getPageSize() {
        return this.pageSize;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof PageParam)) {
            return false;
        } else {
            PageParam var2 = (PageParam)o;
            if (!var2.canEqual(this)) {
                return false;
            } else {
                Integer var3 = this.getPageNo();
                Integer var4 = var2.getPageNo();
                if (var3 == null) {
                    if (var4 != null) {
                        return false;
                    }
                } else if (!var3.equals(var4)) {
                    return false;
                }

                Integer var5 = this.getPageSize();
                Integer var6 = var2.getPageSize();
                if (var5 == null) {
                    if (var6 != null) {
                        return false;
                    }
                } else if (!var5.equals(var6)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(Object other) {
        return other instanceof PageParam;
    }

    public int hashCode() {
        boolean var1 = true;
        int var2 = 1;
        Integer var3 = this.getPageNo();
        var2 = var2 * 59 + (var3 == null ? 43 : var3.hashCode());
        Integer var4 = this.getPageSize();
        var2 = var2 * 59 + (var4 == null ? 43 : var4.hashCode());
        return var2;
    }

    public String toString() {
        return "PageParam(pageNo=" + this.getPageNo() + ", pageSize=" + this.getPageSize() + ")";
    }
}
