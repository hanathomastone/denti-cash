package com.kaii.dentix.domain.serviceAgreement.dao;

import com.kaii.dentix.domain.serviceAgreement.domain.ServiceAgreement;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServiceAgreementRepository extends JpaRepository<ServiceAgreement, Long> {

    List<ServiceAgreement> findAll(Sort sort);

}
