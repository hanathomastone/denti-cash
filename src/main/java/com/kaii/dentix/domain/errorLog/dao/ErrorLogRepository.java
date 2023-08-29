package com.kaii.dentix.domain.errorLog.dao;

import com.kaii.dentix.domain.errorLog.domain.ErrorLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ErrorLogRepository extends JpaRepository<ErrorLog, Integer> {
    ErrorLog save(ErrorLog errorLog);
}
