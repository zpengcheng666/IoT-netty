package com.sydh.iot.model.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sydh.common.annotation.Excel;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;


/**
 * 数据桥接对象 bridge
 *
 * @author zhuangpeng.li
 * @date 2024-11-12
 */

@ApiModel(value = "BridgeVO", description = "数据桥接 bridge")
@Data
public class BridgeVO{

    /** id唯一标识 */
    @Excel(name = "id唯一标识")
    @ApiModelProperty("id唯一标识")
    private Long id;

    /** 桥接配置信息 */
    @Excel(name = "桥接配置信息")
    @ApiModelProperty("桥接配置信息")
    private String configJson;

    /** 连接器名称 */
    @Excel(name = "连接器名称")
    @ApiModelProperty("连接器名称")
    private String name;

    // -------------------------- 是否生效（enable） --------------------------
    /** 是否生效（0-不生效，1-生效）- 原始字段 */
    @ApiModelProperty("是否生效（0-不生效，1-生效）")
    private String enable; // 原始字段（不添加@Excel）

    /** 是否生效中文描述 - Excel导出专用 */
    @Excel(name = "是否生效")
    @JsonIgnore
    private String enableDesc;

    public void setEnable(String enable) {
        this.enable = enable;
        // 同步转换中文
        this.enableDesc = "1".equals(enable) ? "生效" : "不生效";
    }

    public String getEnableDesc() {
        return enableDesc;
    }

    // -------------------------- 状态（status） --------------------------
    /** 状态（0-未连接，1-连接中）- 原始字段 */
    @ApiModelProperty("状态（0-未连接，1-连接中）")
    private Integer status; // 原始字段（不添加@Excel）

    /** 状态中文描述 - Excel导出专用 */
    @Excel(name = "状态")
    @JsonIgnore
    private String statusDesc;

    public void setStatus(Integer status) {
        this.status = status;
        // 同步转换中文
        this.statusDesc = status == 1 ? "连接中" : "未连接";
    }

    public String getStatusDesc() {
        return statusDesc;
    }

    // -------------------------- 桥接类型（type） --------------------------
    /** 桥接类型(3=Http推送，4=Mqtt桥接，5=数据库存储) - 原始字段 */
    @ApiModelProperty("桥接类型(3=Http推送，4=Mqtt桥接，5=数据库存储)")
    private Integer type; // 原始字段（不添加@Excel）

    /** 桥接类型中文描述 - Excel导出专用 */
    @Excel(name = "桥接类型")
    @JsonIgnore
    private String typeDesc;

    public void setType(Integer type) {
        this.type = type;
        // 同步转换中文
        switch (type) {
            case 3: this.typeDesc = "Http推送"; break;
            case 4: this.typeDesc = "Mqtt桥接"; break;
            case 5: this.typeDesc = "数据库存储"; break;
            default: this.typeDesc = "未知类型";
        }
    }

    public String getTypeDesc() {
        return typeDesc;
    }

    // -------------------------- 桥接方向（direction） --------------------------
    /** 桥接方向(1=输入，2=输出) - 原始字段 */
    @ApiModelProperty("桥接方向(1=输入，2=输出)")
    private Integer direction; // 原始字段（不添加@Excel）

    /** 桥接方向中文描述 - Excel导出专用 */
    @Excel(name = "桥接方向")
    @JsonIgnore
    private String directionDesc;

    public void setDirection(Integer direction) {
        this.direction = direction;
        // 同步转换中文
        this.directionDesc = direction == 1 ? "输入" : "输出";
    }

    public String getDirectionDesc() {
        return directionDesc;
    }

    // -------------------------- 删除标志（delFlag） --------------------------
    /** 删除标志（0代表存在 2代表删除）- 原始字段 */
    @ApiModelProperty("删除标志（0代表存在 2代表删除）")
    private String delFlag; // 原始字段（不添加@Excel）

    /** 删除标志中文描述 - Excel导出专用 */
    @Excel(name = "删除标志")
    @JsonIgnore
    private String delFlagDesc;

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
        // 同步转换中文
        this.delFlagDesc = "0".equals(delFlag) ? "存在" : "删除";
    }

    public String getDelFlagDesc() {
        return delFlagDesc;
    }

    // -------------------------- 其他无需转换的字段 --------------------------
    /** 转发路由 */
    @ApiModelProperty("转发路由")
    @Excel(name = "转发路由")
    private String route;

    /** 创建者 */
    @Excel(name = "创建者")
    @ApiModelProperty("创建者")
    private String createBy;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("创建时间")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /** 更新者 */
    @Excel(name = "更新者")
    @ApiModelProperty("更新者")
    private String updateBy;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("更新时间")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    /** 备注 */
    @Excel(name = "备注")
    @ApiModelProperty("备注")
    private String remark;

    /** 租户id */
    @Excel(name = "租户id")
    @ApiModelProperty("租户id")
    private Long tenantId;

    /** 租户名称 */
    @Excel(name = "租户名称")
    @ApiModelProperty("租户名称")
    private String tenantName;
}
