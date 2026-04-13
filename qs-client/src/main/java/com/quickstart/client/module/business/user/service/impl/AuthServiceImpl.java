package com.quickstart.client.module.business.user.service.impl;

import com.quickstart.base.domain.user.dto.AdminLoginRequest;
import com.quickstart.base.domain.user.dto.ClientLoginDTO;
import com.quickstart.base.domain.user.vo.LoginResponse;
import com.quickstart.base.domain.user.vo.UserInfoVO;
import com.quickstart.base.domain.user.User;
import com.quickstart.base.security.JwtTokenService;
import com.quickstart.base.security.SecurityUserContext;
import com.quickstart.base.security.TokenStoreService;
import com.quickstart.client.module.business.user.service.AuthService;
import com.quickstart.client.module.business.user.service.UserService;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {
    private final UserService userService;
    private final JwtTokenService jwtTokenService;
    private final TokenStoreService tokenStoreService;

    public AuthServiceImpl(UserService userService, JwtTokenService jwtTokenService, TokenStoreService tokenStoreService) {
        this.userService = userService;
        this.jwtTokenService = jwtTokenService;
        this.tokenStoreService = tokenStoreService;
    }

    @Override
    public LoginResponse adminLogin(AdminLoginRequest request) {
        User user = userService.adminLogin(request);
        String token = jwtTokenService.createToken(user.getUserCode());
        Claims claims = jwtTokenService.parseClaims(token);
        tokenStoreService.save(claims.getId(), user.getUserCode());
        return new LoginResponse(token, UserInfoVO.fromEntity(user));
    }

    @Override
    public LoginResponse clientLogin(ClientLoginDTO request) {
        User user = userService.clientLogin(request);
        String token = jwtTokenService.createToken(user.getUserCode());
        Claims claims = jwtTokenService.parseClaims(token);
        tokenStoreService.save(claims.getId(), user.getUserCode());
        return new LoginResponse(token, UserInfoVO.fromEntity(user));
    }

    @Override
    public UserInfoVO currentUser(String memberCode) {

        User user = userService.findByMemberCode(memberCode);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在或登录已失效");
        }
        return UserInfoVO.fromEntity(user);
    }
}
