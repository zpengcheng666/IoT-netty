package com.sydh.common.client;


import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import com.sydh.common.client.dto.*;
import com.sydh.common.core.redis.RedisCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.Base64Utils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
@Slf4j
public class OAuthClient {

    @Value("${sso.oauth2-base-url}")
    private String BASE_URL;

    /**
     * 租户编号
     *
     * 默认使用 1；如果使用别的租户，可以调整
     */
    public static final Long TENANT_ID = 1L;


    @Value("${sso.client-id}")
    private String CLIENT_ID;
    @Value("${sso.client-secret}")
    private String CLIENT_SECRET;

    @Autowired
    private RedisCache redisCache;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * 使用 code 授权码，获得访问令牌
     *
     * @param code        授权码
     * @param redirectUri 重定向 URI
     * @return 访问令牌
     */
    public CommonResult<OAuth2AccessTokenRespDTO> postAccessToken(String code, String redirectUri) {
        // 1.1 构建请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//        headers.set("tenant-id", TENANT_ID.toString());
        addClientHeader(headers);
        // 1.2 构建请求参数
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("code", code);
        body.add("redirect_uri", redirectUri);
//        body.add("state", "12345678"); // 选填；填了会校验

        // 2. 执行请求
        ResponseEntity<CommonResult<OAuth2AccessTokenRespDTO>> exchange = restTemplate.exchange(
                this.BASE_URL + "/token",
                HttpMethod.POST,
                new HttpEntity<>(body, headers),
                new ParameterizedTypeReference<CommonResult<OAuth2AccessTokenRespDTO>>() {}); // 解决 CommonResult 的泛型丢失
        Assert.isTrue(exchange.getStatusCode().is2xxSuccessful(), "响应必须是 200 成功");
        log.info("=============请求token返回值");
        CommonResult<OAuth2AccessTokenRespDTO> res = exchange.getBody();
        log.info(JSONUtil.toJsonStr(res));
        return res;
    }

    public CommonResult<OAuth2CheckTokenRespDTO> checkToken(String token) {
        // 1.1 构建请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//        headers.set("tenant-id", TENANT_ID.toString());
        addClientHeader(headers);
        // 1.2 构建请求参数
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("token", token);

        // 2. 执行请求
        ResponseEntity<CommonResult<OAuth2CheckTokenRespDTO>> exchange = restTemplate.exchange(
                this.BASE_URL + "/check-token",
                HttpMethod.POST,
                new HttpEntity<>(body, headers),
                new ParameterizedTypeReference<CommonResult<OAuth2CheckTokenRespDTO>>() {}); // 解决 CommonResult 的泛型丢失
        Assert.isTrue(exchange.getStatusCode().is2xxSuccessful(), "响应必须是 200 成功");
        return exchange.getBody();
    }

    public CommonResult<OAuth2AccessTokenRespDTO> refreshToken(String refreshToken) {
        // 1.1 构建请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//        headers.set("tenant-id", TENANT_ID.toString());
        addClientHeader(headers);
        // 1.2 构建请求参数
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "refresh_token");
        body.add("refresh_token", refreshToken);

        // 2. 执行请求
        ResponseEntity<CommonResult<OAuth2AccessTokenRespDTO>> exchange = restTemplate.exchange(
                this.BASE_URL + "/token",
                HttpMethod.POST,
                new HttpEntity<>(body, headers),
                new ParameterizedTypeReference<CommonResult<OAuth2AccessTokenRespDTO>>() {}); // 解决 CommonResult 的泛型丢失
        Assert.isTrue(exchange.getStatusCode().is2xxSuccessful(), "响应必须是 200 成功");
        return exchange.getBody();
    }

    public CommonResult<Boolean> revokeToken(String token) {
        // 1.1 构建请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//        headers.set("tenant-id", TENANT_ID.toString());
        addClientHeader(headers);
        // 1.2 构建请求参数
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("token", token);

        // 2. 执行请求
        ResponseEntity<CommonResult<Boolean>> exchange = restTemplate.exchange(
                this.BASE_URL + "/token",
                HttpMethod.DELETE,
                new HttpEntity<>(body, headers),
                new ParameterizedTypeReference<CommonResult<Boolean>>() {}); // 解决 CommonResult 的泛型丢失
        Assert.isTrue(exchange.getStatusCode().is2xxSuccessful(), "响应必须是 200 成功");
        System.out.println(exchange.getBody());
        return exchange.getBody();
    }

    public CommonResult<List<DeptInfoRespDTO>> getAllOrg(){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//        headers.set("tenant-id", TENANT_ID.toString());
        addClientHeader(headers);
        // 1.2 构建请求参数
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "client_credentials");
//        body.add("state", ""); // 选填；填了会校验

        // 2. 执行请求
        ResponseEntity<CommonResult<OAuth2AccessTokenRespDTO>> exchange = restTemplate.exchange(
                this.BASE_URL + "/token",
                HttpMethod.POST,
                new HttpEntity<>(body, headers),
                new ParameterizedTypeReference<CommonResult<OAuth2AccessTokenRespDTO>>() {}); // 解决 CommonResult 的泛型丢失
        Assert.isTrue(exchange.getStatusCode().is2xxSuccessful(), "响应必须是 200 成功");

        CommonResult<OAuth2AccessTokenRespDTO> tokenDto = exchange.getBody();

        assert tokenDto != null;
        String accessToken = tokenDto.getData().getAccessToken();
        redisCache.setCacheObject("appToken", accessToken);

        return getBaseOrg(accessToken);
    }

    private CommonResult<List<DeptInfoRespDTO>> getBaseOrg(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//        headers.set("tenant-id", TENANT_ID.toString());
//        addClientHeader(headers);
        // 1.2 构建请求参数
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("token", accessToken);

        // 2. 执行请求
        ResponseEntity<CommonResult<List<DeptInfoRespDTO>>> exchange = restTemplate.exchange(
                this.BASE_URL + "/dept/list",
                HttpMethod.DELETE,
                new HttpEntity<>(body, headers),
                new ParameterizedTypeReference<CommonResult<List<DeptInfoRespDTO>>>() {}); // 解决 CommonResult 的泛型丢失
        Assert.isTrue(exchange.getStatusCode().is2xxSuccessful(), "响应必须是 200 成功");
        System.out.println(exchange.getBody());
        return exchange.getBody();
    }

    public CommonResult<List<UserInfoRespDTO>> getUserByOrg(Long deptId){
        String appToken = (String) redisCache.getCacheObject("appToken");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//        headers.set("tenant-id", TENANT_ID.toString());
//        addClientHeader(headers);
        // 1.2 构建请求参数
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("token", appToken);

        // 2. 执行请求
        ResponseEntity<CommonResult<List<UserInfoRespDTO>>> exchange = restTemplate.exchange(
                this.BASE_URL + "/user/list?deptId=" + deptId,
                HttpMethod.DELETE,
                new HttpEntity<>(body, headers),
                new ParameterizedTypeReference<CommonResult<List<UserInfoRespDTO>>>() {}); // 解决 CommonResult 的泛型丢失
        Assert.isTrue(exchange.getStatusCode().is2xxSuccessful(), "响应必须是 200 成功");
        System.out.println(exchange.getBody());
        return exchange.getBody();
    }

    private void addClientHeader(HttpHeaders headers) {
        // client 拼接，需要 BASE64 编码
        String client = this.CLIENT_ID + ":" + this.CLIENT_SECRET;
        client = Base64Utils.encodeToString(client.getBytes(StandardCharsets.UTF_8));
        headers.add("Authorization", "Basic " + client);
    }






}
