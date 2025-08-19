package com.sydh.system.domain.vo;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sydh.common.core.domain.PageEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.sydh.common.annotation.Excel;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;


import java.util.HashMap;
import java.util.Map;


/**
 * 操作日志记录对象 sys_oper_log
 *
 * @author zhuangpeng.li
 * @date 2024-12-13
 */

@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "SysOperLogVO", description = "操作日志记录 sys_oper_log")
@Data
public class SysOperLogVO extends PageEntity {

    /** 操作日志ID */
    @Excel(name = "操作日志ID")
    @ApiModelProperty("操作日志ID")
    private Long operId;

    /** 模块标题 */
    @Excel(name = "模块标题")
    @ApiModelProperty("模块标题")
    private String title;

    /** 业务类型（0其它 1新增 2修改 3删除） */
    @ApiModelProperty("业务类型")
//@Excel(name = "业务类型")
    private Long businessType;

    /** 状态中文描述 - Excel导出专用 */
    @Excel(name = "业务类型")
    @JsonIgnore
    private String businessTypeDesc;

    public void setBusinessType(Long businessType) {
        this.businessType = businessType;
        // 使用 switch 语句转换业务类型为中文描述
        // 注意：Long 类型需先判断非空，否则可能出现空指针异常
        if (businessType == null) {
            this.businessTypeDesc = "未知"; // 空值处理
            return;
        }
        switch (businessType.intValue()) {
            case 0:
                this.businessTypeDesc = "其它";
                break;
            case 1:
                this.businessTypeDesc = "新增";
                break;
            case 2:
                this.businessTypeDesc = "修改";
                break;
            case 3:
                this.businessTypeDesc = "删除";
                break;
            default:
                this.businessTypeDesc = "未知"; // 处理未定义的类型
                break;
        }
    }

    public String getBusinessTypeDesc() {
        return businessTypeDesc;
    }

    /** 方法名称 */
    @Excel(name = "方法名称")
    @ApiModelProperty("方法名称")
    private String method;

    /** 请求方式 */
    @Excel(name = "请求方式")
    @ApiModelProperty("请求方式")
    private String requestMethod;

    /** 操作类别（0其它 1后台用户 2手机端用户） */
    @ApiModelProperty("操作类别")
    //@Excel(name = "操作类别")
    private Long operatorType;

    /** 状态中文描述 - Excel导出专用 */
    @Excel(name = "操作类别")
    @JsonIgnore
    private String operatorTypeDesc;

    public void setOperatorType(Long operatorType) {
        this.operatorType = operatorType;
            if (operatorType == null) {
                this.operatorTypeDesc = "未知"; // 空值处理
                return;
            }
            switch (operatorType.intValue()) {
                case 0:
                    this.operatorTypeDesc = "其它";
                    break;
                case 1:
                    this.operatorTypeDesc = "后台用户";
                    break;
                case 2:
                    this.operatorTypeDesc = "手机端用户";
                    break;
                default:
                    this.operatorTypeDesc = "未知";
                    break;
            }
    }
    public String getOperatorTypeDesc() {
        return operatorTypeDesc;
    }

    /** 操作人员 */
    @Excel(name = "操作人员")
    @ApiModelProperty("操作人员")
    private String operName;

    /** 部门名称 */
    //@Excel(name = "部门名称")
    @ApiModelProperty("部门名称")
    private String deptName;

    /** 请求URL */
    @Excel(name = "请求URL")
    @ApiModelProperty("请求URL")
    private String operUrl;

    /** 主机地址 */
    @Excel(name = "主机地址")
    @ApiModelProperty("主机地址")
    private String operIp;

    /** 操作地点 */
    @Excel(name = "操作地点")
    @ApiModelProperty("操作地点")
    private String operLocation;

    /** 请求参数 */
    @Excel(name = "请求参数")
    @ApiModelProperty("请求参数")
    private String operParam;

    /** 返回参数 */
    @Excel(name = "返回参数")
    @ApiModelProperty("返回参数")
    private String jsonResult;

    /** 操作状态（0正常 1异常） */
    @ApiModelProperty("操作状态")
    //@Excel(name = "操作状态")
    private Long status;

    /** 状态中文描述 - Excel导出专用 */
    @Excel(name = "状态")
    @JsonIgnore
    private String statusDesc;

    public void setStatus(Long status) {
        this.status = status;
        // 同步转换中文
        this.statusDesc = status == 0 ? "正常" : "异常";
    }
    public String getStatusDesc() {
        return statusDesc;
    }




    /** 错误消息 */
    @Excel(name = "错误消息")
    @ApiModelProperty("错误消息")
    private String errorMsg;

    /** 操作时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("操作时间")
    @Excel(name = "操作时间")
    private Date operTime;

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

    /** 自定义代码区域 **/

    /** 业务类型数组 */
    @ApiModelProperty("业务类型数组")
    private Integer[] businessTypes;

    /** 自定义代码区域 END**/
}
