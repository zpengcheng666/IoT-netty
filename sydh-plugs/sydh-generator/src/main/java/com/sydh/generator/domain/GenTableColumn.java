package com.sydh.generator.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sydh.common.core.domain.BaseEntity;
import com.sydh.common.utils.StringUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;

/**
 * 代码生成业务字段表 gen_table_column
 *
 * @author ruoyi
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(value = "GenTableColumn", description = "代码生成业务字段表 gen_table_column")
@TableName("gen_table_column")
public class GenTableColumn extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 编号
     */
    @ApiModelProperty("编号")
    @TableId("column_id")
    private Long columnId;

    /**
     * 归属表编号
     */
    @ApiModelProperty("归属表编号")
    private Long tableId;

    /**
     * 列名称
     */
    @ApiModelProperty("列名称")
    private String columnName;

    /**
     * 列描述
     */
    @ApiModelProperty("列描述")
    private String columnComment;

    /**
     * 列类型
     */
    @ApiModelProperty("列类型")
    private String columnType;

    /**
     * JAVA类型
     */
    @ApiModelProperty("JAVA类型")
    private String javaType;

    /**
     * JAVA字段名
     */
    @ApiModelProperty(value = "JAVA字段名", required = true)
    @NotBlank(message = "Java属性不能为空")
    private String javaField;

    /**
     * 是否主键（1是）
     */
    @ApiModelProperty("是否主键（1是）")
    private String isPk;

    /**
     * 是否自增（1是）
     */
    @ApiModelProperty("是否自增（1是）")
    private String isIncrement;

    /**
     * 是否必填（1是）
     */
    @ApiModelProperty("是否必填（1是）")
    private String isRequired;

    /**
     * 是否为插入字段（1是）
     */
    @ApiModelProperty("是否为插入字段（1是）")
    private String isInsert;

    /**
     * 是否编辑字段（1是）
     */
    @ApiModelProperty("是否编辑字段（1是）")
    private String isEdit;

    /**
     * 是否列表字段（1是）
     */
    @ApiModelProperty("是否列表字段（1是）")
    private String isList;

    /**
     * 是否查询字段（1是）
     */
    @ApiModelProperty("是否查询字段（1是）")
    private String isQuery;

    /**
     * 查询方式（EQ等于、NE不等于、GT大于、LT小于、LIKE模糊、BETWEEN范围）
     */
    @ApiModelProperty(value = "查询方式", notes = "（EQ等于、NE不等于、GT大于、LT小于、LIKE模糊、BETWEEN范围）")
    private String queryType;

    /**
     * 显示类型（input文本框、textarea文本域、select下拉框、checkbox复选框、radio单选框、datetime日期控件、image图片上传控件、upload文件上传控件、editor富文本控件）
     */
    @ApiModelProperty(value = "显示类型", notes = "（input文本框、textarea文本域、select下拉框、checkbox复选框、radio单选框、datetime日期控件、image图片上传控件、upload文件上传控件、editor富文本控件）")
    private String htmlType;

    /**
     * 字典类型
     */
    @ApiModelProperty("字典类型")
    private String dictType;

    /**
     * 排序
     */
    @ApiModelProperty("排序")
    private Integer sort;

    public String getCapJavaField() {
        return StringUtils.capitalize(javaField);
    }

    public boolean isPk() {
        return isPk(this.isPk);
    }

    public boolean isPk(String isPk) {
        return isPk != null && StringUtils.equals("1", isPk);
    }

    public boolean isIncrement() {
        return isIncrement(this.isIncrement);
    }

    public boolean isIncrement(String isIncrement) {
        return isIncrement != null && StringUtils.equals("1", isIncrement);
    }

    public boolean isRequired() {
        return isRequired(this.isRequired);
    }

    public boolean isRequired(String isRequired) {
        return isRequired != null && StringUtils.equals("1", isRequired);
    }

    public boolean isInsert() {
        return isInsert(this.isInsert);
    }

    public boolean isInsert(String isInsert) {
        return isInsert != null && StringUtils.equals("1", isInsert);
    }

    public boolean isEdit() {
        return isInsert(this.isEdit);
    }

    public boolean isEdit(String isEdit) {
        return isEdit != null && StringUtils.equals("1", isEdit);
    }

    public boolean isList() {
        return isList(this.isList);
    }

    public boolean isList(String isList) {
        return isList != null && StringUtils.equals("1", isList);
    }

    public boolean isQuery() {
        return isQuery(this.isQuery);
    }

    public boolean isQuery(String isQuery) {
        return isQuery != null && StringUtils.equals("1", isQuery);
    }

    public boolean isSuperColumn() {
        return isSuperColumn(this.javaField);
    }

    public static boolean isSuperColumn(String javaField) {
        return StringUtils.equalsAnyIgnoreCase(javaField,
                // BaseEntity
                "createBy", "createTime", "updateBy", "updateTime", "remark",
                // TreeEntity
                "parentName", "parentId", "orderNum", "ancestors");
    }

    public boolean isUsableColumn() {
        return isUsableColumn(javaField);
    }

    public static boolean isUsableColumn(String javaField) {
        // isSuperColumn()中的名单用于避免生成多余Domain属性，若某些属性在生成页面时需要用到不能忽略，则放在此处白名单
        return StringUtils.equalsAnyIgnoreCase(javaField, "parentId", "orderNum", "remark");
    }

    public String readConverterExp() {
        String remarks = StringUtils.substringBetween(this.columnComment, "（", "）");
        StringBuffer sb = new StringBuffer();
        if (StringUtils.isNotEmpty(remarks)) {
            for (String value : remarks.split(" ")) {
                if (StringUtils.isNotEmpty(value)) {
                    Object startStr = value.subSequence(0, 1);
                    String endStr = value.substring(1);
                    sb.append("").append(startStr).append("=").append(endStr).append(",");
                }
            }
            return sb.deleteCharAt(sb.length() - 1).toString();
        } else {
            return this.columnComment;
        }
    }
}
