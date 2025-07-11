package com.sydh.iot.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sydh.common.core.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;


/**
 * 产品分类对象 iot_category
 *
 * @author zhuangpeng.li
 * @date 2024-11-12
 */

@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "Category", description = "产品分类 iot_category")
@Data
@TableName("iot_category" )
public class Category extends BaseEntity implements Serializable{
    private static final long serialVersionUID=1L;

    /** 产品分类ID */
    @TableId(value = "category_id", type = IdType.AUTO)
    @ApiModelProperty("产品分类ID")
    private Long categoryId;

    /** 产品分类名称 */
    @ApiModelProperty("产品分类名称")
    private String categoryName;

    /** 租户ID */
    @ApiModelProperty("租户ID")
    private Long tenantId;

    /** 租户名称 */
    @ApiModelProperty("租户名称")
    private String tenantName;

    /** 是否系统通用（0-否，1-是） */
    @ApiModelProperty("是否系统通用")
    private Integer isSys;

    /** 父级ID */
    @ApiModelProperty("父级ID")
    private Long parentId;

    /** 显示顺序 */
    @ApiModelProperty("显示顺序")
    private Integer orderNum;

    /** 删除标志（0代表存在 2代表删除） */
    @ApiModelProperty("删除标志")
    @TableLogic
    private String delFlag;

}
