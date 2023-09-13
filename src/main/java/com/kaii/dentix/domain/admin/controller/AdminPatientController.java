package com.kaii.dentix.domain.admin.controller;

import com.kaii.dentix.domain.admin.application.AdminPatientService;
import com.kaii.dentix.domain.admin.dto.AdminRegisterPatientDto;
import com.kaii.dentix.domain.admin.dto.request.AdminRegisterPatientRequest;
import com.kaii.dentix.global.common.response.DataResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/patient")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class AdminPatientController {

    private final AdminPatientService adminPatientService;

    /**
     *  관리자 환자 등록
     */
    @PostMapping(name = "환자 등록")
    public DataResponse<AdminRegisterPatientDto> adminRegisterPatient(@RequestBody @Valid AdminRegisterPatientRequest request){
        DataResponse<AdminRegisterPatientDto> response = new DataResponse<>(adminPatientService.adminRegisterPatient(request));
        return response;
    }

}
