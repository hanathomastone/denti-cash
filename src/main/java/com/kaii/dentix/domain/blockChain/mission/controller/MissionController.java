package com.kaii.dentix.domain.blockChain.mission.controller;

import com.kaii.dentix.domain.blockChain.mission.application.MissionService;
import com.kaii.dentix.domain.blockChain.mission.dto.MissionListResponseDto;
import com.kaii.dentix.global.common.response.DataResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
@RestController
@RequestMapping("/admin/missions")
@RequiredArgsConstructor
public class MissionController {

    private final MissionService missionService;

    /** 미션 검색 + 페이징 */
    @GetMapping("/search")
    public ResponseEntity<DataResponse<Page<MissionListResponseDto>>> searchMissions(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @RequestParam(required = false) Boolean active,
            Pageable pageable
    ) {
        Page<MissionListResponseDto> result = missionService.searchMissions(keyword, startDate, endDate, active, pageable);
        return ResponseEntity.ok(new DataResponse<>(200, "미션 검색 결과", result));
    }
}