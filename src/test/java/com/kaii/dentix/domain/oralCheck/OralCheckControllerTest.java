package com.kaii.dentix.domain.oralCheck;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kaii.dentix.common.ControllerTest;
import com.kaii.dentix.domain.oralCheck.application.OralCheckService;
import com.kaii.dentix.domain.oralCheck.controller.OralCheckController;
import com.kaii.dentix.domain.oralCheck.dto.*;
import com.kaii.dentix.domain.questionnaire.dto.OralStatusTypeDto;
import com.kaii.dentix.domain.toothBrushing.dto.ToothBrushingDto;
import com.kaii.dentix.domain.type.OralDateStatusType;
import com.kaii.dentix.domain.type.OralSectionType;
import com.kaii.dentix.domain.type.oral.OralCheckDivisionCommentType;
import com.kaii.dentix.domain.type.oral.OralCheckDivisionScoreType;
import com.kaii.dentix.domain.type.oral.OralCheckResultTotalType;
import com.kaii.dentix.global.common.response.DataResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.kaii.dentix.common.ApiDocumentUtils.getDocumentRequest;
import static com.kaii.dentix.common.ApiDocumentUtils.getDocumentResponse;
import static com.kaii.dentix.common.DocumentOptionalGenerator.*;
import static com.kaii.dentix.global.common.response.ResponseMessage.SUCCESS_MSG;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OralCheckController.class)
public class OralCheckControllerTest extends ControllerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
                .build();
    }

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OralCheckService oralCheckService;

    private OralCheckResultDto oralCheckResultDto(){
        return OralCheckResultDto.builder()
                .userId(1L)
                .oralCheckResultTotalType(OralCheckResultTotalType.DANGER)
                .created("2023-07-19")
                .oralCheckTotalRange(55)
                .oralCheckUpRightRange(73)
                .oralCheckUpRightScoreType(OralCheckDivisionScoreType.DANGER)
                .oralCheckUpLeftRange(70)
                .oralCheckUpLeftScoreType(OralCheckDivisionScoreType.DANGER)
                .oralCheckDownLeftRange(16)
                .oralCheckDownLeftScoreType(OralCheckDivisionScoreType.ATTENTION)
                .oralCheckDownRightRange(20)
                .oralCheckDownRightScoreType(OralCheckDivisionScoreType.ATTENTION)
                .oralCheckDivisionCommentType(OralCheckDivisionCommentType.UL)
                .build();
    }

    /**
     * 구강검진 사진 촬영
     */
    @Test
    public void oralCheckPhoto() throws Exception{

        // given
        MockMultipartFile file = new MockMultipartFile("file", "test1.jpg", MediaType.IMAGE_JPEG_VALUE, "hello file".getBytes());

        DataResponse<OralCheckPhotoDto> response = new DataResponse<>(200, SUCCESS_MSG, new OralCheckPhotoDto(1L));

        given(oralCheckService.oralCheckPhoto(any(HttpServletRequest.class), any(MultipartFile.class))).willReturn(response);

        ResultActions result = mockMvc.perform(multipart("/oralCheck/photo")
                .file(file)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "oralCheck-photo.고유경.AccessToken")
                .with(user("user").roles("USER"))
        );

        result.andExpect(status().isOk())
                .andExpect(jsonPath("rt").value(200))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(document("oralCheck/photo",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        responseFields(
                                fieldWithPath("rt").type(JsonFieldType.NUMBER).description("결과 코드"),
                                fieldWithPath("rtMsg").type(JsonFieldType.STRING).description("결과 메시지"),
                                fieldWithPath("response").type(JsonFieldType.OBJECT).description("결과 데이터"),
                                fieldWithPath("response.oralCheckId").type(JsonFieldType.NUMBER).optional().description("구강검진 고유 번호")
                        )
                ));

        verify(oralCheckService).oralCheckPhoto(any(HttpServletRequest.class), any(MultipartFile.class));

    }

    /**
     *  구강검진 결과
     */
    @Test
    public void oralCheckResult() throws Exception{

        // given
        given(oralCheckService.oralCheckResult(any(HttpServletRequest.class), any(Long.class))).willReturn(oralCheckResultDto());

        // when
        ResultActions resultActions = mockMvc.perform(
                RestDocumentationRequestBuilders.get("/oralCheck/result?oralCheckId={oralCheckId}", "5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "oralCheck-photo.고유경.AccessToken")
                        .with(user("user").roles("USER"))
        );

        // then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("rt").value(200))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(document("oralCheck/result",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        queryParameters(
                                parameterWithName("oralCheckId").description("구강검진 고유 번호")
                        ),
                        responseFields(
                                fieldWithPath("rt").type(JsonFieldType.NUMBER).description("결과 코드"),
                                fieldWithPath("rtMsg").type(JsonFieldType.STRING).description("결과 메세지"),
                                fieldWithPath("response").type(JsonFieldType.OBJECT).description("결과 데이터"),
                                fieldWithPath("response.userId").type(JsonFieldType.NUMBER).description("사용자 고유 번호"),
                                fieldWithPath("response.oralCheckResultTotalType").type(JsonFieldType.STRING).attributes(oralCheckResultTotalFormat()).description("전체 구강 상태"),
                                fieldWithPath("response.created").type(JsonFieldType.STRING).attributes(dateFormat()).description("구강 검진일"),
                                fieldWithPath("response.oralCheckTotalRange").type(JsonFieldType.NUMBER).description("전체 평균 플라그 비율"),
                                fieldWithPath("response.oralCheckUpRightRange").type(JsonFieldType.NUMBER).description("상악우측 플라그 비율"),
                                fieldWithPath("response.oralCheckUpRightScoreType").type(JsonFieldType.STRING).attributes(oralCheckDivisionScoreFormat()).description("상악우측 상태"),
                                fieldWithPath("response.oralCheckUpLeftRange").type(JsonFieldType.NUMBER).description("상악좌측 플라그 비율"),
                                fieldWithPath("response.oralCheckUpLeftScoreType").type(JsonFieldType.STRING).attributes(oralCheckDivisionScoreFormat()).description("상악좌측 상태"),
                                fieldWithPath("response.oralCheckDownLeftRange").type(JsonFieldType.NUMBER).description("하악좌측 플라그 비율"),
                                fieldWithPath("response.oralCheckDownLeftScoreType").type(JsonFieldType.STRING).attributes(oralCheckDivisionScoreFormat()).description("하악좌측 상태"),
                                fieldWithPath("response.oralCheckDownRightRange").type(JsonFieldType.NUMBER).description("하악우측 플라그 비율"),
                                fieldWithPath("response.oralCheckDownRightScoreType").type(JsonFieldType.STRING).attributes(oralCheckDivisionScoreFormat()).description("하악우측 상태"),
                                fieldWithPath("response.oralCheckDivisionCommentType").type(JsonFieldType.STRING).attributes(oralCheckDivisionCommentFormat()).description("부위별 구강 상태 코멘트")
                        )
                ));

        verify(oralCheckService).oralCheckResult(any(HttpServletRequest.class), any(Long.class));

    }

    /**
     * 구강 상태 조회
     */
    @Test
    public void oralCheck() throws Exception {
        final int oneDay = 86400000;
        Date date = new Date();
        OralCheckDto oralCheckDto = new OralCheckDto(
            Arrays.asList(
                OralCheckSectionListDto.builder()
                    .sort(1)
                    .sectionType(OralSectionType.ORAL_CHECK)
                    .date(date)
                    .timeInterval(1000L)
                    .build(),
                OralCheckSectionListDto.builder()
                    .sort(2)
                    .sectionType(OralSectionType.TOOTH_BRUSHING)
                    .date(date)
                    .timeInterval(1000L)
                    .toothBrushingList(List.of(new ToothBrushingDto(1L, date)))
                    .build(),
                OralCheckSectionListDto.builder()
                    .sort(3)
                    .sectionType(OralSectionType.ORAL_CHECK)
                    .date(date)
                    .timeInterval(1000L)
                    .build()
            ),
            Arrays.asList(
                OralCheckDailyDto.builder()
                    .date(date)
                    .status(OralDateStatusType.DANGER)
                    .questionnaire(true)
                    .detailList(Arrays.asList(
                        OralCheckListDto.builder()
                            .sectionType(OralSectionType.ORAL_CHECK)
                            .date(date)
                            .identifier(1)
                            .oralCheckResultTotalType(OralCheckResultTotalType.DANGER)
                            .build(),
                        OralCheckListDto.builder()
                            .sectionType(OralSectionType.TOOTH_BRUSHING)
                            .date(date)
                            .identifier(1)
                            .toothBrushingCount(2)
                            .build(),
                        OralCheckListDto.builder()
                            .sectionType(OralSectionType.QUESTIONNAIRE)
                            .date(date)
                            .identifier(1)
                            .oralStatusList(Arrays.asList(
                                new OralStatusTypeDto("D", "시린니 관리형"),
                                new OralStatusTypeDto("E", "보철 관리형")
                            ))
                            .build()
                    ))
                    .build(),
                OralCheckDailyDto.builder()
                    .date(new Date(date.getTime() + oneDay))
                    .status(OralDateStatusType.DANGER)
                    .questionnaire(false)
                    .detailList(new ArrayList<>())
                    .build()
            )
        );

        // given
        given(oralCheckService.oralCheck(any(HttpServletRequest.class))).willReturn(oralCheckDto);

        // when
        ResultActions resultActions = mockMvc.perform(
            RestDocumentationRequestBuilders.get("/oralCheck")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "oralCheck.이호준.AccessToken")
                .with(user("user").roles("USER"))
        );

        // then
        resultActions.andExpect(status().isOk())
            .andExpect(jsonPath("rt").value(200))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andDo(document("oralCheck",
                getDocumentRequest(),
                getDocumentResponse(),
                responseFields(
                    fieldWithPath("rt").type(JsonFieldType.NUMBER).description("결과 코드"),
                    fieldWithPath("rtMsg").type(JsonFieldType.STRING).description("결과 메세지"),
                    fieldWithPath("response").type(JsonFieldType.OBJECT).description("결과 데이터"),
                    fieldWithPath("response.sectionList").type(JsonFieldType.ARRAY).description("최상단 섹션 목록"),
                    fieldWithPath("response.sectionList[].sort").type(JsonFieldType.NUMBER).description("섹션 정렬"),
                    fieldWithPath("response.sectionList[].sectionType").type(JsonFieldType.STRING).attributes(oralSectionTypeFormat()).description("섹션 타입"),
                    fieldWithPath("response.sectionList[].date").type(JsonFieldType.STRING).optional().description("최근 시각"),
                    fieldWithPath("response.sectionList[].timeInterval").type(JsonFieldType.NUMBER).optional().attributes(timeIntervalFormat()).description("최근 시차"),
                    fieldWithPath("response.sectionList[].toothBrushingList").type(JsonFieldType.ARRAY).description("양치 목록"),
                    fieldWithPath("response.sectionList[].toothBrushingList[].toothBrushingId").type(JsonFieldType.NUMBER).description("양치 고유번호"),
                    fieldWithPath("response.sectionList[].toothBrushingList[].created").type(JsonFieldType.STRING).attributes(dateTimeFormat()).description("양치 시각"),
                    fieldWithPath("response.dailyList").type(JsonFieldType.ARRAY).description("날짜별 목록"),
                    fieldWithPath("response.dailyList[].date").type(JsonFieldType.STRING).attributes(dateFormat()).description("날짜"),
                    fieldWithPath("response.dailyList[].status").type(JsonFieldType.STRING).attributes(oralDateStatusTypeFormat()).optional().description("섹션 타입"),
                    fieldWithPath("response.dailyList[].questionnaire").type(JsonFieldType.BOOLEAN).description("문진표 작성 여부"),
                    fieldWithPath("response.dailyList[].detailList").type(JsonFieldType.ARRAY).description("날짜별 상세 목록"),
                    fieldWithPath("response.dailyList[].detailList[].sectionType").type(JsonFieldType.STRING).attributes(oralSectionTypeFormat()).description("섹션 타입"),
                    fieldWithPath("response.dailyList[].detailList[].date").type(JsonFieldType.STRING).attributes(dateTimeFormat()).description("최근 시각"),
                    fieldWithPath("response.dailyList[].detailList[].identifier").type(JsonFieldType.NUMBER).description("고유번호"),
                    fieldWithPath("response.dailyList[].detailList[].oralCheckResultTotalType").type(JsonFieldType.STRING).optional().attributes(oralCheckResultTotalFormat()).description("전체 구강 상태"),
                    fieldWithPath("response.dailyList[].detailList[].toothBrushingCount").type(JsonFieldType.NUMBER).optional().description("양치 횟수"),
                    fieldWithPath("response.dailyList[].detailList[].oralStatusList").type(JsonFieldType.ARRAY).description("구강 상태 목록"),
                    fieldWithPath("response.dailyList[].detailList[].oralStatusList[].type").type(JsonFieldType.STRING).description("구강 상태 타입"),
                    fieldWithPath("response.dailyList[].detailList[].oralStatusList[].title").type(JsonFieldType.STRING).description("구강 상태 제목")
                )
            ));

        verify(oralCheckService).oralCheck(any(HttpServletRequest.class));

    }

    /**
     * 대시보드 조회
     */
    @Test
    public void dashboard() throws Exception {
        DashboardDto dashboardDto = DashboardDto.builder()
            .oralCheckTimeInterval(1000L)
            .oralCheckTotalCount(10)
            .oralCheckHealthyCount(4)
            .oralCheckGoodCount(3)
            .oralCheckAttentionCount(2)
            .oralCheckDangerCount(1)
            .toothBrushingTotalCount(100)
            .toothBrushingAverage(1.5F)
            .oralStatus(new OralStatusTypeDto("A", "양치 관리형"))
            .questionnaireCreated(new Date())
            .oralCheckResultTotalType(OralCheckResultTotalType.GOOD)
            .oralCheckUpRightScoreType(OralCheckDivisionScoreType.HEALTHY)
            .oralCheckUpLeftScoreType(OralCheckDivisionScoreType.GOOD)
            .oralCheckDownLeftScoreType(OralCheckDivisionScoreType.ATTENTION)
            .oralCheckDownRightScoreType(OralCheckDivisionScoreType.DANGER)
            .oralCheckDailyList(Arrays.asList(
                new OralCheckDailyChangeDto(1, OralCheckResultTotalType.HEALTHY),
                new OralCheckDailyChangeDto(2, OralCheckResultTotalType.GOOD),
                new OralCheckDailyChangeDto(4, OralCheckResultTotalType.ATTENTION),
                new OralCheckDailyChangeDto(8, OralCheckResultTotalType.DANGER),
                new OralCheckDailyChangeDto(10, OralCheckResultTotalType.HEALTHY)
            ))
            .build();

        // given
        given(oralCheckService.dashboard(any(HttpServletRequest.class))).willReturn(dashboardDto);

        // when
        ResultActions resultActions = mockMvc.perform(
            RestDocumentationRequestBuilders.get("/oralCheck/dashboard")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "oralCheck-dashboard.이호준.AccessToken")
                .with(user("user").roles("USER"))
        );

        // then
        resultActions.andExpect(status().isOk())
            .andExpect(jsonPath("rt").value(200))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andDo(document("oralCheck/dashboard",
                getDocumentRequest(),
                getDocumentResponse(),
                responseFields(
                    fieldWithPath("rt").type(JsonFieldType.NUMBER).description("결과 코드"),
                    fieldWithPath("rtMsg").type(JsonFieldType.STRING).description("결과 메세지"),
                    fieldWithPath("response").type(JsonFieldType.OBJECT).description("결과 데이터"),
                    fieldWithPath("response.oralCheckTimeInterval").type(JsonFieldType.NUMBER).optional().attributes(timeIntervalFormat()).description("구강 촬영 시차 (촬영 이력 없는 경우 null)"),
                    fieldWithPath("response.oralCheckTotalCount").type(JsonFieldType.NUMBER).description("구강 촬영 횟수"),
                    fieldWithPath("response.oralCheckHealthyCount").type(JsonFieldType.NUMBER).description("구강 촬영 건강 횟수"),
                    fieldWithPath("response.oralCheckGoodCount").type(JsonFieldType.NUMBER).description("구강 촬영 양호 횟수"),
                    fieldWithPath("response.oralCheckAttentionCount").type(JsonFieldType.NUMBER).description("구강 촬영 주의 횟수"),
                    fieldWithPath("response.oralCheckDangerCount").type(JsonFieldType.NUMBER).description("구강 촬영 위험 횟수"),
                    fieldWithPath("response.toothBrushingTotalCount").type(JsonFieldType.NUMBER).description("양치 횟수"),
                    fieldWithPath("response.toothBrushingAverage").type(JsonFieldType.NUMBER).description("양치 일 평균"),
                    fieldWithPath("response.oralStatus").type(JsonFieldType.OBJECT).optional().description("구강 상태"),
                    fieldWithPath("response.oralStatus.type").type(JsonFieldType.STRING).description("구강 상태 타입"),
                    fieldWithPath("response.oralStatus.title").type(JsonFieldType.STRING).description("구강 상태 제목"),
                    fieldWithPath("response.questionnaireCreated").type(JsonFieldType.STRING).optional().attributes(dateTimeFormat()).description("최근 문진표 검사일"),
                    fieldWithPath("response.oralCheckResultTotalType").type(JsonFieldType.STRING).optional().attributes(oralCheckResultTotalFormat()).description("최근 구강상태"),
                    fieldWithPath("response.oralCheckUpRightScoreType").type(JsonFieldType.STRING).optional().attributes(oralCheckDivisionScoreFormat()).description("상악우측 상태"),
                    fieldWithPath("response.oralCheckUpLeftScoreType").type(JsonFieldType.STRING).optional().attributes(oralCheckDivisionScoreFormat()).description("상악좌측 상태"),
                    fieldWithPath("response.oralCheckDownLeftScoreType").type(JsonFieldType.STRING).optional().attributes(oralCheckDivisionScoreFormat()).description("하악좌측 상태"),
                    fieldWithPath("response.oralCheckDownRightScoreType").type(JsonFieldType.STRING).optional().attributes(oralCheckDivisionScoreFormat()).description("하악우측 상태"),
                    fieldWithPath("response.oralCheckDailyList").type(JsonFieldType.ARRAY).description("구강 상태 변화 추이"),
                    fieldWithPath("response.oralCheckDailyList[].oralCheckNumber").type(JsonFieldType.NUMBER).description("회차"),
                    fieldWithPath("response.oralCheckDailyList[].oralCheckResultTotalType").attributes(oralCheckResultTotalFormat()).description("구강상태")
                )
            ));

        verify(oralCheckService).dashboard(any(HttpServletRequest.class));

    }

}
