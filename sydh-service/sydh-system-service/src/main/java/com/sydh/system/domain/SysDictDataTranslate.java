package com.sydh.system.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sydh.common.core.domain.PageEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;


/**
 * 字典数据翻译对象 sys_dict_data_translate
 *
 * @author gx_ma
 * @date 2025-01-10
 */
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "SysDictDataTranslate", description = "字典数据翻译 sys_dict_data_translate")
@Data
@TableName("sys_dict_data_translate" )
public class SysDictDataTranslate extends PageEntity implements Serializable {
    private static final long serialVersionUID=1L;

    /** ID */
    @ApiModelProperty("ID")
    @TableId(value = "id")
    private Long id;

    /** zh_CN */
    @ApiModelProperty("zh_CN")
    private String zhCn;

    /** en_US */
    @ApiModelProperty("en_US")
    private String enUs;

}
