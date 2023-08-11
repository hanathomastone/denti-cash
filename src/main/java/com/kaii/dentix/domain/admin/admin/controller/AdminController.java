package com.kaii.dentix.domain.admin.admin.controller;

import com.kaii.dentix.domain.admin.admin.application.AdminService;
import com.kaii.dentix.domain.admin.admin.dto.request.AdminModifyPasswordRequest;
import com.kaii.dentix.global.common.response.SuccessResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/account")
public class AdminController {

    private final AdminService adminService;

    /**
     *  관리자 비밀번호 변경
     */
    @PutMapping(value = "/password", name = "관리자 비밀번호 변경")
    public SuccessResponse adminModifyPassword(HttpServletRequest httpServletRequest, @Valid @RequestBody AdminModifyPasswordRequest request){
        adminService.adminModifyPassword(httpServletRequest, request);
        return new SuccessResponse();
    }

}
