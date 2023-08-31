package com.kaii.dentix.domain.oralCheck.dto;

import com.kaii.dentix.domain.type.oral.OralCheckDivisionScoreType;
import com.kaii.dentix.domain.type.oral.OralCheckResultTotalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter @Builder
@AllArgsConstructor
public class OralCheckResultDto {

    private Long userId; // 사용자 고유 번호

    private OralCheckResultTotalType oralCheckResultTotalType; // 전체 구강 상태 결과 타입

    private String created; // 촬영일

    private Integer oralCheckTotalRange; // 전체 플라그 퍼센트

    private Integer oralCheckUpRightRange; // 구강 검진 우상 비율

    private OralCheckDivisionScoreType oralCheckUpRightScoreType; // 우상 점수 유형

    private Integer oralCheckUpLeftRange; // 구강 검진 좌상 비율

    private OralCheckDivisionScoreType oralCheckUpLeftScoreType; // 좌상 점수 유형

    private Integer oralCheckDownLeftRange; // 구강 검진 좌하 비율

    private OralCheckDivisionScoreType oralCheckDownLeftScoreType; // 좌하 점수 유형

    private Integer oralCheckDownRightRange; // 구강 검진 우하 비율

    private OralCheckDivisionScoreType oralCheckDownRightScoreType; // 우하 점수 유형

    private List<String> oralCheckCommentList; // 부위별 구강 상태 코멘트

}
