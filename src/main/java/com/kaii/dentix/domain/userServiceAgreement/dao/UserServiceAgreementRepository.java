package com.kaii.dentix.domain.userServiceAgreement.dao;

import com.kaii.dentix.domain.userServiceAgreement.domain.UserServiceAgreement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserServiceAgreementRepository extends JpaRepository<UserServiceAgreement, Long> {

    UserServiceAgreement save(UserServiceAgreement userServiceAgreement);

}
