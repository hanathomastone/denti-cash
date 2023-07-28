package com.kaii.dentix.domain.serviceAgreement.application;

import com.kaii.dentix.domain.serviceAgreement.dao.ServiceAgreementRepository;
import com.kaii.dentix.domain.serviceAgreement.domain.ServiceAgreement;
import com.kaii.dentix.domain.serviceAgreement.dto.ServiceAgreementDto;
import com.kaii.dentix.domain.serviceAgreement.dto.ServiceAgreementListDto;
import com.kaii.dentix.global.common.error.exception.NotFoundDataException;
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
     * 서비스 동의 전체 조회
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
     *  약관 조회
     */
    public ServiceAgreementListDto serviceAgreement(Long serviceAgreeId){
        ServiceAgreement serviceAgreement = serviceAgreementRepository.findById(serviceAgreeId).orElseThrow(() -> new NotFoundDataException("존재하지 않는 약관입니다."));
        return ServiceAgreementListDto.builder().serviceAgreePath(serviceAgreement.getServiceAgreePath()).build();
    }

}