package com.kaii.dentix.domain.oralStatus.jpa;

import com.kaii.dentix.domain.oralStatus.domain.OralStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OralStatusRepository extends JpaRepository<OralStatus, String> {
}
