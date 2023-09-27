package com.kaii.dentix.domain.oralCheck.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.kaii.dentix.domain.type.oral.OralCheckResultType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.Date;
import java.util.List;

@Getter @Builder
@AllArgsConstructor
public class OralCheckResultDto {

    private Long userId; // 사용자 고유 번호

    private OralCheckResultType oralCheckResultTotalType; // 전체 구강 상태 결과 타입

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private Date created; // 촬영일

    private Float oralCheckTotalRange; // 전체 플라그 퍼센트

    private Float oralCheckUpRightRange; // 구강 검진 우상 비율

    private OralCheckResultType oralCheckUpRightScoreType; // 우상 점수 유형

    private Float oralCheckUpLeftRange; // 구강 검진 좌상 비율

    private OralCheckResultType oralCheckUpLeftScoreType; // 좌상 점수 유형

    private Float oralCheckDownLeftRange; // 구강 검진 좌하 비율

    private OralCheckResultType oralCheckDownLeftScoreType; // 좌하 점수 유형

    private Float oralCheckDownRightRange; // 구강 검진 우하 비율

    private OralCheckResultType oralCheckDownRightScoreType; // 우하 점수 유형

    private List<String> oralCheckCommentList; // 부위별 구강 상태 코멘트

}
