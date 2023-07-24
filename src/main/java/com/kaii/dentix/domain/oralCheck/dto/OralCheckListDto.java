package com.kaii.dentix.domain.oralCheck.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.kaii.dentix.domain.type.oral.OralCheckResultTotalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 구강검진 리스트
 */
@Getter @Builder
@AllArgsConstructor @NoArgsConstructor
public class OralCheckListDto {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private Date oralCheckDate; // 구강검진일

    private OralCheckResultTotalType oralCheckResult; // 구강검진 결과 타입

    private Integer percent; // 구강검진 플라그 퍼센트

}
