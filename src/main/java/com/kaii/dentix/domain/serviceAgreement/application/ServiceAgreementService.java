package com.kaii.dentix.domain.serviceAgreement.application;

import com.kaii.dentix.domain.serviceAgreement.dao.ServiceAgreementRepository;
import com.kaii.dentix.domain.serviceAgreement.dto.ServiceAgreementDto;
import com.kaii.dentix.domain.serviceAgreement.dto.ServiceAgreementListDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ServiceAgreementService {

    private final ServiceAgreementRepository serviceAgreementRepository;

    /**
     * 서비스 동의 목록
     */
    public ServiceAgreementListDto serviceAgreementList() {

        List<ServiceAgreementDto> serviceAgreementList = serviceAgreementRepository.findAll(Sort.by(Sort.Direction.ASC, "serviceAgreeSort")).stream()
                .map(serviceAgreement -> ServiceAgreementDto.builder()
                        .id(serviceAgreement.getServiceAgreeId())
                        .name(serviceAgreement.getServiceAgreeName())
                        .menuName(serviceAgreement.getServiceAgreeMenuName())
                        .isServiceAgreeRequired(serviceAgreement.getIsServiceAgreeRequired())
                        .path(serviceAgreement.getServiceAgreePath())
                        .build())
                .collect(Collectors.toList());

        return ServiceAgreementListDto.builder().serviceAgreement(serviceAgreementList).build();
    }

}