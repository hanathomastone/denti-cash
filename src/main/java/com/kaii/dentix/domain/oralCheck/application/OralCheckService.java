package com.kaii.dentix.domain.oralCheck.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.kaii.dentix.domain.oralCheck.dao.OralCheckRepository;
import com.kaii.dentix.domain.oralCheck.domain.OralCheck;
import com.kaii.dentix.domain.oralCheck.dto.OralCheckAnalysisDivisionDto;
import com.kaii.dentix.domain.oralCheck.dto.OralCheckAnalysisTotalDto;
import com.kaii.dentix.domain.oralCheck.dto.resoponse.OralCheckAnalysisResponse;
import com.kaii.dentix.domain.oralCheck.dto.resoponse.OralCheckPhotoResponse;
import com.kaii.dentix.domain.type.oral.*;
import com.kaii.dentix.domain.user.application.UserService;
import com.kaii.dentix.domain.user.domain.User;
import com.kaii.dentix.global.common.aws.AWSS3Service;
import com.kaii.dentix.global.common.error.exception.BadRequestApiException;
import com.kaii.dentix.global.common.util.LambdaService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.databind.ObjectMapper;

import static com.kaii.dentix.domain.type.oral.OralCheckDivisionCommentType.*;
import static com.kaii.dentix.global.common.response.ResponseMessage.SUCCESS_MSG;
import static java.lang.Math.round;

@Service
@RequiredArgsConstructor
public class OralCheckService {

    private final UserService userService;

    private final AWSS3Service awss3Service;

    private final LambdaService lambdaService;

    private final OralCheckRepository oralCheckRepository;

    private final ObjectMapper objectMapper;

    @Value("public/upload/oralCheck/")
    private String folderPath;

    /**
     *  구강검진 사진 촬영
     */
    @Transactional
    public OralCheckPhotoResponse oralCheckPhoto(HttpServletRequest httpServletRequest, byte[] file, String contentType) throws IOException, NoSuchAlgorithmException, InvalidKeyException, InterruptedException  {
        User user = userService.getTokenUser(httpServletRequest);

        String uploadedUrl = awss3Service.upload(file, folderPath, contentType);

        try {

            TimeUnit.SECONDS.sleep(3);

            if (StringUtils.isBlank(uploadedUrl)) throw new BadRequestApiException("파일 저장에 실패했습니다.");

            String findText = "";
            int pathIndex = uploadedUrl.indexOf(findText);
            String imagePath = uploadedUrl.substring(pathIndex + findText.length());

            OralCheckAnalysisResponse analysisData = lambdaService.getPyDentalLambda(imagePath);

            OralCheck oralCheck = null;

            int resultCode = 500;

            switch (analysisData.getResultCode()) {
                case 0: // Analysis OK
                    // 1. 분석 결과 저장
                    oralCheck = registAnalysisSuccessData(user.getUserId(), uploadedUrl, analysisData);
                    break;
                case 3: // Segment Error
                case 4: // Division Error
                case 5: // Calculation Error
                case 10: // 사진 분석 실패
                    // 1. 분석 실패 저장 (잘못된 사진에서 일어난 에러)
                    oralCheck = registAnalysisFailedData(user.getUserId(), uploadedUrl, analysisData);
                    resultCode = 410;
                    break;
                case 1: // Bad Request
                case 2: // S3 Image Load Error
                default: // 그 외 Lambda Server Error
                    // 1. 분석 실패 저장 (서버에서 일어난 에러)
                    oralCheck = registAnalysisFailedData(user.getUserId(), uploadedUrl, analysisData);
                    resultCode = 411;
                    break;
            }

            if (oralCheck == null) {
                throw new BadRequestApiException("양치 상태 체크 확인 결과 저장에 실패했습니다... 관리자에게 문의 바랍니다.");
            } else {
                if (oralCheck.getOralCheckAnalysisState() == OralCheckAnalysisState.SUCCESS) {
                    return OralCheckPhotoResponse.builder()
                            .rt(200)
                            .rtMsg(SUCCESS_MSG)
                            .oralCheckId(oralCheck.getOralCheckId())
                            .build();
                } else {
                    return OralCheckPhotoResponse.builder()
                            .rt(resultCode)
                            .rtMsg("양치 상태 체크 확인을 실패했습니다. 재촬영 바랍니다.")
                            .build();
                }
            }

        } catch (InterruptedException e) {
            throw new BadRequestApiException("Thread interrupted");
        }

    }

    /**
     * 4등분 점수 유형 계산
     *
     * @param divisionRange : 영역 비율
     * @return ToothColoringDivisionScoreType : 4등분 점수 유형 결과
     */
    public OralCheckDivisionScoreType calcDivisionScoreType(Float divisionRange) {
        return divisionRange < 1 ? OralCheckDivisionScoreType.HEALTHY
                : divisionRange < 10 ? OralCheckDivisionScoreType.GOOD
                : divisionRange < 30 ? OralCheckDivisionScoreType.ATTENTION
                : OralCheckDivisionScoreType.DANGER;
    }

    /**
     * 4등분 코멘트 유형 계산
     *
     * @param totalRange     : 전체 비율
     * @param upRightRange   : 우상 비율
     * @param upLeftRange    : 좌상 비율
     * @param downRightRange : 우하 비율
     * @param downLeftRange  : 좌하 비율
     * @return ToothColoringDivisionCommentType : 4등분 코멘트 유형
     */
    public OralCheckDivisionCommentType calcDivisionCommentType(int totalRange, int upRightRange, int upLeftRange, int downRightRange, int downLeftRange) {
        // 4등분 코멘트 유형
        OralCheckDivisionCommentType divisionCommentType = null;

        int upDivision = upRightRange + upLeftRange; // 윗니
        int downDivision = downRightRange + downLeftRange; // 아랫니
        int rightDivision = upRightRange + downRightRange; // 오른쪽
        int leftDivision = upLeftRange + downLeftRange; // 왼쪽

        if (totalRange == 0) {
            divisionCommentType = HEALTHY;
        } else {
            boolean rightBool = rightDivision > 0;
            boolean leftBool = leftDivision > 0;
            boolean upBool = upDivision > 0;
            boolean downBool = downDivision > 0;

            boolean rightGtLeftBool = rightBool && leftBool ? rightDivision > leftDivision
                    : rightBool;
            boolean downGtUpBool = upBool && downBool ? downDivision > upDivision
                    : downBool;

            divisionCommentType = rightGtLeftBool && downGtUpBool ? DR
                    : rightGtLeftBool && !downGtUpBool ? UR
                    : !rightGtLeftBool && downGtUpBool ? DL
                    : UL; // !rightGtLeftBool && !downGtUpBool --> UL
        }

        return divisionCommentType;
    }

    /**
     * 플라그 분포 유형 계산
     *
     * @param totalRange         : 전체 비율
     * @param interproximalRange : 치간 비율
     * @param cervicalRange      : 치경 비율
     * @param labialRange        : 순면 비율
     * @return ToothColoringPlaqueDistributionType : 플라그 분포 유형 결과
     */
    public OralCheckPlaqueDistributionType calcPlaqueDistributionType(int totalRange, int interproximalRange, int cervicalRange, int labialRange) {
        OralCheckPlaqueDistributionType plaqueDistributionType = null;

        if (totalRange == 0) {
            plaqueDistributionType = OralCheckPlaqueDistributionType.S;
        } else {
            if (interproximalRange == cervicalRange && interproximalRange == labialRange) {
                plaqueDistributionType = OralCheckPlaqueDistributionType.ICL;
            } else {
                if (interproximalRange == cervicalRange) {
                    plaqueDistributionType = OralCheckPlaqueDistributionType.IC;
                } else if (interproximalRange == labialRange) {
                    plaqueDistributionType = OralCheckPlaqueDistributionType.IL;
                } else if (cervicalRange == labialRange) {
                    plaqueDistributionType = OralCheckPlaqueDistributionType.CL;
                }

                if (interproximalRange > cervicalRange && interproximalRange > labialRange) {
                    plaqueDistributionType = OralCheckPlaqueDistributionType.I;
                } else if (cervicalRange > interproximalRange && cervicalRange > labialRange) {
                    plaqueDistributionType = OralCheckPlaqueDistributionType.C;
                } else if (labialRange > interproximalRange && labialRange > cervicalRange) {
                    plaqueDistributionType = OralCheckPlaqueDistributionType.L;
                }
            }
        }
        return plaqueDistributionType;
    }

    /**
     * 플라그 사진 분석 결과 데이터 추출 및 저장 처리
     *
     * @param resource : 플라그 사진 분석 결과
     * @return ToothColorings : 업데이트된 치면착색검사 정보
     * @throws JsonProcessingException : Json to String 시 예외
     */
    @Transactional
    public OralCheck registAnalysisSuccessData(Long userId, String filePath, OralCheckAnalysisResponse resource) throws JsonProcessingException {
        OralCheckAnalysisDivisionDto tDivision = resource.getTDivision();
        Float upRightGroupRatio = tDivision.getUpRight().getGroup().getRatio();
        Float upLeftGroupRatio = tDivision.getUpLeft().getGroup().getRatio();
        Float downRightGroupRatio = tDivision.getDownRight().getGroup().getRatio();
        Float downLeftGroupRatio = tDivision.getDownLeft().getGroup().getRatio();

        int upRightRange = upRightGroupRatio != null ? upRightGroupRatio < 1 ? 0 : round(upRightGroupRatio) : 0; // 우상 비율
        int upLeftRange = upLeftGroupRatio != null ? upLeftGroupRatio < 1 ? 0 : round(upLeftGroupRatio) : 0; // 좌상 비율
        int downRightRange = downRightGroupRatio != null ? downRightGroupRatio < 1 ? 0 : round(downRightGroupRatio) : 0; // 우하 비율
        int downLeftRange = downLeftGroupRatio != null ? downLeftGroupRatio < 1 ? 0 : round(downLeftGroupRatio) : 0; // 좌하 비율

        OralCheckAnalysisTotalDto total = resource.getTotal();
        Float totalGroupRatio = total.getGroup().getRatio();
        Float totalInterproximalRatio = total.getInterproximal() != null ? total.getInterproximal().getRatio() : null;
        Float totalCervicalRatio = total.getCervical() != null ? total.getCervical().getRatio() : null;
        Float totalLabialRatio = total.getLabial() != null ? total.getLabial().getRatio() : null;

        int totalRange = totalGroupRatio != null ? totalGroupRatio < 1 ? 0 : round(totalGroupRatio) : 0; // 전체 비율
        int interproximalRange = totalInterproximalRatio != null ? round(totalInterproximalRatio) : 0; // 치간 비율
        int cervicalRange = totalCervicalRatio != null ? round(totalCervicalRatio) : 0; // 치경 비율
        int labialRange = totalLabialRatio != null ? round(totalLabialRatio) : 0; // 순면 비율

        String resultJsonData = objectMapper.writeValueAsString(resource); // 분석 결과 JSON data 전체

        // 4등분 코멘트 유형
        OralCheckDivisionCommentType divisionCommentType = this.calcDivisionCommentType(totalRange, upRightRange, upLeftRange, downRightRange, downLeftRange);

        // 4등분 점수 유형
        OralCheckDivisionScoreType upRightScoreType = this.calcDivisionScoreType(upRightGroupRatio);
        OralCheckDivisionScoreType upLeftScoreType = this.calcDivisionScoreType(upLeftGroupRatio);
        OralCheckDivisionScoreType downRightScoreType = this.calcDivisionScoreType(downRightGroupRatio);
        OralCheckDivisionScoreType downLeftScoreType = this.calcDivisionScoreType(downLeftGroupRatio);

        // 결과 종합 유형
        int divisionBadCount = 0;
        Float[] divisionRatio = {upRightGroupRatio, upLeftGroupRatio, downRightGroupRatio, downLeftGroupRatio};
        for (Float ratio : divisionRatio) {
            if (!(ratio < 10)) divisionBadCount++;
        }

        OralCheckResultTotalType resultTotalType = divisionBadCount == 0 ? OralCheckResultTotalType.HEALTHY
                : divisionBadCount == 1 ? OralCheckResultTotalType.GOOD
                : divisionBadCount == 2 ? OralCheckResultTotalType.ATTENTION
                : OralCheckResultTotalType.DANGER;

        // 플라그 분포 유형
        OralCheckPlaqueDistributionType plaqueDistributionType = this.calcPlaqueDistributionType(totalRange, interproximalRange, cervicalRange, labialRange);

        // insert 데이터 set
        OralCheck insertData = OralCheck.builder()
                .userId(userId)
                .oralCheckPicturePath(filePath)
                .oralCheckAnalysisState(OralCheckAnalysisState.SUCCESS)
                .oralCheckTotalRange(totalRange)
                .oralCheckUpRightRange(upRightRange)
                .oralCheckUpLeftRange(upLeftRange)
                .oralCheckDownRightRange(downRightRange)
                .oralCheckDownLeftRange(downLeftRange)
                .oralCheckResultJsonData(resultJsonData)
                .oralCheckResultTotalType(resultTotalType)
                .oralCheckDivisionCommentType(divisionCommentType)
                .oralCheckUpRightScoreType(upRightScoreType)
                .oralCheckUpLeftScoreType(upLeftScoreType)
                .oralCheckDownRightScoreType(downRightScoreType)
                .oralCheckDownLeftScoreType(downLeftScoreType)
                .build();

        // INSERT
        OralCheck inserted = oralCheckRepository.save(insertData);

        if (inserted == null) {
            throw new BadRequestApiException("양치 상태 체크 확인 결과 데이터 저장에 실패했습니다.");
        } else {
            return inserted;
        }
    }

    @Transactional
    public OralCheck registAnalysisFailedData(Long userId, String filePath, OralCheckAnalysisResponse resource) throws JsonProcessingException {
        String resultJsonData = objectMapper.writeValueAsString(resource); // 분석 결과 JSON data 전체

        // insert 데이터 set
        OralCheck insertData = OralCheck.builder()
                .userId(userId)
                .oralCheckPicturePath(filePath)
                .oralCheckAnalysisState(OralCheckAnalysisState.FAIL)
                .oralCheckResultJsonData(resultJsonData)
                .build();

        // INSERT
        OralCheck inserted = oralCheckRepository.save(insertData);
        if (inserted == null) {
            throw new BadRequestApiException("양치 상태 체크 확인 실패 데이터 저장에 실패했습니다.");
        } else {
            return inserted;
        }
    }

}
