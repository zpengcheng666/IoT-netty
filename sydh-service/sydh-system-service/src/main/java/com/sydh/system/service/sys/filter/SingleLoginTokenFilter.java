package com.sydh.system.service.sys.filter;

import com.sydh.common.client.OAuthClient;
import com.sydh.common.client.SecurityUtils;
import com.sydh.common.client.UserClient;
import com.sydh.common.client.dto.CommonResult;
import com.sydh.common.client.dto.OAuth2CheckTokenRespDTO;
import com.sydh.common.client.dto.UserInfoRespDTO;
import com.sydh.common.constant.Constants;
import com.sydh.common.extend.core.domin.entity.SysUser;
import com.sydh.common.extend.core.domin.model.LoginUser;
import com.sydh.system.service.ISysUserService;
import com.sydh.system.service.sys.SysPermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Set;


@Component
public class SingleLoginTokenFilter extends OncePerRequestFilter {

    @Resource
    private OAuthClient oauth2Client;

    @Autowired
    private UserClient userClient;

    @Autowired
    private ISysUserService userService;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private SysPermissionService permissionService;

    @Value("${sso.user-base-url}")
    private String BASE_URL;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = SecurityUtils.obtainAuthorization(request, "Authorization");
        if (StringUtils.hasText(token)) {
            // 2. 基于 token 构建登录用户
            LoginUser loginUser = buildLoginUserByToken(token);
            // 3. 设置当前用户
            if (loginUser != null) {
                setLoginUser(loginUser, request);
            }
        }

        // 继续过滤链
        filterChain.doFilter(request, response);
    }

    private LoginUser buildLoginUserByToken(String token) {
        try {
            CommonResult<OAuth2CheckTokenRespDTO> accessTokenResult = oauth2Client.checkToken(token);
            OAuth2CheckTokenRespDTO accessToken = accessTokenResult.getData();
            if (accessToken == null) {
                return null;
            }
//            CommonResult<UserInfoRespDTO> user = userClient.getUser();
            SysUser currentUser = getCurrentUser(accessToken);
            LoginUser loginUser = new LoginUser();
            loginUser.setUserId(currentUser.getUserId());
            loginUser.setToken(accessToken.getAccessToken());
            loginUser.setDeptId(currentUser.getDeptId());
            loginUser.setUser(currentUser);
            loginUser.setLanguage(Constants.ZH_CN);
            loginUser.setPermissions(getPermissions(currentUser));

            // 构建登录用户
            return loginUser;
        } catch (Exception exception) {
            // 校验 Token 不通过时，考虑到一些接口是无需登录的，所以直接返回 null 即可
            return null;
        }
    }

    private Set<String> getPermissions(SysUser currentUser) {

        Set<String> rolePermission = permissionService.getMenuPermission(currentUser);
        return rolePermission;
    }


    public static void setLoginUser(LoginUser loginUser, HttpServletRequest request) {
        // 创建 Authentication，并设置到上下文
        Authentication authentication = buildAuthentication(loginUser, request);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private static Authentication buildAuthentication(LoginUser loginUser, HttpServletRequest request) {
        // 创建 UsernamePasswordAuthenticationToken 对象
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                loginUser, null, Collections.emptyList());
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        return authenticationToken;
    }

    private SysUser getCurrentUser(OAuth2CheckTokenRespDTO dto){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//        headers.set("tenant-id", OAuth2Client.TENANT_ID.toString());
        headers.add("Authorization", "Bearer " + dto.getAccessToken());
        // 1.2 构建请求参数
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

        // 2. 执行请求
        ResponseEntity<CommonResult<UserInfoRespDTO>> exchange = restTemplate.exchange(BASE_URL + "/get",
                HttpMethod.GET, new HttpEntity<>(body, headers),
                new ParameterizedTypeReference<CommonResult<UserInfoRespDTO>>() {
                }); // 解决 CommonResult 的泛型丢失
        Assert.isTrue(exchange.getStatusCode().is2xxSuccessful(), "响应必须是 200 成功");
        CommonResult<UserInfoRespDTO> body1 = exchange.getBody();
        UserInfoRespDTO data = body1.getData();
        SysUser sysUser = userService.selectUserByUserName(data.getUsername());
        return sysUser;

    }
}

