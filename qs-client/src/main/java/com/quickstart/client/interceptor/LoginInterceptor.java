package com.quickstart.client.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quickstart.base.common.annotation.NoNeedLogin;
import com.quickstart.base.domain.ErrorCode;
import com.quickstart.base.domain.LoginUser;
import com.quickstart.base.domain.ResponseDTO;
import com.quickstart.base.security.JwtTokenService;
import com.quickstart.base.security.TokenStoreService;
import com.quickstart.client.module.system.menu.service.PermissionService;
import io.jsonwebtoken.Claims;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;


import java.lang.reflect.Method;

@Component
@Slf4j
public class LoginInterceptor implements HandlerInterceptor {

    @Resource
    private JwtTokenService jwtTokenService;
    @Resource
    private TokenStoreService tokenStoreService;
    @Resource
    private ObjectMapper objectMapper;
    @Resource
    private PermissionService permissionService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {


        // 1. OPTIONS 请求直接放行（跨域预检）
        if (HttpMethod.OPTIONS.toString().equals(request.getMethod())) {
            response.setStatus(HttpStatus.NO_CONTENT.value());
            return true;
        }

        // 2. 非 Controller 方法直接放行
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();

        // 3. 检查是否需要登录（方法/类上的 @NoNeedLogin）
        boolean isNoNeedLogin = method.isAnnotationPresent(NoNeedLogin.class)
                || handlerMethod.getBeanType().isAnnotationPresent(NoNeedLogin.class);
        if (isNoNeedLogin) {
            log.debug("{}#{} 无需登录，直接放行", handlerMethod.getBeanType().getSimpleName(), method.getName());
            return true;
        }

        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (!StringUtils.hasText(authHeader) || !authHeader.startsWith("Bearer ")) {
            writeUnauthorized(response, "未登录或token缺失");
            return false;
        }

        String token = authHeader.substring(7);
        try {
            Claims claims = jwtTokenService.parseClaims(token);
            String memberCode = claims.getSubject();
            String tokenId = claims.getId();
            if (!tokenStoreService.isValid(tokenId, memberCode)) {
                writeUnauthorized(response, "登录已失效，请重新登录");
                return false;
            }
            LoginUser loginUser = permissionService.buildLoginUser(memberCode);
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(loginUser,
                    null, loginUser.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return true;
        } catch (Exception ex) {
            SecurityContextHolder.clearContext();
            writeUnauthorized(response, "token无效或已过期");
            return false;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        SecurityContextHolder.clearContext();
    }

    private void writeUnauthorized(HttpServletResponse response, String msg) throws Exception {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setCharacterEncoding("UTF-8");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        ResponseDTO<Void> body = new ResponseDTO<>(ErrorCode.UNAUTHORIZED, false, msg, null);
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }


}
