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
 * 通知公告对象 sys_notice
 *
 * @author zhuangpeng.li
 * @date 2024-11-26
 */
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "SysNotice", description = "通知公告 sys_notice")
@Data
@TableName("sys_notice" )
public class SysNotice extends PageEntity implements Serializable{
    private static final long serialVersionUID=1L;

    /** 公告ID */
    @TableId(value = "notice_id", type = IdType.AUTO)
    @ApiModelProperty("公告ID")
    private Long noticeId;

    /** 公告标题 */
    @ApiModelProperty("公告标题")
    private String noticeTitle;

    /** 公告类型（1通知 2公告） */
    @ApiModelProperty("公告类型")
    private String noticeType;

    /** 公告内容 */
    @ApiModelProperty("公告内容")
    private String noticeContent;

    /** 公告状态（0正常 1关闭） */
    @ApiModelProperty("公告状态")
    private Integer status;

    /** 创建者 */
    @ApiModelProperty("创建者")
    private String createBy;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty("创建时间")
    private Date createTime;

    /** 更新者 */
    @ApiModelProperty("更新者")
    private String updateBy;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty("更新时间")
    private Date updateTime;

    /** 备注 */
    @ApiModelProperty("备注")
    private String remark;

}
