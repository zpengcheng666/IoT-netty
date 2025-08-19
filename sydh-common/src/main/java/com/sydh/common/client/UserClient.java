package com.sydh.common.client;


import com.sydh.common.client.dto.CommonResult;
import com.sydh.common.client.dto.LoginUser;
import com.sydh.common.client.dto.UserInfoRespDTO;
import com.sydh.common.client.dto.UserUpdateReqDTO;
import com.sydh.common.client.vo.SubappRoleSyncReqVO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class UserClient {

    @Value("${sso.user-base-url}")
    private String BASE_URL;

    @Value("${sso.oauth2-base-url}")
    private String Oauth2_BASE_URL;

    @Value("${sso.bpmn-base-url}")
    private String BPMN_BASE_URL;

    @Value("${sso.client-id}")
    private String CLIENT_ID;
    @Value("${sso.client-secret}")
    private String CLIENT_SECRET;

    private final RestTemplate restTemplate = new RestTemplate();



    public CommonResult<UserInfoRespDTO> getUser() {
        // 1.1 构建请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//        headers.set("tenant-id", OAuth2Client.TENANT_ID.toString());
        addTokenHeader(headers);
        // 1.2 构建请求参数
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

        // 2. 执行请求
        ResponseEntity<CommonResult<UserInfoRespDTO>> exchange = restTemplate.exchange(BASE_URL + "/get",
                HttpMethod.GET, new HttpEntity<>(body, headers),
                new ParameterizedTypeReference<CommonResult<UserInfoRespDTO>>() {
                }); // 解决 CommonResult 的泛型丢失
        Assert.isTrue(exchange.getStatusCode().is2xxSuccessful(), "响应必须是 200 成功");
        return exchange.getBody();
    }

    public CommonResult<Boolean> updateUser(UserUpdateReqDTO updateReqDTO) {
        // 1.1 构建请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("tenant-id", OAuthClient.TENANT_ID.toString());
        addTokenHeader(headers);
        // 1.2 构建请求参数
        // 使用 updateReqDTO 即可

        // 2. 执行请求
        ResponseEntity<CommonResult<Boolean>> exchange = restTemplate.exchange(BASE_URL + "/update", HttpMethod.PUT,
                new HttpEntity<>(updateReqDTO, headers), new ParameterizedTypeReference<CommonResult<Boolean>>() {
                }); // 解决 CommonResult 的泛型丢失
        Assert.isTrue(exchange.getStatusCode().is2xxSuccessful(), "响应必须是 200 成功");
        return exchange.getBody();
    }


    public CommonResult<Object> syncRole() {
        // 1.1 构建请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("tenant-id", OAuthClient.TENANT_ID.toString());
        addTokenHeader(headers);

        // 2. 执行请求
        ResponseEntity<CommonResult<Object>> exchange = restTemplate.exchange(
                "http://127.0.0.1:48080/admin-api//system/subapp-role" + "/open-api-sync", HttpMethod.POST,
                new HttpEntity<>(new SubappRoleSyncReqVO("管理员1", "admin", "2"), headers),
                new ParameterizedTypeReference<CommonResult<Object>>() {
                }); // 解决 CommonResult 的泛型丢失
        Assert.isTrue(exchange.getStatusCode().is2xxSuccessful(), "响应必须是 200 成功");

        System.out.println(exchange.getBody().toString());
        return exchange.getBody();

    }





    private static void addTokenHeader(HttpHeaders headers) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        Assert.notNull(loginUser, "登录用户不能为空");
        headers.add("Authorization", "Bearer " + loginUser.getAccessToken());
    }


}
