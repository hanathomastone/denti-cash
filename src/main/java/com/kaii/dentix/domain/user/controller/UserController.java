package com.kaii.dentix.domain.user.controller;

import com.kaii.dentix.domain.user.application.UserService;
import com.kaii.dentix.domain.user.dto.UserLoginDto;
import com.kaii.dentix.domain.user.dto.request.UserAutoLoginRequest;
import com.kaii.dentix.domain.user.dto.request.UserPasswordVerifyRequest;
import com.kaii.dentix.domain.user.dto.response.UserLoginResponse;
import com.kaii.dentix.global.common.response.SuccessResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;


    /**
     *  사용자 자동 로그인
     */
    @PutMapping(value = "/auto-login", name = "사용자 자동 로그인")
    public UserLoginResponse userAutoLogin(HttpServletRequest servletRequest, @Valid @RequestBody UserAutoLoginRequest request){
        UserLoginDto userLoginDto = userService.userAutoLogin(servletRequest, request);
        UserLoginResponse userLoginResponse = new UserLoginResponse(userLoginDto);

        return userLoginResponse;
    }

    /**
     *  사용자 비밀번호 확인
     */
    @PostMapping(value = "/password-verify", name = "사용자 비밀번호 확인")
    public SuccessResponse userPasswordVerify(HttpServletRequest httpServletRequest, @Valid @RequestBody UserPasswordVerifyRequest request){
        userService.userPasswordVerify(httpServletRequest, request);
        return new SuccessResponse();
    }

}
