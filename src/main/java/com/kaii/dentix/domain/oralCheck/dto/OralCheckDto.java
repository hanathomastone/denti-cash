package com.kaii.dentix.domain.oralCheck.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Getter @Builder
@AllArgsConstructor @NoArgsConstructor
public class OralCheckDto {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private Date latestOralCheck; // 마지막 구강검진일

    // TODO : 문진표 작성일(경과일) , 양치질 완료일

}
