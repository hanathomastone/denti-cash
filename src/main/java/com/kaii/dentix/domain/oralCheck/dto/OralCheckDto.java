package com.kaii.dentix.domain.oralCheck.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter @Builder
@AllArgsConstructor @NoArgsConstructor
public class OralCheckDto {

    List<OralCheckSectionListDto> sectionList;
    List<OralCheckDailyDto> dailyList;
}
