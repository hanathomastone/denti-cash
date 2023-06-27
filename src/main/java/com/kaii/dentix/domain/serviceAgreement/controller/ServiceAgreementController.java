package com.kaii.dentix.domain.serviceAgreement.controller;

import com.kaii.dentix.domain.serviceAgreement.application.ServiceAgreementService;
import com.kaii.dentix.domain.serviceAgreement.dto.response.ServiceAgreementListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*")
@RestController
@RequiredArgsConstructor
@RequestMapping("/service-agreement")
public class ServiceAgreementController {

    private final ServiceAgreementService serviceAgreementService;

    @GetMapping(name = "서비스 동의 목록 조회")
    public ServiceAgreementListResponse serviceAgreementList() {

        ServiceAgreementListResponse response = new ServiceAgreementListResponse(serviceAgreementService.serviceAgreementList());

        return response;
    }

}
