package com.kaii.dentix.domain.toothBrushing.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Getter @Builder
@AllArgsConstructor @NoArgsConstructor
public class ToothBrushingDto {

    private Long toothBrushingId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private Date created;

}
