package com.sydh.iot.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.sydh.common.core.domain.BaseEntity;
import com.sydh.iot.util.PostgresJsonStringTypeHandler;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;


/**
 * 数据桥接对象 bridge
 *
 * @author zhuangpeng.li
 * @date 2024-11-18
 */
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "Bridge", description = "数据桥接 bridge")
@Data
@TableName("bridge" )
public class Bridge extends BaseEntity implements Serializable{
    private static final long serialVersionUID=1L;

    /** id唯一标识 */
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty("id唯一标识")
    private Long id;

    /** 桥接配置信息
     *  postgres数据库不能将string映射为json，typeHandler手动处理
     * */
    @ApiModelProperty("桥接配置信息")
    @TableField(typeHandler = PostgresJsonStringTypeHandler.class)
    private String configJson;

    /** 连接器名称 */
    @ApiModelProperty("连接器名称")
    private String name;

    /** 是否生效（0-不生效，1-生效） */
    @ApiModelProperty("是否生效")
    private String enable;

    /** 状态（0-未连接，1-连接中） */
    @ApiModelProperty("状态")
    private Integer status;

    /** 桥接类型(3=Http推送，4=Mqtt桥接，5=数据库存储) */
    @ApiModelProperty("桥接类型(3=Http推送，4=Mqtt桥接，5=数据库存储)")
    private Long type;

    /** 桥接方向(1=输入，2=输出) */
    @ApiModelProperty("桥接方向(1=输入，2=输出)")
    private Long direction;

    /** 转发路由（mqtt topic，http url） */
    @ApiModelProperty("转发路由")
    private String route;

    /** 删除标志（0代表存在 2代表删除） */
    @ApiModelProperty("删除标志")
    @TableLogic
    private String delFlag;

    /** 租户id */
    @ApiModelProperty("租户id")
    private Long tenantId;

    /** 租户名称 */
    @ApiModelProperty("租户名称")
    private String tenantName;

}
