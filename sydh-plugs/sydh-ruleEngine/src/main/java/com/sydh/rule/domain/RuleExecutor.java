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
 * 规则执行器对象 rule_executor
 *
 * @author zhuangpeng.li
 * @date 2025-05-08
 */
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "RuleExecutor", description = "规则执行器 rule_executor")
@Data
@TableName("rule_executor" )
public class RuleExecutor extends PageEntity implements Serializable {
    private static final long serialVersionUID=1L;

    /** 租户id */
    @ApiModelProperty("租户id")
    private Long tenantId;

    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty("id")
    private Long id;

    /** 执行器ID */
    @ApiModelProperty("执行器ID")
    private String executorId;

    /** 执行器名称 */
    @ApiModelProperty("执行器名称")
    private String executorName;

    /** 执行器配置IvyConfig */
    @ApiModelProperty("执行器配置IvyConfig")
    private Long ivyConfigId;

    /** 执行器类型【execute2Resp:execute2Resp,execute2Future:execute2Future】 */
    @ApiModelProperty("执行器类型【execute2Resp:execute2Resp,execute2Future:execute2Future】")
    private String executorType;

    /** 上下文bean */
    @ApiModelProperty("上下文bean")
    private String contextBeans;

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
