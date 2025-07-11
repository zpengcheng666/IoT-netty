package com.sydh.iot.wechat.impl;

import com.sydh.common.core.domain.AjaxResult;
import com.sydh.common.core.redis.RedisCache;
import com.sydh.common.enums.SocialPlatformType;
import com.sydh.common.enums.VerifyTypeEnum;
import com.sydh.common.exception.ServiceException;
import com.sydh.common.extend.core.domin.entity.SysUser;
import com.sydh.common.extend.core.domin.model.LoginUser;
import com.sydh.common.extend.utils.WechatUtils;
import com.sydh.common.extend.wechat.*;
import com.sydh.common.utils.DateUtils;
import com.sydh.common.utils.MessageUtils;
import com.sydh.common.utils.StringUtils;
import com.sydh.common.utils.bean.BeanUtils;
import com.sydh.common.utils.uuid.IdUtils;
import com.sydh.iot.domain.SocialPlatform;
import com.sydh.iot.domain.SocialUser;
import com.sydh.iot.model.RegisterUserInput;
import com.sydh.iot.model.RegisterUserOutput;
import com.sydh.iot.model.login.WeChatLoginQrRes;
import com.sydh.iot.service.ISocialLoginService;
import com.sydh.iot.service.ISocialPlatformService;
import com.sydh.iot.service.ISocialUserService;
import com.sydh.iot.service.IToolService;
import com.sydh.iot.wechat.WeChatService;
import com.sydh.iot.wechat.vo.WxBindReqVO;
import com.sydh.iot.wechat.vo.WxCancelBindReqVO;
import com.sydh.system.service.ISysUserService;
import com.sydh.system.service.sys.SysLoginService;
import lombok.extern.slf4j.Slf4j;
import me.zhyd.oauth.model.AuthUser;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.sydh.common.core.domain.AjaxResult.error;
import static com.sydh.common.core.domain.AjaxResult.success;
import static com.sydh.common.extend.utils.SecurityUtils.getLoginUser;
import static com.sydh.common.extend.utils.SecurityUtils.getUserId;
import static com.sydh.iot.service.impl.SocialLoginServiceImpl.LOGIN_ERROR_MSG_REDIS_KEY;
import static com.sydh.iot.service.impl.SocialLoginServiceImpl.WX_BIND_REDIS_KEY;

/**
 * 微信登录相关服务类
 *
 * @author fastb
 * @date 2023-08-14 9:16
 */
@Slf4j
@Service
public class WeChatServiceImpl implements WeChatService {

    @Resource
    private ISocialUserService socialUserService;
    @Resource
    private ISysUserService sysUserService;
    @Resource
    private SysLoginService sysLoginService;
    @Resource
    private ISocialPlatformService socialPlatformService;
    @Resource
    private ISocialLoginService socialLoginService;
    @Resource
    private IToolService toolService;
    @Resource
    private RedisCache redisCache;

    /**
     * 移动应用微信登录
     *
     * @param weChatLoginBody 微信登录参数
     * @return String
     */
    @Override
    public WeChatLoginResult mobileLogin(WeChatLoginBody weChatLoginBody, String language) {
        WeChatLoginResult weChatLoginResult = new WeChatLoginResult();
        SocialPlatformType socialPlatformType = SocialPlatformType.WECHAT_OPEN_MOBILE;
        // 查询微信平台信息
        SocialPlatform socialPlatform = socialPlatformService.selectSocialPlatformByPlatform(socialPlatformType.getSourceClient());
        if (socialPlatform == null) {
            throw new ServiceException(MessageUtils.message("wechat.please.config.open.platform"));
        }
        // 用户凭证code只能消费一次，前端调的uni.login有时会消费，然后直接就把获取到的信息传过来，不会消费的话就这里通过code拿
        if (StringUtils.isEmpty(weChatLoginBody.getAccessToken()) || StringUtils.isEmpty(weChatLoginBody.getOpenId()) || StringUtils.isEmpty(weChatLoginBody.getUnionId())) {
            WeChatAppResult weChatResult = WechatUtils.getAccessTokenOpenId(weChatLoginBody.getCode(), socialPlatform.getClientId(), socialPlatform.getSecretKey());
            if (weChatResult == null || weChatResult.getErrCode() != null) {
                throw new ServiceException(MessageUtils.message("wechat.user.certificate.gain.fail"));
            }
            weChatLoginBody.setAccessToken(weChatResult.getAccessToken()).setRefreshToken(weChatResult.getRefreshToken()).setExpiresIn(weChatResult.getExpiresIn()).setOpenId(weChatResult.getOpenId()).setUnionId(weChatResult.getUnionId()).setScope(weChatResult.getScope());
        }
        Long bindSysUserId;
        // 查询用户第三方信息
        SocialUser socialUser = socialUserService.selectOneByOpenIdAndUnionId(weChatLoginBody.getOpenId(), weChatLoginBody.getUnionId());
        if (socialUser != null && socialUser.getSysUserId() != null) {
            bindSysUserId = socialUser.getSysUserId();
        } else {
            bindSysUserId = socialUserService.selectSysUserIdByUnionId(weChatLoginBody.getUnionId());
        }
        Date nowDate = DateUtils.getNowDate();
        String uuid = IdUtils.randomUUID();
        if (socialUser == null) {
            SocialUser addSocialUser = new SocialUser();
            addSocialUser.setSysUserId(bindSysUserId).setStatus(bindSysUserId == null ? 0 : 1).setUuid(uuid).setSource(socialPlatform.getPlatform().toUpperCase(Locale.ROOT)).setAccessToken(weChatLoginBody.getAccessToken()).setExpireIn(weChatLoginBody.getExpiresIn())
                    .setRefreshToken(weChatLoginBody.getRefreshToken()).setOpenId(weChatLoginBody.getOpenId()).setUnionId(weChatLoginBody.getUnionId()).setSourceClient(socialPlatform.getPlatform())
                    .setCode(weChatLoginBody.getCode());
            addSocialUser.setCreateBy("System");
            addSocialUser.setCreateTime(nowDate);
            // 获取微信用户信息
            WeChatUserInfo weChatUserInfo = WechatUtils.getWeChatUserInfo(weChatLoginBody.getAccessToken(), weChatLoginBody.getOpenId());
            if (weChatUserInfo != null) {
                addSocialUser.setUnionId(weChatUserInfo.getUnionId()).setUsername(weChatUserInfo.getNickname())
                        .setNickname(weChatUserInfo.getNickname()).setAvatar(weChatUserInfo.getHeadImgUrl()).setGender(weChatUserInfo.getSex());
            }
            socialUserService.insertSocialUser(addSocialUser);
        } else {
            SocialUser updateSocialUser = new SocialUser();
            BeanUtils.copyProperties(socialUser, updateSocialUser);
            updateSocialUser.setUpdateBy("System");
            updateSocialUser.setSysUserId(bindSysUserId).setStatus(bindSysUserId == null ? 0 : 1).setAccessToken(weChatLoginBody.getAccessToken())
                    .setRefreshToken(weChatLoginBody.getRefreshToken()).setUuid(uuid);
            socialUserService.updateSocialUser(updateSocialUser);
        }
        // 没有绑定账号，需要去绑定系统账号,跳转到绑定页面
        if (bindSysUserId == null) {
            AuthUser authUser = new AuthUser();
            authUser.setUuid(uuid);
            authUser.setSource(socialPlatform.getPlatform());
            String bindId = socialLoginService.genBindId(authUser);
            weChatLoginResult.setBindId(bindId);
        } else {
            SysUser sysUser = sysUserService.selectUserById(bindSysUserId);
            if (sysUser == null) {
                throw new ServiceException(MessageUtils.message("user.not.exists"));
            }
//            if (null != sysUser.getDeptId()) {
//                throw new ServiceException("只允许终端用户登录！");
//            }
            String token = sysLoginService.redirectLogin(sysUser.getUserName(), sysUser.getPassword(), language);
            weChatLoginResult.setToken(token);
        }
        return weChatLoginResult;
    }

    /**
     * 小程序微信登录
     *
     * @param weChatLoginBody 微信登录参数
     * @return 登录结果
     */
    @Override
    public WeChatLoginResult miniLogin(WeChatLoginBody weChatLoginBody, String language) {
        // 使用微信手机号去登录不绑定微信，没有用户则用手机号自动注册一个登录，密码是手机号
        SocialPlatformType socialPlatformType = SocialPlatformType.WECHAT_OPEN_MINI_PROGRAM;
        // 查询微信平台信息
        SocialPlatform socialPlatform = socialPlatformService.selectSocialPlatformByPlatform(socialPlatformType.getSourceClient());
        if (socialPlatform == null) {
            throw new ServiceException(MessageUtils.message("wechat.please.config.open.platform.mini"));
        }
        if (StringUtils.isEmpty(weChatLoginBody.getCode())) {
            throw new ServiceException(MessageUtils.message("wechat.user.certificate.gain.fail"));
        }
        if (StringUtils.isEmpty(weChatLoginBody.getPhoneCode())) {
            throw new ServiceException(MessageUtils.message("wechat.user.phone.certificate.gain.fail"));
        }
        // 获取用户微信信息
        WeChatMiniProgramResult weChatResult = WechatUtils.codeToSession(weChatLoginBody.getCode(), socialPlatform.getClientId(), socialPlatform.getSecretKey());
        if (weChatResult == null || StringUtils.isEmpty(weChatResult.getOpenId())) {
            throw new ServiceException(MessageUtils.message("wechat.user.certificate.gain.fail"));
        }
        SocialUser socialUser = socialUserService.selectOneByOpenIdAndUnionId(weChatResult.getOpenId(), weChatResult.getUnionId());
        // 先获取token
        WeChatAppResult result = WechatUtils.getAccessToken(socialPlatform.getClientId(), socialPlatform.getSecretKey());
        if (result == null || StringUtils.isEmpty(result.getAccessToken())) {
            throw new ServiceException(MessageUtils.message("wechat.gain.user.call.certificate.fail"));
        }
        // 根据phoneCode获取用户手机号
        WeChatPhoneInfo userPhoneInfo = WechatUtils.getWechatUserPhoneInfo(weChatLoginBody.getPhoneCode(), result.getAccessToken());
        if (userPhoneInfo == null) {
            throw new ServiceException(MessageUtils.message("wechat.gain.user.phone.fail"));
        }
        if (!"0".equals(userPhoneInfo.getErrCode())) {
            throw new ServiceException(userPhoneInfo.getErrmsg());
        }
        String phoneNumber = userPhoneInfo.getPhoneInfo().getPhoneNumber();
        SysUser sysUser = sysUserService.selectUserByPhoneNumber(phoneNumber);
        String token;
        WeChatLoginResult weChatLoginResult = new WeChatLoginResult();
        Long bindSysUserId;
        if (sysUser == null) {
            // 直接用手机号注册一个新用户，密码就是手机号
            RegisterUserInput registerUserInput = new RegisterUserInput();
            registerUserInput.setUsername(phoneNumber);
            registerUserInput.setPhonenumber(phoneNumber);
            registerUserInput.setPassword(phoneNumber);
            RegisterUserOutput registerUserOutput = toolService.registerNoCaptcha(registerUserInput);
            if (StringUtils.isNotEmpty(registerUserOutput.getMsg())) {
                throw new ServiceException(registerUserOutput.getMsg());
            }
            bindSysUserId = registerUserOutput.getSysUserId();
            token = sysLoginService.redirectLogin(phoneNumber, registerUserOutput.getEncryptPassword(), language);
        } else {
//            if (null != sysUser.getDeptId()) {
//                throw new ServiceException("只允许终端用户登录！");
//            }
            bindSysUserId = sysUser.getUserId();
            token = sysLoginService.redirectLogin(sysUser.getUserName(), sysUser.getPassword(), language);
        }
        Long oldBindUserId = socialUserService.selectSysUserIdByUnionId(weChatResult.getUnionId());
        // 当前手机号登录用户绑定当前微信信息
        if (null == oldBindUserId || oldBindUserId.equals(bindSysUserId)) {
            miniCreateOrUpdateSocialUser(socialUser, socialPlatform, weChatLoginBody, weChatResult, result.getAccessToken(), bindSysUserId);
        }
        weChatLoginResult.setToken(token);
        return weChatLoginResult;
    }

    private void miniCreateOrUpdateSocialUser(SocialUser socialUser, SocialPlatform socialPlatform, WeChatLoginBody weChatLoginBody, WeChatMiniProgramResult weChatResult, String accessToken, Long bindSysUserId) {
        Date nowDate = DateUtils.getNowDate();
        String uuid = IdUtils.randomUUID();
        if (socialUser == null) {
            SocialUser insertSocialUser = new SocialUser();
            String sourceClient = socialPlatform.getPlatform();
            insertSocialUser.setSysUserId(bindSysUserId).setStatus(1).setUuid(uuid).setSource(sourceClient.toUpperCase(Locale.ROOT)).setSourceClient(sourceClient)
                    .setAccessToken(accessToken).setOpenId(weChatResult.getOpenId()).setUnionId(weChatResult.getUnionId())
                    .setCode(weChatLoginBody.getCode()).setDelFlag("0");
            insertSocialUser.setCreateBy("System");
            insertSocialUser.setCreateTime(nowDate);
            socialUserService.insertSocialUser(insertSocialUser);
        } else {
            SocialUser updateSocialUser = new SocialUser();
            updateSocialUser.setSocialUserId(socialUser.getSocialUserId());
            updateSocialUser.setSysUserId(bindSysUserId).setStatus(1).setCode(weChatLoginBody.getCode());
            updateSocialUser.setUpdateBy("System");
            socialUserService.updateSocialUser(updateSocialUser);
        }
    }

    /**
     * 取消所有相关微信绑定
     *
     * @param wxCancelBindReqVO 微信解绑传参类型
     * @return 结果
     */
    @Override
    public AjaxResult cancelBind(WxCancelBindReqVO wxCancelBindReqVO) {
        LoginUser loginUser = getLoginUser();
        if (loginUser == null || loginUser.getUserId() == null) {
            throw new ServiceException(MessageUtils.message("wechat.please.login"));
        }
        // 密码验证
        if (VerifyTypeEnum.PASSWORD.getVerifyType() == wxCancelBindReqVO.getVerifyType()) {
            if (StringUtils.isEmpty(wxCancelBindReqVO.getPassword())) {
                throw new ServiceException(MessageUtils.message("wechat.please.enter.user.password"));
            }
            Boolean validateResult = sysUserService.validatePassword(loginUser.getUser().getPassword(), wxCancelBindReqVO.getPassword());
            if (Boolean.FALSE.equals(validateResult)) {
                throw new ServiceException(MessageUtils.message("wechat.cancelBind.password.fail"));
            }
        }
        // 解绑所有微信应用
        int cancelBind = socialUserService.cancelBind(loginUser.getUserId(), SocialPlatformType.listWechatPlatform);
        return cancelBind >= 1 ? success(MessageUtils.message("unbind.success")) : AjaxResult.error(MessageUtils.message("unbind.fail"));
    }

    /**
     * 小程序、移动应用微信绑定
     *
     * @param wxBindReqVO 微信绑定传参类型
     * @return 结果
     */
    @Override
    public AjaxResult bind(WxBindReqVO wxBindReqVO) {
        Long sysUserId = getUserId();
        if (sysUserId == null) {
            throw new ServiceException(MessageUtils.message("wechat.please.login"));
        }
        String openId = "";
        String unionId = "";
        // 区分小程序绑定还是移动应用绑定
        if (SocialPlatformType.WECHAT_OPEN_MOBILE.sourceClient.equals(wxBindReqVO.getSourceClient())) {
            if (StringUtils.isEmpty(wxBindReqVO.getOpenId()) || StringUtils.isEmpty(wxBindReqVO.getUnionId())) {
                throw new ServiceException(MessageUtils.message("wechat.please.enter.wechat.user.info"));
            }
            openId = wxBindReqVO.getOpenId();
            unionId = wxBindReqVO.getUnionId();
        } else if (SocialPlatformType.WECHAT_OPEN_MINI_PROGRAM.sourceClient.equals(wxBindReqVO.getSourceClient())) {
            if (StringUtils.isEmpty(wxBindReqVO.getCode())) {
                throw new ServiceException(MessageUtils.message("wechat.please.enter.user.certificate"));
            }
            // 查询微信平台信息
            SocialPlatform socialPlatform = socialPlatformService.selectSocialPlatformByPlatform(SocialPlatformType.WECHAT_OPEN_MINI_PROGRAM.sourceClient);
            if (socialPlatform == null) {
                throw new ServiceException(MessageUtils.message("wechat.please.config.open.platform.mini"));
            }
            WeChatMiniProgramResult weChatMiniProgramResult = WechatUtils.codeToSession(wxBindReqVO.getCode(), socialPlatform.getClientId(), socialPlatform.getSecretKey());
            if (weChatMiniProgramResult == null
                    || (StringUtils.isEmpty(weChatMiniProgramResult.getOpenId()) && StringUtils.isEmpty(weChatMiniProgramResult.getUnionId()))) {
                throw new ServiceException(MessageUtils.message("wechat.gain.wechat.info.fail"));
            }
            openId = weChatMiniProgramResult.getOpenId();
            unionId = weChatMiniProgramResult.getUnionId();
        }
        Long bindUserId = socialUserService.selectSysUserIdByUnionId(unionId);
        if (bindUserId != null && !bindUserId.equals(sysUserId)) {
            throw new ServiceException(MessageUtils.message("wechat.your.wechat.already.bind.other.account"));
        }
        SocialUser socialUser = socialUserService.selectOneByOpenIdAndUnionId(openId, unionId);
        int bindResult;
        List<SocialUser> socialUserList = socialUserService.selectBySysUserId(sysUserId);
        if (CollectionUtils.isNotEmpty(socialUserList)) {
            return success(MessageUtils.message("bind.success"));
        }
        if (socialUser != null) {
            if (socialUser.getSysUserId() != null && !sysUserId.equals(socialUser.getSysUserId())) {
                throw new ServiceException(MessageUtils.message("wechat.this.wechat.already.bind.other.account"));
            }
            SocialUser updateSocialUser = new SocialUser();
            updateSocialUser.setSocialUserId(socialUser.getSocialUserId());
            updateSocialUser.setSysUserId(sysUserId).setStatus(1);
            updateSocialUser.setUpdateBy("System");
            bindResult = socialUserService.updateSocialUser(updateSocialUser);
        } else {
            Date nowDate = DateUtils.getNowDate();
            String uuid = IdUtils.randomUUID();
            SocialUser insertSocialUser = new SocialUser();
            insertSocialUser.setSysUserId(sysUserId).setStatus(1).setUuid(uuid).setSource(wxBindReqVO.getSourceClient().toUpperCase(Locale.ROOT)).setSourceClient(wxBindReqVO.getSourceClient())
                    .setOpenId(openId).setUnionId(unionId)
                    .setDelFlag("0");
            insertSocialUser.setCreateBy("System");
            insertSocialUser.setCreateTime(nowDate);
            bindResult = socialUserService.insertSocialUser(insertSocialUser);
        }
        // 绑定
        return bindResult >= 1 ? success(MessageUtils.message("bind.success")) : AjaxResult.error(MessageUtils.message("bind.fail"));
    }

    @Override
    public AjaxResult getWxBindQr(HttpServletRequest httpServletRequest) {
        Long sysUserId = getUserId();
        if (sysUserId == null) {
            throw new ServiceException(MessageUtils.message("wechat.please.login"));
        }
        WeChatLoginQrRes weChatLoginQrRes = new WeChatLoginQrRes();
        SocialPlatform socialPlatform = socialPlatformService.selectSocialPlatformByPlatform(SocialPlatformType.WECHAT_OPEN_WEB_BIND.sourceClient);
        if (socialPlatform == null) {
            throw new ServiceException(MessageUtils.message("wechat.please.config.open.platform.web.application.personal.bind.info"));
        }
        weChatLoginQrRes.setAppid(socialPlatform.getClientId());
        weChatLoginQrRes.setScope("snsapi_login");
        weChatLoginQrRes.setState(String.valueOf(System.currentTimeMillis()));
        String wxBindId = socialLoginService.genWxBindId(sysUserId);
        String url = socialPlatform.getRedirectUri() + wxBindId;
        weChatLoginQrRes.setRedirectUri(url);
        return success(weChatLoginQrRes);
    }

    @Override
    public String wxBindCallback(String code, String state, String wxBindId, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        SocialPlatform socialPlatform = socialPlatformService.selectSocialPlatformByPlatform(SocialPlatformType.WECHAT_OPEN_WEB_BIND.sourceClient);
        if (socialPlatform == null) {
            String serverName = httpServletRequest.getServerName();
            String msgId = socialLoginService.genErrorId(MessageUtils.message("wechat.please.config.open.platform.web.application.personal.bind.info"));
            return "https://" + serverName + "/user/profile?wxBindMsgId=" + msgId;
        }
        String url = socialPlatform.getRedirectLoginUri();
        //获取临时票据 code
        log.info("code:{}", code);
        if (StringUtils.isEmpty(code)) {
            String msgId = socialLoginService.genErrorId(MessageUtils.message("wechat.you.cancel.or.not.gain.authorization.info"));
            return url + msgId;
        }
        Long sysUserId = redisCache.getCacheObject(WX_BIND_REDIS_KEY + wxBindId);
        if (sysUserId == null) {
            String msgId = socialLoginService.genErrorId(MessageUtils.message("wechat.the.qr.code.has.expired"));
            return url + msgId;
        }
        List<SocialUser> socialUserList = socialUserService.selectBySysUserId(sysUserId);
        if (CollectionUtils.isNotEmpty(socialUserList)) {
            String msgId = socialLoginService.genErrorId(MessageUtils.message("wechat.your.account.already.bind.wechat"));
            return url + msgId;
        }
        // 组装获取accessToken的url
        WeChatAppResult weChatAppResult = WechatUtils.getAccessTokenOpenId(code, socialPlatform.getClientId(), socialPlatform.getSecretKey());
        if (weChatAppResult == null || StringUtils.isEmpty(weChatAppResult.getAccessToken())
                || StringUtils.isEmpty(weChatAppResult.getOpenId()) || StringUtils.isEmpty(weChatAppResult.getUnionId())) {
            String msgId = socialLoginService.genErrorId(MessageUtils.message("wechat.gain.wechat.info.fail"));
            return url + msgId;
        }
        Long bindUserId = socialUserService.selectSysUserIdByUnionId(weChatAppResult.getUnionId());
        if (bindUserId != null && !bindUserId.equals(sysUserId)) {
            String msgId = socialLoginService.genErrorId(MessageUtils.message("wechat.your.wechat.already.bind.other.account"));
            return url + msgId;
        }
        SocialUser socialUser = socialUserService.selectOneByOpenIdAndUnionId(weChatAppResult.getOpenId(), weChatAppResult.getUnionId());
        if (socialUser == null) {
            socialUser = new SocialUser();
        }
        WeChatUserInfo weChatUserInfo = WechatUtils.getWeChatUserInfo(weChatAppResult.getAccessToken(), weChatAppResult.getOpenId());
        String uuid = IdUtils.randomUUID();
        Date nowDate = DateUtils.getNowDate();
        socialUser.setUuid(uuid).setAccessToken(weChatAppResult.getAccessToken()).setRefreshToken(weChatAppResult.getRefreshToken())
                .setOpenId(weChatUserInfo.getOpenId()).setUnionId(weChatUserInfo.getUnionId()).setSysUserId(sysUserId).setStatus(1)
                .setNickname(weChatUserInfo.getNickname()).setUsername(weChatUserInfo.getNickname()).setAvatar(weChatUserInfo.getHeadImgUrl());
        // 个人中心绑定这个也做了一个配置，标识设为了 wechat_open_web_bind,但其实都属于网站应用，所以这里还是改为使用wechat_open_web这个来记录来源吧,统一为网站来源，方便查询
        socialUser.setSource(SocialPlatformType.WECHAT_OPEN_WEB.sourceClient).setSourceClient(SocialPlatformType.WECHAT_OPEN_WEB.sourceClient);
        if (socialUser.getCreateTime() == null) {
            socialUser.setCreateTime(nowDate);
            socialUser.setCreateBy("System");
        }
        socialUser.setUpdateTime(nowDate);
        socialUser.setUpdateBy("System");
        int updateResult = socialUser.getSocialUserId() == null ? socialUserService.insertSocialUser(socialUser) : socialUserService.updateSocialUser(socialUser);
        String msg = updateResult >= 1 ? "绑定成功" : "绑定失败";
        String msgId = socialLoginService.genErrorId(msg);
        return url + msgId;
    }

    @Override
    public AjaxResult getWxBindMsg(String wxBindMsgId) {
        String errorMsg = redisCache.getCacheObject(LOGIN_ERROR_MSG_REDIS_KEY + wxBindMsgId);
        if (StringUtils.isEmpty(errorMsg)) {
            return success();
        } else if ("绑定成功".equals(errorMsg)) {
            return success(errorMsg);
        } else {
            return error(errorMsg);
        }
    }
}
