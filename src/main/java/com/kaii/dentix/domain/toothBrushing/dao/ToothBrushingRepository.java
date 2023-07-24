package com.kaii.dentix.domain.toothBrushing.dao;

import com.kaii.dentix.domain.toothBrushing.domain.ToothBrushing;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ToothBrushingRepository extends JpaRepository<ToothBrushing, Long> {
}
