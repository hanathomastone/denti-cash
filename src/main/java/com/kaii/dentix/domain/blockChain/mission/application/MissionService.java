package com.kaii.dentix.domain.blockChain.mission.application;

import com.kaii.dentix.domain.blockChain.mission.dao.MissionRepository;
import com.kaii.dentix.domain.blockChain.mission.domain.Mission;
import com.kaii.dentix.domain.blockChain.mission.dto.MissionListResponseDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;


@RequiredArgsConstructor
@Transactional
public class MissionService {

    private final MissionRepository missionRepository;

    public Page<MissionListResponseDto> searchMissions(String keyword, LocalDate startDate, LocalDate endDate, Boolean active, Pageable pageable) {
        return missionRepository.searchMissions(keyword, startDate, endDate, active, pageable)
                .map(MissionListResponseDto::from); // ✅ DTO 변환
    }
}