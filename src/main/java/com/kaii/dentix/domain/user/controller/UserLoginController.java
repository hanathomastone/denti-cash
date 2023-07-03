package com.kaii.dentix.domain.user.controller;

import com.kaii.dentix.domain.user.application.UserLoginService;
import com.kaii.dentix.domain.user.dto.request.UserLoginRequest;
import com.kaii.dentix.domain.user.dto.request.UserSignUpRequest;
import com.kaii.dentix.domain.user.dto.request.UserVerifyRequest;
import com.kaii.dentix.domain.user.dto.response.UserLoginResponse;
import com.kaii.dentix.domain.user.dto.response.UserSignUpResponse;
import com.kaii.dentix.domain.user.dto.response.UserVerifyResponse;
import com.kaii.dentix.global.common.response.SuccessResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/login")
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
    public UserSignUpResponse userSignUp(HttpServletRequest httpServletRequest, @Valid @RequestBody UserSignUpRequest request){
        UserSignUpResponse userSignUpResponse = new UserSignUpResponse(userLoginService.userSignUp(httpServletRequest, request));
        return userSignUpResponse;
    }

    /**
     *  아이디 중복 확인
     */
    @GetMapping(value = "/loginId-check", name = "아이디 중복 확인")
    public SuccessResponse loginIdCheck(@RequestParam @NotBlank @Size(min = 4, max = 12) String userLoginId){
        userLoginService.loginIdCheck(userLoginId);
        return new SuccessResponse();
    }

    /**
     *  사용자 로그인
     */
    @PostMapping(name = "사용자 로그인")
    public UserLoginResponse userLogin(HttpServletRequest httpServletRequest, @Valid @RequestBody UserLoginRequest request){
        UserLoginResponse userLoginResponse = new UserLoginResponse(userLoginService.userLogin(httpServletRequest, request));
        return userLoginResponse;
    }

}
