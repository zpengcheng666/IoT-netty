//package com.sydh.common.core.filter;
//
//
//import com.sydh.common.client.OAuthClient;
//import com.sydh.common.client.SecurityUtils;
//import com.sydh.common.client.dto.CommonResult;
//import com.sydh.common.client.dto.LoginUser;
//import com.sydh.common.client.dto.OAuth2CheckTokenRespDTO;
//import org.springframework.stereotype.Component;
//import org.springframework.util.StringUtils;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import javax.annotation.Resource;
//import javax.servlet.FilterChain;
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//
//@Component
//public class TokenAuthenticationFilter extends OncePerRequestFilter {
//
//    @Resource
//    private OAuthClient oauth2Client;
//
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//        String token = SecurityUtils.obtainAuthorization(request, "Authorization");
//        if (StringUtils.hasText(token)) {
//            // 2. 基于 token 构建登录用户
//            LoginUser loginUser = buildLoginUserByToken(token);
//            // 3. 设置当前用户
//            if (loginUser != null) {
//                SecurityUtils.setLoginUser(loginUser, request);
//            }
//        }
//
//        // 继续过滤链
//        filterChain.doFilter(request, response);
//    }
//
//    private LoginUser buildLoginUserByToken(String token) {
//        try {
//            CommonResult<OAuth2CheckTokenRespDTO> accessTokenResult = oauth2Client.checkToken(token);
//            OAuth2CheckTokenRespDTO accessToken = accessTokenResult.getData();
//            if (accessToken == null) {
//                return null;
//            }
//            LoginUser loginUser = new LoginUser();
//            loginUser.setId(accessToken.getUserId());
//            loginUser.setUserType(accessToken.getUserType());
//            loginUser.setTenantId(accessToken.getTenantId());
//            loginUser.setScopes(accessToken.getScopes());
//            loginUser.setAccessToken(accessToken.getAccessToken());
//
//            // 构建登录用户
//            return loginUser;
//        } catch (Exception exception) {
//            // 校验 Token 不通过时，考虑到一些接口是无需登录的，所以直接返回 null 即可
//            return null;
//        }
//    }
//}
