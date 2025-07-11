package com.sydh.oauth.service;

import com.sydh.oauth.domain.OauthAccessToken;
import com.sydh.oauth.domain.OauthClientDetails;

/**
 * @author fastb
 * @date 2023-09-01 17:20
 */
public interface OauthAccessTokenService {

    String selectUserNameByTokenId(String token);

    OauthAccessToken selectByTokenId(String tokenId);

    void updateOpenIdByTokenId(String tokenId, String openUid);

    OauthAccessToken selectByUserName(String userName);

    OauthAccessToken grantAuthorizationCodeForAccessToken(OauthClientDetails client, String code, String redirectUri, String state);
}
