package com.kaii.dentix.domain.toothBrushing.dao;

import com.kaii.dentix.domain.toothBrushing.dto.ToothBrushingDailyCountDto;

import java.util.List;

public interface ToothBrushingCustomRepository {

    List<ToothBrushingDailyCountDto> getDailyCount(Long userId);
}

