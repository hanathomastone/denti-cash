package com.kaii.dentix.domain.systemLog.dao;

import com.kaii.dentix.domain.systemLog.domain.SystemLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SystemLogRepository extends JpaRepository<SystemLog, Integer> {
    SystemLog save(SystemLog systemLog);
}
