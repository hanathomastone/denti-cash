package com.kaii.dentix.domain.oralCheck.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kaii.dentix.domain.oralCheck.dao.OralCheckRepository;
import com.kaii.dentix.domain.oralCheck.domain.OralCheck;
import com.kaii.dentix.domain.oralCheck.dto.*;
import com.kaii.dentix.domain.oralCheck.dto.resoponse.OralCheckAnalysisResponse;
import com.kaii.dentix.domain.oralStatus.domain.OralStatus;
import com.kaii.dentix.domain.oralStatus.jpa.OralStatusRepository;
import com.kaii.dentix.domain.questionnaire.dao.QuestionnaireCustomRepository;
import com.kaii.dentix.domain.questionnaire.dao.QuestionnaireRepository;
import com.kaii.dentix.domain.questionnaire.domain.Questionnaire;
import com.kaii.dentix.domain.questionnaire.dto.OralStatusTypeDto;
import com.kaii.dentix.domain.questionnaire.dto.QuestionnaireAndStatusDto;
import com.kaii.dentix.domain.toothBrushing.dao.ToothBrushingCustomRepository;
import com.kaii.dentix.domain.toothBrushing.dao.ToothBrushingRepository;
import com.kaii.dentix.domain.toothBrushing.domain.ToothBrushing;
import com.kaii.dentix.domain.toothBrushing.dto.ToothBrushingDailyCountDto;
import com.kaii.dentix.domain.toothBrushing.dto.ToothBrushingDto;
import com.kaii.dentix.domain.type.OralDateStatusType;
import com.kaii.dentix.domain.type.OralSectionType;
import com.kaii.dentix.domain.type.oral.OralCheckAnalysisState;
import com.kaii.dentix.domain.type.oral.OralCheckDivisionCommentType;
import com.kaii.dentix.domain.type.oral.OralCheckDivisionScoreType;
import com.kaii.dentix.domain.type.oral.OralCheckResultTotalType;
import com.kaii.dentix.domain.user.application.UserService;
import com.kaii.dentix.domain.user.domain.User;
import com.kaii.dentix.domain.userOralStatus.dao.UserOralStatusRepository;
import com.kaii.dentix.domain.userOralStatus.domain.UserOralStatus;
import com.kaii.dentix.global.common.aws.AWSS3Service;
import com.kaii.dentix.global.common.error.exception.BadRequestApiException;
import com.kaii.dentix.global.common.error.exception.NotFoundDataException;
import com.kaii.dentix.global.common.response.DataResponse;
import com.kaii.dentix.global.common.util.DateFormatUtil;
import com.kaii.dentix.global.common.util.LambdaService;
import com.kaii.dentix.global.common.util.Utils;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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
    private final ToothBrushingRepository toothBrushingRepository;
    private final ToothBrushingCustomRepository toothBrushingCustomRepository;
    private final QuestionnaireRepository questionnaireRepository;
    private final QuestionnaireCustomRepository questionnaireCustomRepository;
    private final OralStatusRepository oralStatusRepository;
    private final UserOralStatusRepository userOralStatusRepository;

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

        List<OralCheck> oralCheckList = oralCheckRepository.findAllByUserIdOrderByCreatedDesc(user.getUserId());
        List<ToothBrushing> toothBrushingList = toothBrushingRepository.findAllByUserIdOrderByCreatedDesc(user.getUserId());
        List<Questionnaire> questionnaireList = questionnaireRepository.findAllByUserIdOrderByCreatedDesc(user.getUserId());
        List<UserOralStatus> userOralStatusList = userOralStatusRepository.findAllByQuestionnaireIn(questionnaireList);
        List<OralStatus> oralStatusList = oralStatusRepository.findAll();

        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();

        final String datePattern = "yyyy-MM-dd";

        // 구강 상태 화면 최상단 섹션 순서
        List<OralCheckSectionListDto> sectionList = new ArrayList<>();
        // 구강 촬영
        OralCheck latestOralCheck = oralCheckList.size() > 0 ? oralCheckList.get(0) : null;
        sectionList.add(OralCheckSectionListDto.builder()
            .sectionType(OralSectionType.ORAL_CHECK)
            .date(latestOralCheck != null ? latestOralCheck.getCreated() : null)
            .timeInterval(latestOralCheck != null ? (today.getTime() - latestOralCheck.getCreated().getTime()) / 1000 : null)
            .build());

        // 권장 촬영 기간
        String oralCheckPeriodBefore = null;
        String oralCheckPeriodAfter = null;
        if (latestOralCheck != null) {
            calendar.setTime(latestOralCheck.getCreated());
            calendar.add(Calendar.DATE, 6);
            oralCheckPeriodBefore = DateFormatUtil.dateToString(datePattern, calendar.getTime());
            calendar.add(Calendar.DATE, 2);
            oralCheckPeriodAfter = DateFormatUtil.dateToString(datePattern, calendar.getTime());
        }

        calendar.setTime(today);
        calendar.add(Calendar.DATE, -30); // 30일 전 기준

        // 양치질
        ToothBrushing latestToothBrushing = toothBrushingList.size() > 0 ? toothBrushingList.get(0) : null;
        sectionList.add(OralCheckSectionListDto.builder()
            .sectionType(OralSectionType.TOOTH_BRUSHING)
            .date(latestToothBrushing != null ? latestToothBrushing.getCreated() : null)
            .timeInterval(latestToothBrushing != null ? (today.getTime() - latestToothBrushing.getCreated().getTime()) / 1000 : null)
            .toothBrushingList(toothBrushingList.stream()
                .filter(toothBrushing -> DateFormatUtil.dateToString(datePattern, toothBrushing.getCreated())
                    .equals(DateFormatUtil.dateToString(datePattern, today)))
                .map(toothBrushing -> ToothBrushingDto.builder()
                    .toothBrushingId(toothBrushing.getToothBrushingId())
                    .created(toothBrushing.getCreated())
                    .build())
                .sorted(Comparator.comparing(ToothBrushingDto::getCreated))
                .collect(Collectors.toList())
            )
            .build());
        // 문진표
        Questionnaire latestQuestionnaire = questionnaireList.size() > 0 ? questionnaireList.get(0) : null;
        // 문진표 작성 이력이 없거나 30일이 지난 경우 두번째 아니면 세번째
        sectionList.add(latestQuestionnaire == null || latestQuestionnaire.getCreated().before(calendar.getTime()) ? 1 : 2, OralCheckSectionListDto.builder()
            .sectionType(OralSectionType.QUESTIONNAIRE)
            .date(latestQuestionnaire != null ? latestQuestionnaire.getCreated() : null)
            .timeInterval(latestQuestionnaire != null ? (today.getTime() - latestQuestionnaire.getCreated().getTime()) / 1000 : null)
            .build());

        // sectionList에 sort 값 추가
        for (int i = 0; i < sectionList.size(); i++) {
            sectionList.get(i).setSort(i + 1);
        }

        // 요일별 나의 구강 상태
        List<OralCheckDailyDto> dailyList = new ArrayList<>();
        calendar.add(Calendar.DATE, 1 - calendar.get(Calendar.DAY_OF_WEEK)); // 30일 전날이 포함된 일요일부터 시작

        while (calendar.getTime().before(today) || calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
            OralDateStatusType dailyStatusType = null;
            List<OralCheckListDto> detailList = new ArrayList<>();
            String dateString = DateFormatUtil.dateToString(datePattern, calendar.getTime());

            // 구강 촬영
            detailList.addAll(oralCheckList.stream()
                .filter(oralCheck -> DateFormatUtil.dateToString(datePattern, oralCheck.getCreated()).equals(dateString))
                .map(oralCheck -> OralCheckListDto.builder()
                    .sectionType(OralSectionType.ORAL_CHECK)
                    .date(oralCheck.getCreated())
                    .identifier(oralCheck.getOralCheckId())
                    .oralCheckResultTotalType(oralCheck.getOralCheckResultTotalType())
                    .build()
                ).toList());

            // 양치질
            List<ToothBrushing> dailyToothBrushingList = toothBrushingList.stream()
                .filter(toothBrushing -> DateFormatUtil.dateToString(datePattern, toothBrushing.getCreated()).equals(dateString)).toList();
            for (int i = 0; i < dailyToothBrushingList.size(); i++) {
                ToothBrushing toothBrushing = dailyToothBrushingList.get(i);
                detailList.add(OralCheckListDto.builder()
                    .sectionType(OralSectionType.TOOTH_BRUSHING)
                    .date(toothBrushing.getCreated())
                    .identifier(toothBrushing.getToothBrushingId())
                    .toothBrushingCount(dailyToothBrushingList.size() - i) // 역순
                    .build()
                );
            }

            // 문진표
            List<Questionnaire> dailyQuestionnaireList = questionnaireList.stream()
                .filter(questionnaire -> DateFormatUtil.dateToString(datePattern, questionnaire.getCreated()).equals(dateString)).toList();
            detailList.addAll(dailyQuestionnaireList.stream()
                .map(questionnaire -> OralCheckListDto.builder()
                    .sectionType(OralSectionType.QUESTIONNAIRE)
                    .date(questionnaire.getCreated())
                    .identifier(questionnaire.getQuestionnaireId())
                    .oralStatusList(userOralStatusList.stream()
                        .filter(userOralStatus -> userOralStatus.getQuestionnaire().equals(questionnaire))
                        .map(userOralStatus -> {
                            OralStatus oralStatus = oralStatusList.stream()
                                .filter(o -> o.equals(userOralStatus.getOralStatus()))
                                .findAny().orElseThrow(() -> new NotFoundDataException("구강 상태 결과 정보가 없습니다."));

                            return OralStatusTypeDto.builder()
                                .type(oralStatus.getOralStatusType())
                                .title(oralStatus.getOralStatusTitle())
                                .build();
                        })
                        .toList())
                    .build()
                ).toList());

            // 전체 목록을 역순으로 정렬 후 가장 최신의 상태값 적용
            if (detailList.size() > 0) {
                detailList.sort(Comparator.comparing(OralCheckListDto::getDate).reversed());
                OralCheckListDto latestDto = detailList.get(0);
                switch (latestDto.getSectionType()) {
                    case ORAL_CHECK -> {
                        switch (latestDto.getOralCheckResultTotalType()) {
                            case HEALTHY -> dailyStatusType = OralDateStatusType.HEALTHY;
                            case GOOD -> dailyStatusType = OralDateStatusType.GOOD;
                            case ATTENTION -> dailyStatusType = OralDateStatusType.ATTENTION;
                            case DANGER -> dailyStatusType = OralDateStatusType.DANGER;
                        }
                    }
                    case QUESTIONNAIRE -> dailyStatusType = OralDateStatusType.QUESTIONNAIRE;
                }
            }
            // 권장 촬영기간
            if (dailyStatusType == null && latestOralCheck != null && dateString.compareTo(oralCheckPeriodBefore) >= 0 && dateString.compareTo(oralCheckPeriodAfter) <= 0) {
                dailyStatusType = OralDateStatusType.ORAL_CHECK_PERIOD;
            }

            dailyList.add(OralCheckDailyDto.builder()
                .date(calendar.getTime())
                .status(dailyStatusType)
                .questionnaire(dailyQuestionnaireList.size() > 0)
                .detailList(detailList)
                .build());

            calendar.add(Calendar.DATE, 1);
        }

        return OralCheckDto.builder()
                .sectionList(sectionList)
                .dailyList(dailyList)
                .build();
    }

    /**
     * 대시보드 조회
     */
    public DashboardDto dashboard(HttpServletRequest httpServletRequest) {
        User user = userService.getTokenUser(httpServletRequest);

        List<OralCheck> oralCheckList = oralCheckRepository.findAllByUserIdOrderByCreatedDesc(user.getUserId());

        // 구강 촬영을 한 번이라도 하지 않으면 아무 데이터도 보이지 않음
        if (oralCheckList.size() == 0) {
            return new DashboardDto();
        }

        OralCheck latestOralCheck = oralCheckList.get(0);

        List<ToothBrushingDailyCountDto> toothBrushingDailyCountList = toothBrushingCustomRepository.getDailyCount(user.getUserId());
        // 양치 수
        int toothBrushingTotalCount = toothBrushingDailyCountList.stream().mapToInt(ToothBrushingDailyCountDto::getCount).sum();

        QuestionnaireAndStatusDto latestQuestionnaire = questionnaireCustomRepository.getLatestQuestionnaireAndHigherStatus(user.getUserId());

        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();

        int oralCheckHealthyCount = 0;
        int oralCheckGoodCount = 0;
        int oralCheckAttentionCount = 0;
        int oralCheckDangerCount = 0;
        List<OralCheckDailyChangeDto> oralCheckDailyChangeList = new ArrayList<>();

        String beforeDate = "";
        for (int i = 0; i < oralCheckList.size(); i++) {
            OralCheck oralCheck = oralCheckList.get(i);
            // 구강 상태값 카운트
            switch (oralCheck.getOralCheckResultTotalType()) {
                case HEALTHY -> oralCheckHealthyCount++;
                case GOOD -> oralCheckGoodCount++;
                case ATTENTION -> oralCheckAttentionCount++;
                case DANGER -> oralCheckDangerCount++;
            }

            // 구강 상태 변화 추이
            if (oralCheckDailyChangeList.size() >= 10) {
                continue;
            }
            String dateString = DateFormatUtil.dateToString("yyyy-MM-dd", oralCheck.getCreated());
            if (beforeDate.equals(dateString)) {
                continue;
            }

            oralCheckDailyChangeList.add(0, new OralCheckDailyChangeDto(oralCheckList.size() - i, oralCheck.getOralCheckResultTotalType()));
            beforeDate = dateString;
        }

        // 5개 미만인 경우 미노출
        if (oralCheckDailyChangeList.size() < 5) {
            oralCheckDailyChangeList = new ArrayList<>();
        }

        return DashboardDto.builder()
            .oralCheckTimeInterval((today.getTime() - latestOralCheck.getCreated().getTime()) / 1000)
            .oralCheckTotalCount(oralCheckList.size())
            .oralCheckHealthyCount(oralCheckHealthyCount)
            .oralCheckGoodCount(oralCheckGoodCount)
            .oralCheckAttentionCount(oralCheckAttentionCount)
            .oralCheckDangerCount(oralCheckDangerCount)
            .toothBrushingTotalCount(toothBrushingTotalCount)
            .toothBrushingAverage(Utils.getDeleteDecimalValue((float) toothBrushingTotalCount / toothBrushingDailyCountList.size(), 1))
            .oralStatus(latestQuestionnaire != null ? new OralStatusTypeDto(latestQuestionnaire.getOralStatusType(), latestQuestionnaire.getOralStatusTitle()) : null)
            .questionnaireCreated(latestQuestionnaire != null ? latestQuestionnaire.getQuestionnaireCreated() : null)
            .oralCheckResultTotalType(latestOralCheck.getOralCheckResultTotalType())
            .oralCheckUpRightScoreType(latestOralCheck.getOralCheckUpRightScoreType())
            .oralCheckUpLeftScoreType(latestOralCheck.getOralCheckUpLeftScoreType())
            .oralCheckDownLeftScoreType(latestOralCheck.getOralCheckDownLeftScoreType())
            .oralCheckDownRightScoreType(latestOralCheck.getOralCheckDownRightScoreType())
            .oralCheckDailyList(oralCheckDailyChangeList)
            .build();
    }
}
