package com.kaii.dentix.domain.user.controller;

import com.kaii.dentix.domain.user.application.UserService;
import com.kaii.dentix.domain.user.dto.UserLoginDto;
import com.kaii.dentix.domain.user.dto.request.*;
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

    /**
     *  사용자 보안정보수정 - 비밀번호 변경
     */
    @PutMapping(value = "/password", name = "사용자 보안정보수정 - 비밀번호 변경")
    public SuccessResponse userModifyPassword(HttpServletRequest httpServletRequest, @Valid @RequestBody UserInfoModifyPasswordRequest request){
        userService.userModifyPassword(httpServletRequest, request);
        return new SuccessResponse();
    }

    /**
     *  사용자 보안정보수정 - 질문과 답변 수정
     */
    @PutMapping(value = "/qna", name = "사용자 보안정보수정 - 질문과 답변 수정")
    public SuccessResponse userModifyQnA(HttpServletRequest httpServletRequest, @Valid @RequestBody UserInfoModifyQnARequest request){
        userService.userModifyQnA(httpServletRequest, request);
        return new SuccessResponse();
    }

    /**
     *  사용자 회원 정보 수정
     */
    @PutMapping(name = "사용자 회원 정보 수정")
    public SuccessResponse userModifyInfo(HttpServletRequest httpServletRequest, @Valid @RequestBody UserInfoModifyRequest request){
        userService.userModifyInfo(httpServletRequest, request);
        return new SuccessResponse();
    }

    /**
     *  사용자 로그아웃
     */
    @PutMapping(value = "/logout", name = "사용자 로그아웃")
    public SuccessResponse userLogout(HttpServletRequest httpServletRequest){
        userService.userLogout(httpServletRequest);
        return new SuccessResponse();
    }

    /**
     *  사용자 회원탈퇴
     */
    @DeleteMapping(name = "사용자 회원탈퇴")
    public SuccessResponse userRevoke(HttpServletRequest httpServletRequest){
        userService.userRevoke(httpServletRequest);
        return new SuccessResponse();
    }

}
