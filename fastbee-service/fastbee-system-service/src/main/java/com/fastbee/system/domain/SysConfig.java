package com.fastbee.system.domain;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.fastbee.common.annotation.Excel;
import com.fastbee.common.annotation.Excel.ColumnType;
import com.fastbee.common.core.domain.BaseEntity;

import java.util.Date;

/**
 * 参数配置对象 sys_config
 *
 * @author zhuangpeng.li
 * @date 2024-12-12
 */
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "SysConfig", description = "参数配置 sys_config")
@Data
@TableName("sys_config" )
public class SysConfig extends BaseEntity{
    private static final long serialVersionUID=1L;

    /** 参数主键 */
    @TableId(value = "config_id", type = IdType.AUTO)
    @ApiModelProperty("参数主键")
    private Long configId;

    /** 参数名称 */
    @ApiModelProperty("参数名称")
    private String configName;

    /** 参数键名 */
    @ApiModelProperty("参数键名")
    private String configKey;

    /** 参数键值 */
    @ApiModelProperty("参数键值")
    private String configValue;

    /** 系统内置（Y是 N否） */
    @ApiModelProperty("系统内置")
    private String configType;

}
