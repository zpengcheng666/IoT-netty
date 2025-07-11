package com.sydh.iot.service.impl;

import com.sydh.common.enums.SocialPlatformType;
import com.sydh.common.exception.ServiceException;
import com.sydh.iot.domain.SocialPlatform;
import com.sydh.iot.model.login.AuthRequestWrap;
import com.sydh.iot.service.IAuthRequestFactory;
import com.sydh.iot.service.ISocialPlatformService;
import me.zhyd.oauth.config.AuthConfig;
import me.zhyd.oauth.request.AuthQqRequest;
import me.zhyd.oauth.request.AuthRequest;
import me.zhyd.oauth.request.AuthWeChatOpenRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class AuthRequestFactoryImpl implements IAuthRequestFactory {

    @Autowired
    private ISocialPlatformService iSocialPlatformService;

    @Autowired
    private AuthStateRedisCache authStateRedisCache;

    /**
     * 获得对于AUthRequest
     *
     * @param source 登录方式
     * @return 对应AuthRequest
     */
    @Override
    public AuthRequestWrap getAuthRequest(String source) {
        AuthRequestWrap authRequestWrap = new AuthRequestWrap();
        AuthRequest authRequest;
        try {
            String lowerSource = source.toLowerCase(Locale.ROOT);
            SocialPlatformType socialPlatformType = SocialPlatformType.getSocialPlatformType(lowerSource);
            if (socialPlatformType == null) {
                throw new ServiceException("未获取到第三方平台来源");
            }
            SocialPlatform socialPlatform = iSocialPlatformService.selectSocialPlatformByPlatform(lowerSource);
            authRequestWrap.setSocialPlatform(socialPlatform);
            AuthConfig authConfig = AuthConfig.builder()
                    .clientId(socialPlatform.getClientId())
                    .clientSecret(socialPlatform.getSecretKey())
                    .redirectUri(socialPlatform.getRedirectUri())
                    .build();
            switch (socialPlatformType) {
                case WECHAT_OPEN_WEB: {
                    authRequest = new AuthWeChatOpenRequest(authConfig, authStateRedisCache);
                    break;
                }
                case QQ_OPEN_WEB: {
                    authRequest = new AuthQqRequest(authConfig, authStateRedisCache);
                    break;
                }
                default: {
                    throw new ServiceException("source: " + source + ",暂不支持");
                }
            }
            authRequestWrap.setAuthRequest(authRequest);
            return authRequestWrap;
        } catch (Exception e) {
            throw new ServiceException(e.getMessage());
        }
    }

}
