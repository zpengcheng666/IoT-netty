package com.sydh.oauth.service.impl;

import cn.hutool.core.util.IdUtil;
import com.sydh.oauth.domain.OauthAccessToken;
import com.sydh.oauth.domain.OauthClientDetails;
import com.sydh.oauth.domain.OauthCode;
import com.sydh.oauth.mapper.OauthAccessTokenMapper;
import com.sydh.oauth.service.IOauthClientDetailsService;
import com.sydh.oauth.service.IOauthCodeService;
import com.sydh.oauth.service.OauthAccessTokenService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;

/**
 * @author fastb
 * @date 2023-09-01 17:20
 */
@Service
public class OauthAccessTokenServiceImpl implements OauthAccessTokenService {

    @Resource
    private OauthAccessTokenMapper oauthAccessTokenMapper;
    @Resource
    private IOauthCodeService oauthCodeService;
    @Resource
    private IOauthClientDetailsService oauthClientDetailsService;

    @Override
    public String selectUserNameByTokenId(String tokenId) {
        return oauthAccessTokenMapper.selectUserNameByTokenId(tokenId);
    }

    @Override
    public OauthAccessToken selectByTokenId(String tokenId) {
        return oauthAccessTokenMapper.selectByTokenId(tokenId);
    }

    @Override
    public void updateOpenIdByTokenId(String tokenId, String openUid) {
        oauthAccessTokenMapper.updateOpenIdByTokenId(tokenId, openUid);
    }

    @Override
    public OauthAccessToken selectByUserName(String userName) {
        return oauthAccessTokenMapper.selectByUserName(userName);
    }

    @Override
    public OauthAccessToken grantAuthorizationCodeForAccessToken(OauthClientDetails client, String code, String redirectUri, String state) {
        OauthCode oauthCode = oauthCodeService.consumeAuthorizationCode(code);
        oauthAccessTokenMapper.deleteByUserId(oauthCode.getUserId());
        OauthAccessToken oauthAccessToken = new OauthAccessToken();
        oauthAccessToken.setTokenId(generateRefreshToken());
        oauthAccessToken.setClientId(client.getClientId());
        oauthAccessToken.setUserId(oauthCode.getUserId());
        oauthAccessToken.setRefreshToken(generateRefreshToken());
        oauthAccessToken.setExpiresTime(LocalDateTime.now().plusSeconds(client.getAccessTokenValidity()));
        oauthAccessTokenMapper.insertOauthAccessToken(oauthAccessToken);
        return oauthAccessToken;
    }

    private static String generateRefreshToken() {
        return IdUtil.fastSimpleUUID();
    }

}
