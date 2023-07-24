package com.kaii.dentix.domain.oralCheck.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.kaii.dentix.domain.oralCheck.dao.OralCheckRepository;
import com.kaii.dentix.domain.oralCheck.domain.OralCheck;
import com.kaii.dentix.domain.oralCheck.dto.*;
import com.kaii.dentix.domain.oralCheck.dto.resoponse.OralCheckAnalysisResponse;
import com.kaii.dentix.domain.type.oral.*;
import com.kaii.dentix.domain.user.application.UserService;
import com.kaii.dentix.domain.user.domain.User;
import com.kaii.dentix.global.common.aws.AWSS3Service;
import com.kaii.dentix.global.common.error.exception.BadRequestApiException;
import com.kaii.dentix.global.common.error.exception.NotFoundDataException;
import com.kaii.dentix.global.common.response.DataResponse;
import com.kaii.dentix.global.common.util.LambdaService;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.multipart.MultipartFile;

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

    @Value("${s3.folderPath.oralCheck}")
    private String folderPath;

    /**
     *  구강검진 사진 촬영
     */
    @Transactional
    public DataResponse<OralCheckPhotoDto> oralCheckPhoto(HttpServletRequest httpServletRequest, MultipartFile file) throws IOException, NoSuchAlgorithmException, InvalidKeyException, InterruptedException  {
        User user = userService.getTokenUser(httpServletRequest);

        // 업로드 결과 경로 생성
        String uploadedUrl = awss3Service.upload(file, folderPath, true);

        try {

            TimeUnit.SECONDS.sleep(3);

            // 업로드 경로가 없을 경우, 파일 저장 실패
            if (StringUtils.isBlank(uploadedUrl)) throw new BadRequestApiException("파일 저장에 실패했습니다.");

            // 불필요 경로를 제외하고 추출
            String findText = "aws.com/";
            int pathIndex = uploadedUrl.indexOf(findText);
            String imagePath = uploadedUrl.substring(pathIndex + findText.length());

            imagePath = "public/upload/tooth_coloration_test/1635829930233_tooth.jpg"; // TODO : 수정 필요

            // 람다 AI 서버로 업로드 경로 전달 후, AI 분석 결과 받아옴
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
                // 분석 결과 상태가 '성공'일 경우
                if (oralCheck.getOralCheckAnalysisState() == OralCheckAnalysisState.SUCCESS) {
                    return new DataResponse<>(200, SUCCESS_MSG, new OralCheckPhotoDto(oralCheck.getOralCheckId()));
                } else {
                    // 분석 결과 상태가 '실패'일 경우
                    return new DataResponse<>(resultCode, "양치 상태 체크 확인을 실패했습니다. 재촬영 바랍니다.", null);
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

        // 부위별 구강 상태 Comment
        OralCheckDivisionCommentType divisionCommentType = null;

        int upDivision = upRightRange + upLeftRange; // 윗니
        int downDivision = downRightRange + downLeftRange; // 아랫니
        int rightDivision = upRightRange + downRightRange; // 오른쪽
        int leftDivision = upLeftRange + downLeftRange; // 왼쪽

        if (totalRange == 0) { // 전체 플라그 비율이 0일 경우
            divisionCommentType = HEALTHY;
        } else {
            boolean rightBool = rightDivision > 0;
            boolean leftBool = leftDivision > 0;
            boolean upBool = upDivision > 0;
            boolean downBool = downDivision > 0;

            boolean rightGtLeftBool = rightBool && leftBool ? rightDivision > leftDivision : rightBool; // true : 오른쪽, false : 왼쪽
            boolean downGtUpBool = upBool && downBool ? downDivision > upDivision : downBool; // true : 아래, false : 위

            divisionCommentType = rightGtLeftBool && downGtUpBool ? DR
                    : rightGtLeftBool && !downGtUpBool ? UR
                    : !rightGtLeftBool && downGtUpBool ? DL
                    : UL; // !rightGtLeftBool && !downGtUpBool --> UL
        }

        return divisionCommentType;
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

        int totalRange = totalGroupRatio != null ? totalGroupRatio < 1 ? 0 : round(totalGroupRatio) : 0; // 전체 비율

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

    /**
     * 구강 사진 분석 실패
     */
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

    /**
     *  구강 검진 결과
     */
    @Transactional(readOnly = true)
    public OralCheckResultDto oralCheckResult(HttpServletRequest httpServletRequest, Long oralCheckId){
        User user = userService.getTokenUser(httpServletRequest);

        OralCheck oralCheck = oralCheckRepository.findById(oralCheckId).orElseThrow(() -> new NotFoundDataException("존재하지 않는 구강 검진입니다."));

        if (!oralCheck.getUserId().equals(user.getUserId())) throw new BadRequestApiException("회원 정보와 구강 검진 정보가 일치하지 않습니다.");

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        return OralCheckResultDto.builder()
                .userId(user.getUserId())
                .oralCheckResultTotalType(oralCheck.getOralCheckResultTotalType())
                .created(formatter.format(oralCheck.getCreated()))
                .oralCheckTotalRange(oralCheck.getOralCheckTotalRange())
                .oralCheckUpRightRange(oralCheck.getOralCheckUpRightRange())
                .oralCheckUpRightScoreType(oralCheck.getOralCheckUpRightScoreType())
                .oralCheckUpLeftRange(oralCheck.getOralCheckUpLeftRange())
                .oralCheckUpLeftScoreType(oralCheck.getOralCheckUpLeftScoreType())
                .oralCheckDownLeftRange(oralCheck.getOralCheckDownLeftRange())
                .oralCheckDownLeftScoreType(oralCheck.getOralCheckDownLeftScoreType())
                .oralCheckDownRightRange(oralCheck.getOralCheckDownRightRange())
                .oralCheckDownRightScoreType(oralCheck.getOralCheckDownRightScoreType())
                .oralCheckDivisionCommentType(oralCheck.getOralCheckDivisionCommentType())
                .build();

    }

    /**
     *  구강 상태 조회
     */
    @Transactional(readOnly = true)
    public OralCheckDto oralCheck(HttpServletRequest httpServletRequest){
        User user = userService.getTokenUser(httpServletRequest);

        // 마지막 구강검진일
        Date latestOralCheck = null;

        // TODO : 문진표 작성일 & 양치질 완료일

        return OralCheckDto.builder()
                .latestOralCheck(latestOralCheck)
                .build();

    }

    /**
     *  요일별 구강 상태 조회
     */
    @Transactional(readOnly = true)
    public DailyOralCheckDto dailyOralCheck(HttpServletRequest httpServletRequest, String day){
        User user = userService.getTokenUser(httpServletRequest);

        // 마지막 구강검진일
        Date latestOralCheck = null;

        // 구강검진 목록 조회
        List<OralCheckListDto> oralCheckList = new ArrayList<>();
        List<OralCheck> oralCheckDescList = oralCheckRepository.findAllByUserIdOrderByCreatedDesc(user.getUserId());

        // 구강검진 기록이 있는 경우
        if (oralCheckDescList.size() > 0) {
            OralCheck lastOralCheck = oralCheckDescList.get(0);
            latestOralCheck = lastOralCheck.getCreated();

            // 구강 촬영 목록
            for (OralCheck oralCheck : oralCheckDescList) {
                oralCheckList.add(OralCheckListDto.builder()
                        .oralCheckDate(oralCheck.getCreated())
                        .oralCheckResult(oralCheck.getOralCheckResultTotalType())
                        .percent(oralCheck.getOralCheckTotalRange())
                        .build());
            }
        }

        // TODO : 문진표 리스트 & 양치질 리스트

        return DailyOralCheckDto.builder()
                .latestOralCheck(latestOralCheck)
                .oralCheckList(oralCheckList)
                .build();

    }

}
