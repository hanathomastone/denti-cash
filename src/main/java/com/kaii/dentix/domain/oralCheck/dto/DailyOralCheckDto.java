package com.kaii.dentix.domain.oralCheck.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Getter @Builder
@AllArgsConstructor @NoArgsConstructor
public class DailyOralCheckDto {

    private List<OralCheckListDto> oralCheckList; // 구강검진 리스트

    // TODO : 문진표 리스트, 양치질 리스트

}
