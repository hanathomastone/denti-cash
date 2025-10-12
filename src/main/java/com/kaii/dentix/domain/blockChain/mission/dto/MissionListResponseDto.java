package com.kaii.dentix.domain.blockChain.mission.dto;

import com.kaii.dentix.domain.blockChain.mission.domain.Mission;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MissionListResponseDto {

    private Long id;
    private String name;
    private String condition;
    private String repeatType;
    private Double tokenReward;
    private Boolean active;
    private LocalDate startDate;
    private LocalDate endDate;

    public static MissionListResponseDto from(Mission mission) {
        return MissionListResponseDto.builder()
                .id(mission.getId())
                .name(mission.getName())
                .condition(mission.getCondition())
                .repeatType(mission.getRepeatType())
                .tokenReward(mission.getTokenReward())
                .active(mission.isActive())
                .startDate(mission.getStartDate())
                .endDate(mission.getEndDate())
                .build();
    }
}