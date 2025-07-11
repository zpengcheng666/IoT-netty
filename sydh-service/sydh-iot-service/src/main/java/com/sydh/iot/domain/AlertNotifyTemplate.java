package com.sydh.iot.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;


/**
 * 告警通知模版关联对象 iot_alert_notify_template
 *
 * @author zhuangpeng.li
 * @date 2024-11-12
 */

@ApiModel(value = "AlertNotifyTemplate", description = "告警通知模版关联 iot_alert_notify_template")
@Data
@TableName("iot_alert_notify_template" )
public class AlertNotifyTemplate implements Serializable{
    private static final long serialVersionUID=1L;

    /** 告警id */
    @TableId(value = "alert_id", type = IdType.AUTO)
    @ApiModelProperty("告警id")
    private Long alertId;

    /** 通知模版id */
    @ApiModelProperty("通知模版id")
    private Long notifyTemplateId;

    @Setter
    @TableField(exist = false)
    @ApiModelProperty("请求参数")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Map<String, Object> params;

    public Map<String, Object> getParams(){
        if (params == null){
            params = new HashMap<>();
        }
        return params;
    }

}
