package com.kaii.dentix.domain.admin.controller;

import com.kaii.dentix.domain.admin.application.AdminLoginService;
import com.kaii.dentix.domain.admin.dto.AdminLoginDto;
import com.kaii.dentix.domain.admin.dto.request.AdminLoginRequest;
import com.kaii.dentix.global.common.response.DataResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminLoginController {

    private final AdminLoginService adminLoginService;

    /**
     *  관리자 로그인
     */
    @PostMapping(value = "/login", name = "관리자 로그인")
    public DataResponse<AdminLoginDto> adminLogin(@Valid @RequestBody AdminLoginRequest request){
        DataResponse<AdminLoginDto> response = new DataResponse<>(adminLoginService.adminLogin(request));
        return response;
    }

}
