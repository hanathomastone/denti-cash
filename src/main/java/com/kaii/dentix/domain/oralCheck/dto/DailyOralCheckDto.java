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

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private Date latestOralCheck; // 마지막 구강검진일

    private List<OralCheckListDto> oralCheckList; // 구강검진 리스트

    // TODO : 문진표 리스트, 양치질 리스트

}
