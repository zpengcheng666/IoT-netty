package com.sydh.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sydh.common.core.domain.PageEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;


/**
 * 系统访问记录对象 sys_logininfor
 *
 * @author zhuangpeng.li
 * @date 2024-12-13
 */
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "SysLogininfor", description = "系统访问记录 sys_logininfor")
@Data
@TableName("sys_logininfor" )
public class SysLogininfor extends PageEntity implements Serializable{
    private static final long serialVersionUID=1L;

    /** 访问ID */
    @TableId(value = "info_id", type = IdType.AUTO)
    @ApiModelProperty("访问ID")
    private Long infoId;

    /** 用户账号 */
    @ApiModelProperty("用户账号")
    private String userName;

    /** 登录IP地址 */
    @ApiModelProperty("登录IP地址")
    private String ipaddr;

    /** 登录地点 */
    @ApiModelProperty("登录地点")
    private String loginLocation;

    /** 浏览器类型 */
    @ApiModelProperty("浏览器类型")
    private String browser;

    /** 操作系统 */
    @ApiModelProperty("操作系统")
    private String os;

    /** 登录状态（0成功 1失败） */
    @ApiModelProperty("登录状态")
    private Integer status;

    /** 提示消息 */
    @ApiModelProperty("提示消息")
    private String msg;

    /** 访问时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("访问时间")
    private Date loginTime;

}
