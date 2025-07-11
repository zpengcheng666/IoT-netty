package com.sydh.iot.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;


/**
 * 告警场景对象 iot_alert_scene
 *
 * @author zhuangpeng.li
 * @date 2024-11-12
 */

@ApiModel(value = "AlertScene", description = "告警场景 iot_alert_scene")
@Data
@TableName("iot_alert_scene" )
public class AlertScene implements Serializable{
    private static final long serialVersionUID=1L;

    /** 告警ID */
    @TableId(value = "alert_id", type = IdType.AUTO)
    @ApiModelProperty("告警ID")
    private Long alertId;

    /** 场景ID */
    @ApiModelProperty("场景ID")
    private Long sceneId;

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
