package com.kaii.dentix.domain.serviceAgreement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter @Builder
@AllArgsConstructor @NoArgsConstructor
public class ServiceAgreementListDto {

    private List<ServiceAgreementDto> serviceAgreement;

}