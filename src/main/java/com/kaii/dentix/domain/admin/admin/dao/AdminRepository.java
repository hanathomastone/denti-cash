package com.kaii.dentix.domain.admin.admin.dao;

import com.kaii.dentix.domain.admin.admin.domain.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Long> {

    Optional<Admin> findByAdminLoginIdentifier(String adminIdentifier);

    List<Admin> findByAdminNameOrAdminPhoneNumber(String adminName, String adminPhoneNumber);

}
