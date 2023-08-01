package com.kaii.dentix.domain.admin.admin.controller;

import com.kaii.dentix.domain.admin.admin.application.AdminService;
import com.kaii.dentix.domain.admin.admin.dto.AdminSignUpDto;
import com.kaii.dentix.domain.admin.admin.dto.request.AdminSignUpRequest;
import com.kaii.dentix.global.common.response.DataResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    /**
     *  관리자 등록
     */
    @PostMapping(value = "/signUp", name = "관리자 등록")
    public DataResponse<AdminSignUpDto> adminSignUp(@Valid @RequestBody AdminSignUpRequest request){
        DataResponse<AdminSignUpDto> response = new DataResponse<>(adminService.adminSignUp(request));
        return response;
    }

}
