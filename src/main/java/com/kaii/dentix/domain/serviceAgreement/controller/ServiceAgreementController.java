package com.kaii.dentix.domain.serviceAgreement.controller;

import com.kaii.dentix.domain.serviceAgreement.application.ServiceAgreementService;
import com.kaii.dentix.domain.serviceAgreement.dto.ServiceAgreementListDto;
import com.kaii.dentix.global.common.response.DataResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@RequiredArgsConstructor
@RequestMapping("/service-agreement")
public class ServiceAgreementController {

    private final ServiceAgreementService serviceAgreementService;

    /**
     * 약관 전체 조회
     */
    @GetMapping(name = "약관 전체 조회")
    public DataResponse<ServiceAgreementListDto> serviceAgreementPath() {
        DataResponse<ServiceAgreementListDto> response = new DataResponse<>(serviceAgreementService.serviceAgreementList());
        return response;
    }

}