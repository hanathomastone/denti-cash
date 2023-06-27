package com.kaii.dentix.domain.user.controller;

import com.kaii.dentix.domain.user.application.UserLoginService;
import com.kaii.dentix.domain.user.dto.request.UserSignUpRequest;
import com.kaii.dentix.domain.user.dto.request.UserVerifyRequest;
import com.kaii.dentix.domain.user.dto.response.UserSignUpResponse;
import com.kaii.dentix.domain.user.dto.response.UserVerifyResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserLoginController {

    private final UserLoginService userLoginService;

    /**
     *  사용자 회원인증
     */
    @PostMapping(value = "/verify", name = "사용자 회원 인증")
    public UserVerifyResponse userVerify(@Valid @RequestBody UserVerifyRequest request){
        UserVerifyResponse userVerifyResponse = new UserVerifyResponse(userLoginService.userVerify(request));
        return userVerifyResponse;
    }

    /**
     * 사용자 회원가입
     */
    @PostMapping(value = "/signUp", name = "사용자 회원가입")
    public UserSignUpResponse userSignUp(@Valid @RequestBody UserSignUpRequest request){
        UserSignUpResponse userSignUpResponse = new UserSignUpResponse(userLoginService.userSignUp(request));
        return userSignUpResponse;
    }

}
