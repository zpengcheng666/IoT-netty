package com.sydh.system.domain.vo;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sydh.common.core.domain.PageEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.sydh.common.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * 系统访问记录对象 sys_logininfor
 *
 * @author zhuangpeng.li
 * @date 2024-12-13
 */

@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "SysLogininforVO", description = "系统访问记录 sys_logininfor")
@Data
public class SysLogininforVO extends PageEntity {
    /** 代码生成区域 可直接覆盖**/
    /** 访问ID */
    @Excel(name = "访问ID")
    @ApiModelProperty("访问ID")
    private Long infoId;

    /** 用户账号 */
    @Excel(name = "用户账号")
    @ApiModelProperty("用户账号")
    private String userName;

    /** 登录IP地址 */
    @Excel(name = "登录IP地址")
    @ApiModelProperty("登录IP地址")
    private String ipaddr;

    /** 登录地点 */
    @Excel(name = "登录地点")
    @ApiModelProperty("登录地点")
    private String loginLocation;

    /** 浏览器类型 */
    @Excel(name = "浏览器类型")
    @ApiModelProperty("浏览器类型")
    private String browser;

    /** 操作系统 */
    @Excel(name = "操作系统")
    @ApiModelProperty("操作系统")
    private String os;

    /** 登录状态（0成功 1失败） */
    @ApiModelProperty("登录状态")
    //@Excel(name = "登录状态")
    private Integer status;


    /** 状态中文描述 - Excel导出专用 */
    @Excel(name = "登录状态")
    @JsonIgnore
    private String statusDesc;

    public void setStatus(Integer status) {
        this.status = status;
        // 同步转换中文
        this.statusDesc = status == 0 ? "成功" : "失败";
    }
    public String getStatusDesc() {
        return statusDesc;
    }


    /** 提示消息 */
    @Excel(name = "提示消息")
    @ApiModelProperty("提示消息")
    private String msg;

    /** 访问时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("访问时间")
    @Excel(name = "访问时间")
    private Date loginTime;


    /** 代码生成区域 可直接覆盖END**/

    /** 自定义代码区域 **/


    /** 自定义代码区域 END**/
}
