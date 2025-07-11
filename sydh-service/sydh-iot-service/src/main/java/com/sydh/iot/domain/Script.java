package com.sydh.iot.domain;

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
 * 规则引擎脚本对象 iot_script
 *
 * @author zhuangpeng.li
 * @date 2024-11-14
 */

@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "Script", description = "规则引擎脚本 iot_script")
@Data
@TableName("iot_script" )
public class Script extends BaseEntity implements Serializable{
    private static final long serialVersionUID=1L;

    /** 脚本ID */
    @TableId(value = "script_id")
    @ApiModelProperty("脚本ID")
    private String scriptId;

    /** 用户ID */
    @ApiModelProperty("用户ID")
    private Long userId;

    /** 用户昵称 */
    @ApiModelProperty("用户昵称")
    private String userName;

    /** 关联场景ID */
    @ApiModelProperty("关联场景ID")
    private Long sceneId;

    /** 产品ID */
    @ApiModelProperty("产品ID")
    private Long productId;

    /** 产品名称 */
    @ApiModelProperty("产品名称")
    private String productName;

    /** 脚本事件(1=设备上报，2=平台下发，3=设备上线，4=设备离线) */
    @ApiModelProperty("脚本事件(1=设备上报，2=平台下发，3=设备上线，4=设备离线)")
    private Integer scriptEvent;

    /** 脚本动作(1=消息重发，2=消息通知，3=Http推送，4=Mqtt桥接，5=数据库存储) */
    @ApiModelProperty("脚本动作(1=消息重发，2=消息通知，3=Http推送，4=Mqtt桥接，5=数据库存储)")
    private Integer scriptAction;

    /** 脚本用途(1=数据流，2=触发器，3=执行动作) */
    @ApiModelProperty("脚本用途(1=数据流，2=触发器，3=执行动作)")
    private Integer scriptPurpose;

    /** 脚本执行顺序，值越大优先级越高 */
    @ApiModelProperty("脚本执行顺序，值越大优先级越高")
    private Integer scriptOrder;

    /** 应用名，后端、规则和脚本要统一 */
    @ApiModelProperty("应用名，后端、规则和脚本要统一")
    private String applicationName;

    /** 脚本名 */
    @ApiModelProperty("脚本名")
    private String scriptName;

    /** 脚本数据 */
    @ApiModelProperty("脚本数据")
    private String scriptData;

    /** 脚本类型：
     script=普通脚本，
     switch_script=选择脚本，
     if_script=条件脚本，
     for_script=数量循环脚本，
     while_script=条件循环，
     break_script=退出循环脚本 */
    @ApiModelProperty("脚本类型")
    private String scriptType;

    /** 脚本语言（groovy | qlexpress | js | python | lua | aviator | java） */
    @ApiModelProperty("脚本语言")
    private String scriptLanguage;

    /** 是否生效（0-不生效，1-生效） */
    @ApiModelProperty("是否生效")
    private Integer enable;

    /** 删除标志（0代表存在 2代表删除） */
    @ApiModelProperty("删除标志")
    @TableLogic
    private String delFlag;

}
