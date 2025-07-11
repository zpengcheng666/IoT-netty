package com.sydh.iot.model.vo;

import com.sydh.common.annotation.Excel;
import com.sydh.common.core.domain.PageEntity;
import com.sydh.iot.domain.ModbusConfig;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.List;


/**
 * 物模型对象 iot_things_model
 *
 * @author zhuangpeng.li
 * @date 2024-12-23
 */

@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "ThingsModelVO", description = "物模型 iot_things_model")
@Data
public class ThingsModelVO extends PageEntity {
    /** 代码生成区域 可直接覆盖**/
    /** 物模型ID */
    @ApiModelProperty("物模型ID")
    private Long modelId;

    /** 物模型名称 */
    @Excel(name = "物模型名称")
    @ApiModelProperty("物模型名称")
    private String modelName;

    /** 物模型名称 */
    @ApiModelProperty("中文物模型名称_中文")
    private String modelName_zh_CN;

    /** 物模型名称 */
    @ApiModelProperty("英文物模型名称")
    private String modelName_en_US;

    /** 产品ID */
    @ApiModelProperty("产品ID")
    private Long productId;

    /** 产品名称 */
    @ApiModelProperty("产品名称")
    private String productName;

    /** 租户ID */
    @ApiModelProperty("租户ID")
    private Long tenantId;

    /** 租户名称 */
    @ApiModelProperty("租户名称")
    private String tenantName;

    /** 标识符，产品下唯一 */
    @Excel(name = "标识符")
    @ApiModelProperty("标识符，产品下唯一")
    private String identifier;

    /** 模型类别（1-属性，2-功能，3-事件） */
    @ApiModelProperty("模型类别")
    @Excel(name = "模型类别", readConverterExp = "1=属性,2=功能,3=事件")
    private Integer type;

    /** 数据类型（integer、decimal、string、bool、array、enum） */
    @ApiModelProperty("数据类型")
    @Excel(name = "数据类型")
    private String datatype;

    /** 数据定义 */
    @Excel(name = "数据定义",type = Excel.Type.EXPORT)
    @ApiModelProperty("数据定义")
    private String specs;

    /** 计算公式 */
    @Excel(name = "计算公式")
    @ApiModelProperty("计算公式")
    private String formula;

    /** 是否图表展示（0-否，1-是） */
    @ApiModelProperty("是否图表展示")
    @Excel(name = "是否图表展示", readConverterExp = "0=否,1=是")
    private Integer isChart;

    /** 是否实时监测（0-否，1-是） */
    @ApiModelProperty("是否实时监测")
    @Excel(name = "是否实时监测", readConverterExp = "0=否,1=是")
    private Integer isMonitor;

    /** 是否历史存储（0-否，1-是） */
    @ApiModelProperty("是否历史存储")
    @Excel(name = "是否历史存储", readConverterExp = "0=否,1=是")
    private Integer isHistory;

    /** 是否只读数据(0-否，1-是) */
    @Excel(name = "是否只读数据", readConverterExp = "0=否,1=是")
    @ApiModelProperty("是否只读数据(0-否，1-是)")
    private Integer isReadonly;

    /** 是否设备分享权限(0-否，1-是) */
    @Excel(name = "是否设备分享权限", readConverterExp = "0=否,1=是")
    @ApiModelProperty("是否设备分享权限(0-否，1-是)")
    private Integer isSharePerm;

    /** 是否在APP显示(0-否，1-是) */
    @ApiModelProperty("是否在APP显示(0-否，1-是)")
    private Integer isApp;

    /** 排序，值越大，排序越靠前 */
    @Excel(name = "排序值")
    @ApiModelProperty("排序，值越大，排序越靠前")
    private Integer modelOrder;

    /** 删除标志（0代表存在 2代表删除） */
    @ApiModelProperty("删除标志")
    private String delFlag;

    /** 创建者 */
    @ApiModelProperty("创建者")
    private String createBy;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("创建时间")
    private Date createTime;

    /** 更新者 */
    @ApiModelProperty("更新者")
    private String updateBy;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("更新时间")
    private Date updateTime;

    /** 备注 */
    @ApiModelProperty("备注")
    private String remark;

    @Excel(name = "单位")
    private String unit;

    @Excel(name = "有效值范围", defaultValue = "0/100")
    private String limitValue;

    private ModbusConfig modbusConfig;

    private String language;

    private List<Long> modelIdList;

    /**
     *  0->置顶，1->上移，2->下移
     */
    private Integer action;

    private Long upModelId;

    private Long downModelId;
}
