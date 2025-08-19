package com.sydh.iot.model.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sydh.common.annotation.Excel;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;


/**
 * 产品授权码对象 iot_product_authorize
 *
 * @author zhuangpeng.li
 * @date 2024-11-13
 */

@ApiModel(value = "ProductAuthorizeVO", description = "产品授权码 iot_product_authorize")
@Data
public class ProductAuthorizeVO {

    /** 授权码ID */
    @Excel(name = "授权码ID")
    @ApiModelProperty("授权码ID")
    private Long authorizeId;

    /** 授权码 */
    @Excel(name = "授权码")
    @ApiModelProperty("授权码")
    private String authorizeCode;

    /** 产品ID */
    @Excel(name = "产品ID")
    @ApiModelProperty("产品ID")
    private Long productId;

    /** 设备ID */
    @Excel(name = "设备ID")
    @ApiModelProperty("设备ID")
    private Long deviceId;

    /** 设备编号 */
    @Excel(name = "设备编号")
    @ApiModelProperty("设备编号")
    private String serialNumber;

    /** 用户ID */
    @Excel(name = "用户ID")
    @ApiModelProperty("用户ID")
    private Long userId;

    /** 用户名称 */
    @Excel(name = "用户名称")
    @ApiModelProperty("用户名称")
    private String userName;

    /** 状态（1-未使用，2-使用中） */
    @ApiModelProperty("状态")
    //@Excel(name = "状态")
    private Integer status;

    /** 状态中文描述 - Excel导出专用 */
    @Excel(name = "状态")
    @JsonIgnore
    private String statusDesc;

    public void setStatus(Integer status) {
        this.status = status;
        // 同步转换中文
        this.statusDesc = status == 2 ? "已使用" : "未使用";
    }

    public String getStatusDesc() {
        return statusDesc;
    }
    /** 创建数量 */
    @ApiModelProperty("创建数量")
    @Excel(name = "创建数量")
    private Integer createNum;

    /** 删除标志（0代表存在 2代表删除） */
    @ApiModelProperty("删除标志")
    @Excel(name = "删除标志")
    private String delFlag;

    /** 创建者 */
    @Excel(name = "创建者")
    @ApiModelProperty("创建者")
    private String createBy;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("创建时间")
    @Excel(name = "创建时间")
    private Date createTime;

    /** 更新者 */
    @Excel(name = "更新者")
    @ApiModelProperty("更新者")
    private String updateBy;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("更新时间")
    @Excel(name = "更新时间")
    private Date updateTime;

    /** 备注 */
    @Excel(name = "备注")
    @ApiModelProperty("备注")
    private String remark;
}
