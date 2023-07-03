package com.kaii.dentix.domain.userDeviceType.domain;

import com.kaii.dentix.domain.type.DeviceType;
import com.kaii.dentix.global.common.entity.TimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter @Builder
@AllArgsConstructor @NoArgsConstructor
@Table(name = "userDeviceType")
public class UserDeviceType extends TimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userDeviceTypeId;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "enum", nullable = false)
    private DeviceType userDeviceType;

    @Column(length = 10, nullable = false)
    private String userDeviceTypeMinVersion;

}
