package com.kaii.dentix.domain.serviceAgreement.dao;

import com.kaii.dentix.domain.userServiceAgreement.dto.UserServiceAgreeList;

import java.util.List;

public interface ServiceAgreementCustomRepository {

    List<UserServiceAgreeList> findAll(Long userId);

}
