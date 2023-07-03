package com.kaii.dentix.domain.user.controller;

import com.kaii.dentix.domain.user.application.UserService;
import com.kaii.dentix.domain.user.dto.UserLoginDto;
import com.kaii.dentix.domain.user.dto.request.UserAutoLoginRequest;
import com.kaii.dentix.domain.user.dto.response.UserLoginResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
