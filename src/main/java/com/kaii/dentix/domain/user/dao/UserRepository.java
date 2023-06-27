package com.kaii.dentix.domain.user.dao;

import com.kaii.dentix.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByPatientId(Long patientId);

}