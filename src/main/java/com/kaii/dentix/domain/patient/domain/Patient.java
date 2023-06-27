package com.kaii.dentix.domain.patient.domain;

import com.kaii.dentix.global.common.entity.TimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter @Builder
@AllArgsConstructor @NoArgsConstructor
@Table(name = "patient")
public class Patient extends TimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long patientId;

    @Column(length = 45, nullable = false)
    private String patientName;

    @Column(length = 45, nullable = false)
    private String patientPhoneNumber;

}
