package com.kaii.dentix.domain.admin.admin.dao;

import com.kaii.dentix.domain.admin.admin.domain.Admin;
import com.kaii.dentix.domain.type.YnType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Long> {

    Optional<Admin> findByAdminLoginIdentifier(String adminIdentifier);

    Optional<Admin> findByAdminNameAndAdminPhoneNumberAndAdminIsSuper(String adminName, String adminPhoneNumber, YnType adminIsSuper);

}
