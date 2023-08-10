package com.kaii.dentix.domain.serviceAgreement.dao;

import com.kaii.dentix.domain.serviceAgreement.domain.ServiceAgreement;
import com.kaii.dentix.domain.type.ServiceAgreeType;
import com.kaii.dentix.domain.type.YnType;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ServiceAgreementRepository extends JpaRepository<ServiceAgreement, Long> {

    List<ServiceAgreement> findAll(Sort sort);

    Optional<ServiceAgreement> findByServiceAgreeType(ServiceAgreeType serviceAgreeType);

    List<ServiceAgreement> findAllByIsServiceAgreeRequired(YnType isServiceAgreeRequired);

}
