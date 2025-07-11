package com.sydh.iot.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sydh.common.core.domain.PageEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;


/**
 * 第三方登录用户对象 iot_social_user
 *
 * @author zhuangpeng.li
 * @date 2024-11-14
 */

@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "SocialUser", description = "第三方登录用户 iot_social_user")
@Data
@Accessors(chain = true)
@TableName("iot_social_user" )
public class SocialUser extends PageEntity implements Serializable{
    private static final long serialVersionUID=1L;

    /** 第三方系统用户表主键 */
    @TableId(value = "social_user_id", type = IdType.AUTO)
    @ApiModelProperty("第三方系统用户表主键")
    private Long socialUserId;

    /** 第三方系统的唯一ID */
    @ApiModelProperty("第三方系统的唯一ID")
    private String uuid;

    /** 第三方用户来源 */
    @ApiModelProperty("第三方用户来源")
    private String source;

    /** 用户的授权令牌 */
    @ApiModelProperty("用户的授权令牌")
    private String accessToken;

    /** 第三方用户的授权令牌的有效期（部分平台可能没有） */
    @ApiModelProperty("第三方用户的授权令牌的有效期")
    private Long expireIn;

    /** 刷新令牌(部分平台可能没有) */
    @ApiModelProperty("刷新令牌(部分平台可能没有)")
    private String refreshToken;

    /** 第三方用户的 open id（部分平台可能没有） */
    @ApiModelProperty("第三方用户的 open id")
    private String openId;

    /** 第三方用户的 ID(部分平台可能没有) */
    @ApiModelProperty("第三方用户的 ID(部分平台可能没有)")
    private String uid;

    /** 个别平台的授权信息（部分平台可能没有） */
    @ApiModelProperty("个别平台的授权信息")
    private String accessCode;

    /** 第三方用户的 union id(部分平台可能没有) */
    @ApiModelProperty("第三方用户的 union id(部分平台可能没有)")
    private String unionId;

    /** 第三方用户授予的权限(部分平台可能没有) */
    @ApiModelProperty("第三方用户授予的权限(部分平台可能没有)")
    private String scope;

    /** 个别平台的授权信息（部分平台可能没有） */
    @ApiModelProperty("个别平台的授权信息")
    private String tokenType;

    /** id token（部分平台可能没有） */
    @ApiModelProperty("id token")
    private String idToken;

    /** 小米平台用户的附带属性（部分平台可能没有） */
    @ApiModelProperty("小米平台用户的附带属性")
    private String macAlgorithm;

    /** 小米平台用户的附带属性(部分平台可能没有) */
    @ApiModelProperty("小米平台用户的附带属性(部分平台可能没有)")
    private String macKey;

    /** 用户的授权code（部分平台可能没有） */
    @ApiModelProperty("用户的授权code")
    private String code;

    /** Twitter平台用户的附带属性(部分平台可能没有) */
    @ApiModelProperty("Twitter平台用户的附带属性(部分平台可能没有)")
    private String oauthToken;

    /** Twitter平台用户的附带属性(部分平台可能没有) */
    @ApiModelProperty("Twitter平台用户的附带属性(部分平台可能没有)")
    private String oauthTokenSecret;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("创建时间")
    private Date createTime;

    /** 创建者 */
    @ApiModelProperty("创建者")
    private String createBy;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("更新时间")
    private Date updateTime;

    /** 更新者 */
    @ApiModelProperty("更新者")
    private String updateBy;

    /** 删除标记位(0代表存在,2代表删除) */
    @ApiModelProperty("删除标记位(0代表存在,2代表删除)")
    @TableLogic(value = "0", delval = "NULL")
    private String delFlag;

    /** 绑定状态(0:未绑定,1:绑定) */
    @ApiModelProperty("绑定状态(0:未绑定,1:绑定)")
    private Integer status;

    /** 用户ID */
    @ApiModelProperty("用户ID")
    private Long sysUserId;

    /** 用户名 */
    @ApiModelProperty("用户名")
    private String username;

    /** 用户昵称 */
    @ApiModelProperty("用户昵称")
    private String nickname;

    /** 用户头像 */
    @ApiModelProperty("用户头像")
    private String avatar;

    /** 用户性别 */
    @ApiModelProperty("用户性别")
    private Integer gender;

    /** 第三方用户来源客户端（web、app、小程序） */
    @ApiModelProperty("第三方用户来源客户端")
    private String sourceClient;

}
