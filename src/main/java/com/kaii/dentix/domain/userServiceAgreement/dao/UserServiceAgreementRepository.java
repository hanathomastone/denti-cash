package com.kaii.dentix.domain.userServiceAgreement.dao;

import com.kaii.dentix.domain.userServiceAgreement.domain.UserServiceAgreement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserServiceAgreementRepository extends JpaRepository<UserServiceAgreement, Long> {

    UserServiceAgreement save(UserServiceAgreement userServiceAgreement);

    Optional<UserServiceAgreement> findByServiceAgreeIdAndUserId(Long serviceAgreeId, Long userId);

}
