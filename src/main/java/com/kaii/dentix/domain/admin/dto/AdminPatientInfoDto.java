package com.kaii.dentix.domain.admin.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.kaii.dentix.domain.type.YnType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Getter @Builder
@AllArgsConstructor @NoArgsConstructor
public class AdminPatientInfoDto {

    private String patientName; // 환자 이름

    private String patientPhoneNumber; // 환자 연락처

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private Date created; // 환자 등록일

    private YnType isUser; // 연계된 대상자 존재 여부

    public AdminPatientInfoDto(String patientName, String patientPhoneNumber, Date created, String isUser){
        this.patientName = patientName;
        this.patientPhoneNumber = patientPhoneNumber;
        this.created = created;
        this.isUser = YnType.valueOf(isUser);
    }

}
