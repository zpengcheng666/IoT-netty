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
 * 规则链对象 rule_chain
 *
 * @author zhuangpeng.li
 * @date 2025-05-08
 */
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "RuleChain", description = "规则链 rule_chain")
@Data
@TableName("rule_chain" )
public class RuleChain extends PageEntity implements Serializable {
    private static final long serialVersionUID=1L;

    /** 租户id */
    @ApiModelProperty("租户id")
    private Long tenantId;

    /** id */
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty("id")
    private Long id;

    /**  */
    @ApiModelProperty("")
    private String applicationName;

    /** 链路ID */
    @ApiModelProperty("链路ID")
    private String chainId;

    /**  */
    @ApiModelProperty("")
    private String chainName;

    /**  */
    @ApiModelProperty("")
    private String elData;

    /** 场景状态（1-启动，2-停止） */
    @ApiModelProperty("场景状态")
    private Long enable;

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
