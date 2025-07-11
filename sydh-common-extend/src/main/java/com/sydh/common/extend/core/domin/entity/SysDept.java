package com.sydh.common.extend.core.domin.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.sydh.common.core.domain.PageEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * 机构对象 sys_dept
 *
 * @author zhuangpeng.li
 * @date 2024-12-25
 */
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "SysDept", description = "机构 sys_dept")
@Data
@TableName("sys_dept" )
public class SysDept extends PageEntity implements Serializable{
    private static final long serialVersionUID=1L;

    /** 机构id */
    @TableId(value = "dept_id", type = IdType.AUTO)
    @ApiModelProperty("机构id")
    private Long deptId;

    /** 机构系统账号ID */
    @ApiModelProperty("机构系统账号ID")
    private Long deptUserId;

    /** 上级机构id */
    @ApiModelProperty("上级机构id")
    private Long parentId;

    /** 祖级列表 */
    @ApiModelProperty("祖级列表")
    private String ancestors;

    /** 机构名称 */
    @ApiModelProperty("机构名称")
    private String deptName;

    /** 显示顺序 */
    @ApiModelProperty("显示顺序")
    private Integer orderNum;

    /** 联系人 */
    @ApiModelProperty("联系人")
    private String leader;

    /** 联系电话 */
    @ApiModelProperty("联系电话")
    private String phone;

    /** 机构状态（0正常 1停用） */
    @ApiModelProperty("机构状态")
    private Integer status;

    /** 删除标志（0代表存在 2代表删除） */
    @ApiModelProperty("删除标志")
    @TableLogic
    private String delFlag;

    /** 创建者 */
    @ApiModelProperty("创建者")
    private String createBy;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("创建时间")
    private Date createTime;

    /** 更新者 */
    @ApiModelProperty("更新者")
    private String updateBy;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("更新时间")
    private Date updateTime;

    /** 邮箱 */
    @ApiModelProperty("邮箱")
    private String email;

    /** 机构类型 */
    @ApiModelProperty("机构类型")
    private Integer deptType;

    /**
     * 机构logo
     */
    @ApiModelProperty("机构logo")
    private String deptLogo;

    /**
     * logo名称
     */
    @ApiModelProperty("logo名称")
    private String logoName;

    /** 父部门名称 */
    @TableField(exist = false)
    @ApiModelProperty("父部门名称")
    private String parentName;

    /**
     * 管理员姓名
     */
    @TableField(exist = false)
    private String deptUserName;

    /** 子部门 */
    @TableField(exist = false)
    @ApiModelProperty("子部门")
    private List<SysDept> children = new ArrayList<SysDept>();

    /**
     * 是否显示自己
     */
    @TableField(exist = false)
    private Boolean showOwner;

    /**
     * 系统账号名称
     */
    @TableField(exist = false)
    private String userName;

    /**
     * 系统账号密码
     */
    @TableField(exist = false)
    private String password;

    /**
     * 确认密码
     */
    @TableField(exist = false)
    private String confirmPassword;

}
