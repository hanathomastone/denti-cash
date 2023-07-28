package com.kaii.dentix.domain.serviceAgreement.application;

import com.kaii.dentix.domain.serviceAgreement.dao.ServiceAgreementRepository;
import com.kaii.dentix.domain.serviceAgreement.dto.ServiceAgreementDto;
import com.kaii.dentix.domain.serviceAgreement.dto.ServiceAgreementListDto;
import com.kaii.dentix.domain.serviceAgreement.dto.ServiceAgreementPathDto;
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
    public List<ServiceAgreementDto> serviceAgreementList() {

        return serviceAgreementRepository.findAll(Sort.by(Sort.Direction.ASC, "serviceAgreeSort")).stream()
                .map(serviceAgreement -> ServiceAgreementDto.builder()
                        .serviceAgreeId(serviceAgreement.getServiceAgreeId())
                        .serviceAgreeName(serviceAgreement.getServiceAgreeName())
                        .serviceAgreeMenuName(serviceAgreement.getServiceAgreeMenuName())
                        .serviceAgreeFooterName(serviceAgreement.getServiceAgreeFooterName())
                        .isServiceAgreeRequired(serviceAgreement.getIsServiceAgreeRequired())
                        .serviceAgreePath(serviceAgreement.getServiceAgreePath())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     *  약관 전체 조회
     */
    public ServiceAgreementListDto serviceAgreementPath(){

        List<ServiceAgreementPathDto> serviceAgreement = serviceAgreementRepository.findAll(Sort.by(Sort.Direction.ASC, "serviceAgreeSort")).stream()
                .map(path -> ServiceAgreementPathDto.builder()
                        .path(path.getServiceAgreePath())
                        .build())
                .collect(Collectors.toList());

        return ServiceAgreementListDto.builder().serviceAgreement(serviceAgreement).build();
    }

}