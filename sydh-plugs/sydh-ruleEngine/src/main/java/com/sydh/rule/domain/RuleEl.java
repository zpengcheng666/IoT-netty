package com.sydh.rule.domain;

import com.sydh.common.core.domain.PageEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Setter;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * 规则el对象 rule_el
 *
 * @author zhuangpeng.li
 * @date 2025-05-08
 */
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "RuleEl", description = "规则el rule_el")
@Data
@TableName("rule_el" )
public class RuleEl extends PageEntity implements Serializable {
    private static final long serialVersionUID=1L;

    /** 租户id */
    @ApiModelProperty("租户id")
    private Long tenantId;

    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty("id")
    private Long id;

    /** EL表达式ID */
    @ApiModelProperty("EL表达式ID")
    private String elId;

    /** EL表达式名称 */
    @ApiModelProperty("EL表达式名称")
    private String elName;

    /** EL表达式 */
    @ApiModelProperty("EL表达式")
    private String el;

    /** 流程数据 */
    @ApiModelProperty("流程数据")
    private String flowJson;

    /** 原始数据 */
    @ApiModelProperty("原始数据")
    private String sourceJson;

    /** Executor执行器ID */
    @ApiModelProperty("Executor执行器ID")
    private Long executorId;

    /** 场景ID */
    @ApiModelProperty("场景ID")
    private Long sceneId;

    /** 是否生效（0-不生效，1-生效） */
    @ApiModelProperty("是否生效")
    private Long enable;

    /** 创建人 */
    @ApiModelProperty("创建人")
    private String createBy;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("创建时间")
    private Date createTime;

    /** 更新人 */
    @ApiModelProperty("更新人")
    private String updateBy;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("更新时间")
    private Date updateTime;

    /** 逻辑删除标识 */
    @ApiModelProperty("逻辑删除标识")
    private Long delFlag;

    /** 备注 */
    @ApiModelProperty("备注")
    private String remark;

    @Setter
    @TableField(exist = false)
    @ApiModelProperty("请求参数")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Map<String, Object> params;

    public Map<String, Object> getParams(){
        if (params == null){
            params = new HashMap<>();
        }
        return params;
    }

}
