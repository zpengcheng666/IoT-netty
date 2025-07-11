package com.sydh.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sydh.common.core.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * 岗位信息对象 sys_post
 *
 * @author zhuangpeng.li
 * @date 2024-12-13
 */
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "SysPost", description = "岗位信息 sys_post")
@Data
@TableName("sys_post" )
public class SysPost extends BaseEntity {
    private static final long serialVersionUID=1L;

    /** 岗位ID */
    @TableId(value = "post_id", type = IdType.AUTO)
    @ApiModelProperty("岗位ID")
    private Long postId;

    /** 岗位编码 */
    @ApiModelProperty("岗位编码")
    private String postCode;

    /** 岗位名称 */
    @ApiModelProperty("岗位名称")
    private String postName;

    /** 显示顺序 */
    @ApiModelProperty("显示顺序")
    private Integer postSort;

    /** 状态（0正常 1停用） */
    @ApiModelProperty("状态")
    private Integer status;

}
