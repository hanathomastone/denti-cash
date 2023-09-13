package com.kaii.dentix.domain.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @Builder
@AllArgsConstructor @NoArgsConstructor
public class AdminPatientInfoDto {

    private String patientName; // 환자 이름

    private String patientPhoneNumber; // 환자 연락처

}
