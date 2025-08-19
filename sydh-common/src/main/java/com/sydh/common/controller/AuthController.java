package com.sydh.common.controller;


import cn.hutool.core.util.StrUtil;
import com.sydh.common.client.OAuthClient;
import com.sydh.common.client.SecurityUtils;
import com.sydh.common.client.dto.CommonResult;
import com.sydh.common.client.dto.OAuth2AccessTokenRespDTO;
import com.sydh.common.client.dto.OAuth2CheckTokenRespDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/sso-auth")
@Api(tags = "统一登陆权限")
public class AuthController {

    @Resource
    private OAuthClient oauth2Client;



    @Value("${sso.user-base-url}")
    private String userBaseUrl;

    @Value("${sso.oauth2-base-url}")
    private String oauth2BaseUrl;


    @Value("${sso.client-id}")
    private String clientId;
    @Value("${sso.client-secret}")
    private String clientSecret;
    @Value("${sso.redirect-uri}")
    private String redirectUri;
    @Value("${sso.sso-uri}")
    private String ssoUri;
    @Value("${sso.jcpt-host}")
    private String jcptHost;


    @Value("${sso.sys-name}")
    private String sysName;

    @GetMapping("/getProp")
    public CommonResult<Map<String, String>> getProp() {

        Map<String, String> res = new HashMap<String, String>();
        res.put("userBaseUrl", userBaseUrl);
        res.put("oauth2BaseUrl", oauth2BaseUrl);
        res.put("clientId", clientId);
        res.put("clientSecret", clientSecret);
        res.put("redirectUri", redirectUri);
        res.put("ssoUri", ssoUri);
        res.put("jcptHost", jcptHost);
        res.put("sysName", sysName);

        CommonResult<Map<String, String>> cr =  new CommonResult<Map<String, String>>();
        cr.setData(res);
        cr.setCode(1);
        return cr;
    }

    /**
     * 使用 code 访问令牌，获得访问令牌
     *
     * @param code 授权码
     * @param redirectUri 重定向 URI
     * @return 访问令牌；注意，实际项目中，最好创建对应的 ResponseVO 类，只返回必要的字段
     */
    @PostMapping("/login-by-code")
    @ApiOperation("使用code获得访问令牌")
    public CommonResult<OAuth2AccessTokenRespDTO> loginByCode(@RequestParam("code") String code,
                                                              @RequestParam("redirectUri") String redirectUri) {
        return oauth2Client.postAccessToken(code, redirectUri);
    }


    @PostMapping("/check-token")
    public CommonResult<OAuth2CheckTokenRespDTO> checkToken(@RequestParam("token") String token) {
        return oauth2Client.checkToken(token);
    }

    @PostMapping("/refresh-token")
    public CommonResult<OAuth2AccessTokenRespDTO> refreshToken(@RequestParam("refreshToken") String refreshToken) {
        return oauth2Client.refreshToken(refreshToken);
    }

    @PostMapping("/logout")
    @ApiOperation("退出登录")
    public CommonResult<Boolean> logout(HttpServletRequest request) {
        String token = SecurityUtils.obtainAuthorization(request, "Authorization");
        if (StrUtil.isNotBlank(token)) {
            return oauth2Client.revokeToken(token);
        }
        // 返回成功
        return new CommonResult<>();
    }

}
