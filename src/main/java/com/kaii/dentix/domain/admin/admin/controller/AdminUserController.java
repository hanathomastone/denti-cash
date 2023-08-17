package com.kaii.dentix.domain.admin.admin.controller;

import com.kaii.dentix.domain.admin.admin.application.AdminUserService;
import com.kaii.dentix.global.common.response.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/user")
public class AdminUserController {

    private final AdminUserService adminUserService;

    /**
     *  사용자 인증
     */
    @PutMapping(value = "/verify", name = "사용자 인증")
    public SuccessResponse userVerify(@RequestParam Long userId){
        adminUserService.userVerify(userId);
        return new SuccessResponse();
    }

    /**
     *  사용자 삭제
     */
    @DeleteMapping(name = "사용자 삭제")
    public SuccessResponse userDelete(@RequestParam Long userId){
        adminUserService.userDelete(userId);
        return new SuccessResponse();
    }

}
