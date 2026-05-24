package com.quickstart.client.module.system.menu.service;

import com.quickstart.common.domain.LoginUser;

public interface PermissionService {

    LoginUser buildLoginUser(String memberCode);
}
