package com.kaii.dentix.domain.serviceAgreement.controller;

import com.kaii.dentix.domain.serviceAgreement.application.ServiceAgreementService;
import com.kaii.dentix.domain.serviceAgreement.dto.ServiceAgreementListDto;
import com.kaii.dentix.domain.serviceAgreement.dto.response.ServiceAgreementListResponse;
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
     * 서비스 동의 목록 전체 조회
     */
    @GetMapping(name = "서비스 동의 목록 전체 조회")
    public ServiceAgreementListResponse serviceAgreementList() {

        ServiceAgreementListResponse response = new ServiceAgreementListResponse(serviceAgreementService.serviceAgreementList());

        return response;
    }

    /**
     *  약관 조회
     */
    @GetMapping(value = "/list", name = "약관 조회")
    public DataResponse<ServiceAgreementListDto> serviceAgreement(@RequestParam Long id){
        DataResponse<ServiceAgreementListDto> response = new DataResponse<>(serviceAgreementService.serviceAgreement(id));
        return response;
    }

}