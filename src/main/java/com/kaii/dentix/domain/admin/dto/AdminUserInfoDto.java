package com.kaii.dentix.domain.admin.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.kaii.dentix.domain.type.YnType;
import com.kaii.dentix.domain.type.oral.OralCheckResultTotalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Getter @Builder
@AllArgsConstructor @NoArgsConstructor
public class AdminUserInfoDto {

    private Long userId; // 사용자 고유 번호

    private String userLoginIdentifier; // 사용자 아이디

    private String userName; // 사용자 이름

    private String oralStatus; // 문진표 유형

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private Date questionnaireDate; // 문진표 검사일

    private OralCheckResultTotalType oralCheckResultTotalType; // 구강 상태 (구강 검진 결과)

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private Date oralCheckDate; // 구강 촬영일

    private YnType isVerify; // 사용자 인증 여부

}
