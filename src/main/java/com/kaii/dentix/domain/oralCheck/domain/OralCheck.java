package com.kaii.dentix.domain.oralCheck.domain;

import com.kaii.dentix.domain.type.oral.OralCheckAnalysisState;
import com.kaii.dentix.domain.type.oral.OralCheckDivisionCommentType;
import com.kaii.dentix.domain.type.oral.OralCheckDivisionScoreType;
import com.kaii.dentix.domain.type.oral.OralCheckResultTotalType;
import com.kaii.dentix.global.common.entity.TimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.type.SqlTypes;

@Entity
@Getter @Builder
@AllArgsConstructor @NoArgsConstructor
@Table(name = "oralCheck")
public class OralCheck extends TimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long oralCheckId;

    private Long userId;

    @Column(length = 200, nullable = false)
    private String oralCheckPicturePath; // 구강 검진 원본 사진 경로

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "enum")
    private OralCheckAnalysisState oralCheckAnalysisState; // 구강 검진 분석 상태

    private Integer oralCheckTotalRange; // 구강 검진 전체 비율

    private Integer oralCheckUpRightRange; // 구강 검진 우상 비율

    private Integer oralCheckUpLeftRange; // 구강 검진 좌상 비율

    private Integer oralCheckDownRightRange; // 구강 검진 우하 비율

    private Integer oralCheckDownLeftRange; // 구강 검진 좌하 비율

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private String oralCheckResultJsonData; // 결과 JSON data 전체

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "enum")
    private OralCheckResultTotalType oralCheckResultTotalType; // 종합 결과 유형

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "enum")
    private OralCheckDivisionCommentType oralCheckDivisionCommentType; // 부위별 구강 상태 코멘트

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "enum")
    private OralCheckDivisionScoreType oralCheckUpRightScoreType; // 우상 점수 유형

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "enum")
    private OralCheckDivisionScoreType oralCheckUpLeftScoreType; // 좌상 점수 유형

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "enum")
    private OralCheckDivisionScoreType oralCheckDownRightScoreType; // 우하 점수 유형

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "enum")
    private OralCheckDivisionScoreType oralCheckDownLeftScoreType; // 좌하 점수 유형

}
