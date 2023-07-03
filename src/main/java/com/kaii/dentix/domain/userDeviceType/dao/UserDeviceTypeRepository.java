package com.kaii.dentix.domain.userDeviceType.dao;

import com.kaii.dentix.domain.type.DeviceType;
import com.kaii.dentix.domain.userDeviceType.domain.UserDeviceType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserDeviceTypeRepository extends JpaRepository<UserDeviceType, Long> {

    Optional<UserDeviceType> findByUserDeviceType(DeviceType type);

}
