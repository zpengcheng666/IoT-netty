package com.sydh.iot.service;

import com.sydh.common.core.domain.AjaxResult;
import com.sydh.common.extend.core.domin.model.BindLoginBody;
import com.sydh.common.extend.core.domin.model.BindRegisterBody;
import com.sydh.common.extend.core.domin.model.LoginBody;
import com.sydh.iot.domain.SocialUser;
import me.zhyd.oauth.model.AuthCallback;
import me.zhyd.oauth.model.AuthUser;

import javax.servlet.http.HttpServletRequest;

/**
 * 第三方登录Service接口
 * 处理登录跳转业务逻辑
 *
 * @author json
 * @date 2022-04-12
 */
public interface ISocialLoginService {

    /**
     * 第三方登录跳转
     *
     * @param source             平台
     * @param httpServletRequest 当前请求
     * @return 跳转路径
     */
    String renderAuth(String source, HttpServletRequest httpServletRequest);

    /**
     * 第三方登录callback
     *
     * @param source             平台
     * @param authCallback       回调参数
     * @param httpServletRequest 当前请求
     * @return 跳转路径
     */
    String callback(String source, AuthCallback authCallback, HttpServletRequest httpServletRequest);

    /**
     * 检查是否bindId
     *
     * @param bindId 绑定id
     * @return
     */
    AjaxResult checkBindId(String bindId);

    /**
     * 获得错误显示
     *
     * @param errorId errorId
     * @return
     */
    AjaxResult getErrorMsg(String errorId);

    /**
     * 跳转直接登录
     *
     * @param loginId 登录id
     * @return
     */
    AjaxResult socialLogin(String loginId, String language);

    /**
     * 绑定登录api
     *
     * @param bindLoginBody 绑定账户参数
     * @param language 语言
     * @return
     */
    AjaxResult bindLogin(BindLoginBody bindLoginBody, String language);

    /**
     * 注册绑定api
     *
     * @param bindRegisterBody
     * @return
     */
    AjaxResult bindRegister(BindRegisterBody bindRegisterBody, String language);

    String genBindId(AuthUser authUser);

    String genErrorId(String msg);

    String genWxBindId(Long userId);

    SocialUser findSocialUser(String uuid, String source);

    AjaxResult checkSocialUser(SocialUser socialUser, String bindId);

    /**
     * 短信登录方法
     *
     * @param loginBody 登录信息
     * @return 结果
     */
    AjaxResult smsLogin(LoginBody loginBody, String language);

    /**
     * oauth2登录
     * @param loginBody 登录信息
     * @return com.sydh.common.core.domain.AjaxResult
     */
    AjaxResult ssoLogin(LoginBody loginBody, String language);
}
