package com.kaii.dentix.domain.blockChain.mission.domain;

import com.kaii.dentix.global.common.entity.TimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "mission")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Mission extends TimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mission_id")
    private Long id;

    @Column(nullable = false, length = 100)
    private String name; // 미션명

    @Column(name = "mission_condition", nullable = false, length = 500)
    private String condition; // 조건 설명 ✅ 예약어 피하기

    @Column(nullable = false, length = 50)
    private String repeatType; // 반복 여부

    @Column(nullable = false)
    private Double tokenReward; // 지급 토큰 수

    @Column(nullable = false)
    private Boolean active; // 활성화 여부

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;
}