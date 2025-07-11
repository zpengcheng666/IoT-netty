package com.sydh.common.extend.core.domin.entity;

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
import javax.validation.constraints.Size;



/**
 * 字典数据对象 sys_dict_data
 *
 * @author gx_ma
 * @date 2024-12-26
 */
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "SysDictData", description = "字典数据 sys_dict_data")
@Data
@TableName("sys_dict_data" )
public class SysDictData extends BaseEntity {
    private static final long serialVersionUID=1L;

    /** 字典编码 */
    @TableId(value = "dict_code", type = IdType.AUTO)
    @ApiModelProperty("字典编码")
    private Long dictCode;

    /** 字典排序 */
    @ApiModelProperty("字典排序")
    private Long dictSort;

    /** 字典标签 */
    @NotBlank(message = "字典标签不能为空")
    @Size(min = 0, max = 100, message = "字典标签长度不能超过100个字符")
    @ApiModelProperty("字典标签")
    private String dictLabel;

    /** 字典键值 */
    @NotBlank(message = "字典键值不能为空")
    @Size(min = 0, max = 100, message = "字典键值长度不能超过100个字符")
    @ApiModelProperty("字典键值")
    private String dictValue;

    /** 字典类型 */
    @NotBlank(message = "字典类型不能为空")
    @Size(min = 0, max = 100, message = "字典类型长度不能超过100个字符")
    @ApiModelProperty("字典类型")
    private String dictType;

    /** 样式属性（其他样式扩展） */
    @Size(min = 0, max = 100, message = "样式属性长度不能超过100个字符")
    @ApiModelProperty("样式属性")
    private String cssClass;

    /** 表格回显样式 */
    @ApiModelProperty("表格回显样式")
    private String listClass;

    /** 是否默认（Y是 N否） */
    @ApiModelProperty("是否默认")
    private String isDefault;

    /** 状态（0正常 1停用） */
    @ApiModelProperty("状态")
    private Integer status;

    /** 字典标签 */
    @TableField(exist = false)
    @ApiModelProperty("中文字典标签")
    private String dictLabel_zh_CN;

    /** 字典标签 */
    @TableField(exist = false)
    @ApiModelProperty("英文字典标签")
    private String dictLabel_en_US;

    @TableField(exist = false)
    @Deprecated
    private String language;

}
