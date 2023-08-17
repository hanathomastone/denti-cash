package com.kaii.dentix.domain.admin.admin.controller;

import com.kaii.dentix.domain.admin.admin.application.AdminUserService;
import com.kaii.dentix.global.common.response.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/user")
public class AdminUserController {

    private final AdminUserService adminUserService;

    /**
     *  사용자 인증
     */
    @PutMapping("/verify")
    public SuccessResponse userVerify(@RequestParam Long userId){
        adminUserService.userVerify(userId);
        return new SuccessResponse();
    }

}
