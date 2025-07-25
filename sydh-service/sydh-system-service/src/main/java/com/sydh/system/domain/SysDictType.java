package com.sydh.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sydh.common.core.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;


/**
 * 字典类型对象 sys_dict_type
 *
 * @author gx_ma
 * @date 2024-12-26
 */
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "SysDictType", description = "字典类型 sys_dict_type")
@Data
@TableName("sys_dict_type" )
public class SysDictType extends BaseEntity {
    private static final long serialVersionUID=1L;

    /** 字典主键 */
    @TableId(value = "dict_id", type = IdType.AUTO)
    @ApiModelProperty("字典主键")
    private Long dictId;

    /** 字典名称 */
    @NotBlank(message = "字典名称不能为空")
    @Size(min = 0, max = 100, message = "字典类型名称长度不能超过100个字符")
    @ApiModelProperty("字典名称")
    private String dictName;

    /** 字典类型 */
    @NotBlank(message = "字典类型不能为空")
    @Size(min = 0, max = 100, message = "字典类型类型长度不能超过100个字符")
    @Pattern(regexp = "^[a-z][a-z0-9_]*$", message = "字典类型必须以字母开头，且只能为（小写字母，数字，下滑线）")
    @ApiModelProperty("字典类型")
    private String dictType;

    /** 状态（0正常 1停用） */
    @ApiModelProperty("状态")
    private Integer status;

    /** 字典类型标签 */
    @ApiModelProperty("中文字典标签")
    @TableField(exist = false)
    private String dictName_zh_CN;

    /** 字典类型标签 */
    @ApiModelProperty("英文字典标签")
    @TableField(exist = false)
    private String dictName_en_US;

    @TableField(exist = false)
    @Deprecated
    private String language;

}
