package com.quickstart.client.module.system.menu.service.impl;

import com.quickstart.base.domain.LoginUser;
import com.quickstart.base.domain.user.User;
import com.quickstart.client.module.business.user.service.UserService;
import com.quickstart.client.module.system.menu.service.PermissionService;
import com.quickstart.client.module.system.menu.mapper.MenuMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class PermissionServiceImpl implements PermissionService {

    @Resource
    private UserService userService;
    @Resource
    private MenuMapper menuMapper;

    @Override
    public LoginUser buildLoginUser(String memberCode) {
        User user = userService.findByMemberCode(memberCode);
        if (user == null || user.getStatus() == null || user.getStatus() != 1) {
            throw new IllegalArgumentException("用户不存在或已禁用");
        }
        List<String> permissions = menuMapper.selectPermissionsByUserCode(memberCode);
        if (permissions == null) {
            permissions = Collections.emptyList();
        }
        return new LoginUser(user, permissions);
    }
}
