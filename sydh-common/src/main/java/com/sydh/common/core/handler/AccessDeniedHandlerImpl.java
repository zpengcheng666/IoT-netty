package com.sydh.common.core.handler;


import com.sydh.common.client.SecurityUtils;
import com.sydh.common.client.dto.CommonResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@SuppressWarnings("JavadocReference")
@Slf4j
public class AccessDeniedHandlerImpl implements AccessDeniedHandler {


    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        log.warn("[commence][访问 URL({}) 时，用户({}) 权限不够]", request.getRequestURI(),
                SecurityUtils.getLoginUserId(), accessDeniedException);
        // 返回 403
        CommonResult<Object> result = new CommonResult<>();
        result.setCode(HttpStatus.FORBIDDEN.value());
        result.setMsg("没有该操作权限");
        ServletUtils.writeJSON(response, result);
    }
}
