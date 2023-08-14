package com.kaii.dentix.domain.admin.admin.controller;

import com.kaii.dentix.domain.admin.admin.application.AdminService;
import com.kaii.dentix.domain.admin.admin.dto.AdminPasswordResetDto;
import com.kaii.dentix.domain.admin.admin.dto.AdminSignUpDto;
import com.kaii.dentix.domain.admin.admin.dto.request.AdminModifyPasswordRequest;
import com.kaii.dentix.domain.admin.admin.dto.request.AdminSignUpRequest;
import com.kaii.dentix.global.common.response.DataResponse;
import com.kaii.dentix.global.common.response.SuccessResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/account")
public class AdminController {

    private final AdminService adminService;

    /**
     *  관리자 등록
     */
    @PostMapping(name = "관리자 등록")
    public DataResponse<AdminSignUpDto> adminSignUp(@Valid @RequestBody AdminSignUpRequest request){
        DataResponse<AdminSignUpDto> response = new DataResponse<>(adminService.adminSignUp(request));
        return response;
    }

    /**
     *  관리자 비밀번호 변경
     */
    @PutMapping(value = "/password", name = "관리자 비밀번호 변경")
    public SuccessResponse adminModifyPassword(HttpServletRequest httpServletRequest, @Valid @RequestBody AdminModifyPasswordRequest request){
        adminService.adminModifyPassword(httpServletRequest, request);
        return new SuccessResponse();
    }

    /**
     *  관리자 삭제
     */
    @DeleteMapping(name = "관리자 삭제")
    public SuccessResponse adminDelete(@RequestParam Long adminId){
        adminService.adminDelete(adminId);
        return new SuccessResponse();
    }

    /**
     *  관리자 비밀번호 초기화
     */
    @PutMapping(value = "/reset-password", name = "관리자 비밀번호 초기화")
    public DataResponse<AdminPasswordResetDto> adminPasswordReset(@RequestParam Long adminId){
        DataResponse<AdminPasswordResetDto> response = new DataResponse<>(adminService.adminPasswordReset(adminId));
        return response;
    }

}
