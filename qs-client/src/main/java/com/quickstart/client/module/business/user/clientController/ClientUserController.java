package com.quickstart.client.module.business.user.clientController;

import com.quickstart.base.common.annotation.NoNeedLogin;
import com.quickstart.base.domain.user.dto.ClientLoginDTO;
import com.quickstart.base.domain.user.dto.ClientRegisterDTO;
import com.quickstart.base.domain.user.dto.UpdateInfoDTO;
import com.quickstart.base.domain.user.vo.LoginResponse;
import com.quickstart.base.domain.user.vo.UserInfoVO;
import com.quickstart.base.domain.user.User;
import com.quickstart.base.domain.ResponseDTO;
import com.quickstart.base.security.SecurityUserContext;
import com.quickstart.client.module.business.user.service.AuthService;
import com.quickstart.client.module.business.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Validated
@Tag(name = "Client User")
@RestController
public class ClientUserController {

    @Resource
    private UserService userService;
    @Resource
    private AuthService authService;


    @PostMapping("/client/user/register")
    @Operation(summary = "客户端用户注册")
    @NoNeedLogin
    public ResponseDTO<UserInfoVO> register(@RequestBody @Valid ClientRegisterDTO request) {
        log.info("收到请求：/client/user/register");
        User user = userService.registerClient(request);
        return ResponseDTO.ok(UserInfoVO.fromEntity(user));
    }


    @PostMapping("/client/user/login")
    @Operation(summary = "客户端用户登录")
    @NoNeedLogin
    public ResponseDTO<LoginResponse> login(@RequestBody @Valid ClientLoginDTO request) {
        log.info("收到请求：/client/user/login");
        LoginResponse loginResponse = authService.clientLogin(request);
        return ResponseDTO.ok(loginResponse);
    }


    @GetMapping("/client/user/me")
    @Operation(summary = "获取当前客户端用户信息")
    public ResponseDTO<UserInfoVO> profile() {
        log.info("收到请求：/client/user/me");
        String memberCode = SecurityUserContext.getCurrentMemberCode();
        UserInfoVO vo = authService.currentUser(memberCode);
        return ResponseDTO.ok(vo);
    }


    @PostMapping("/client/user/updateInfo")
    @Operation(summary = "修改用户信息")
    public ResponseDTO<UserInfoVO> updateInfo(@RequestBody @Valid UpdateInfoDTO updateInfoDTO) {
        log.info("收到请求：/client/user/updateInfo");
        String userCode = SecurityUserContext.getCurrentMemberCode();
        userService.updateInfo(userCode,updateInfoDTO);
        return ResponseDTO.ok();
    }



}
