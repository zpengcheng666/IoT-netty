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
 * 设备分组对象 iot_group
 *
 * @author zhuangpeng.li
 * @date 2024-11-13
 */

@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "Group", description = "设备分组 iot_group")
@Data
@TableName("iot_group")
public class Group extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 分组ID
     */
    @TableId(value = "group_id", type = IdType.AUTO)
    @ApiModelProperty("分组ID")
    private Long groupId;

    /**
     * 分组名称
     */
    @ApiModelProperty("分组名称")
    private String groupName;

    /**
     * 分组排序
     */
    @ApiModelProperty("分组排序")
    private Long groupOrder;

    /**
     * 用户ID
     */
    @ApiModelProperty("用户ID")
    private Long userId;

    /**
     * 用户昵称
     */
    @ApiModelProperty("用户昵称")
    private String userName;

    /**
     * 删除标志（0代表存在 2代表删除）
     */
    @ApiModelProperty("删除标志")
    @TableLogic
    private String delFlag;

}
