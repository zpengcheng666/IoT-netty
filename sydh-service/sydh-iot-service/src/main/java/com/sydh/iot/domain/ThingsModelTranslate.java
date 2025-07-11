package com.sydh.iot.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sydh.common.core.domain.PageEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;


/**
 * 物模型翻译对象 iot_things_model_translate
 *
 * @author zhuangpeng.li
 * @date 2024-12-24
 */
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "ThingsModelTranslate", description = "物模型翻译 iot_things_model_translate")
@Data
@TableName("iot_things_model_translate" )
public class ThingsModelTranslate extends PageEntity implements Serializable{
    private static final long serialVersionUID=1L;

    /** ID */
    @TableId(value = "id")
    @ApiModelProperty("ID")
    private Long id;

    /** zh_CN */
    @ApiModelProperty("zh_CN")
    private String zhCn;

    /** en_US */
    @ApiModelProperty("en_US")
    private String enUs;

    /** 产品ID */
    @ApiModelProperty("产品ID")
    private Long productId;

}
