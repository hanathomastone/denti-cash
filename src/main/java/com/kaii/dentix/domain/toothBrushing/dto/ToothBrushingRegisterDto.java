package com.kaii.dentix.domain.toothBrushing.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter @Builder
@AllArgsConstructor @NoArgsConstructor
public class ToothBrushingRegisterDto {

    List<ToothBrushingDto> toothBrushingList;
    Long timeInterval;
}
