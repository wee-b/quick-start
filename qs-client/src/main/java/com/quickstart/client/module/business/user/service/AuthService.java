package com.quickstart.client.module.business.user.service;

import com.quickstart.base.domain.user.dto.AdminLoginRequest;
import com.quickstart.base.domain.user.dto.ClientLoginDTO;
import com.quickstart.base.domain.user.vo.LoginResponse;
import com.quickstart.base.domain.user.vo.UserInfoVO;

public interface AuthService {
    LoginResponse adminLogin(AdminLoginRequest request);

    LoginResponse clientLogin(ClientLoginDTO request);

    UserInfoVO currentUser(String memberCode);
}
