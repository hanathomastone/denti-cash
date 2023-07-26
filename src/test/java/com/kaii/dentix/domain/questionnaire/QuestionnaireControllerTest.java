package com.kaii.dentix.domain.questionnaire;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kaii.dentix.common.ControllerTest;
import com.kaii.dentix.domain.questionnaire.application.QuestionnaireService;
import com.kaii.dentix.domain.questionnaire.controller.QuestionnaireController;
import com.kaii.dentix.domain.questionnaire.dto.*;
import com.kaii.dentix.domain.questionnaire.dto.request.QuestionnaireSubmitRequest;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.kaii.dentix.common.ApiDocumentUtils.getDocumentRequest;
import static com.kaii.dentix.common.ApiDocumentUtils.getDocumentResponse;
import static com.kaii.dentix.common.DocumentOptionalGenerator.dateFormat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(QuestionnaireController.class)
public class QuestionnaireControllerTest extends ControllerTest {

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
    private QuestionnaireService questionnaireService;

    private QuestionnaireTemplateJsonDto questionnaireTemplateJsonDto() {
        List<QuestionnaireTemplateDto> list = Arrays.asList(
            QuestionnaireTemplateDto.builder()
                .sort(1)
                .key("q_1")
                .number("01")
                .title("현재 구강 건강 상태는 어떻다고 생각하십니까?")
                .description(null)
                .minimum(1)
                .maximum(1)
                .contents(Arrays.asList(
                    new QuestionnaireTemplateContentDto(1, 1, "매우 건강하다"),
                    new QuestionnaireTemplateContentDto(2, 2, "건강한 편이다"),
                    new QuestionnaireTemplateContentDto(3, 3, "보통이다"),
                    new QuestionnaireTemplateContentDto(4, 4, "건강하지 못한 편이다"),
                    new QuestionnaireTemplateContentDto(5, 5, "전혀 건강하지 않다")
                ))
                .build(),
            QuestionnaireTemplateDto.builder()
                .sort(3)
                .key("q_3")
                .number("03")
                .title("지난 12개월 동안 구강관련 불편감이 있었습니까?")
                .description("(중복 표시 가능)")
                .minimum(0)
                .maximum(null)
                .contents(Arrays.asList(
                    new QuestionnaireTemplateContentDto(1, 1, "아니요"),
                    new QuestionnaireTemplateContentDto(2, 2, "씹기 힘들다"),
                    new QuestionnaireTemplateContentDto(3, 3, "이가 아프다"),
                    new QuestionnaireTemplateContentDto(4, 4, "뜨겁고 찬 음식에 시리고 민감하다"),
                    new QuestionnaireTemplateContentDto(5, 5, "잇몸이 붓고 피가 난다"),
                    new QuestionnaireTemplateContentDto(6, 6, "입이 마른다"),
                    new QuestionnaireTemplateContentDto(7, 7, "입냄새가 난다")
                ))
                .build()
        );

        return new QuestionnaireTemplateJsonDto("v1", list);
    }

    /**
     * 문진표 양식 조회
     */
    @Test
    public void questionnaireTemplate() throws Exception{
        // given
        given(questionnaireService.getQuestionnaireTemplate()).willReturn(questionnaireTemplateJsonDto());

        // when
        ResultActions resultActions = mockMvc.perform(
            RestDocumentationRequestBuilders.get("/questionnaire/template")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "questionnaire-template.이호준.AccessToken")
                .with(user("user").roles("USER"))
        );

        // then
        resultActions.andExpect(status().isOk())
            .andExpect(jsonPath("rt").value(200))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andDo(document("questionnaire/template",
                getDocumentRequest(),
                getDocumentResponse(),
                responseFields(
                    fieldWithPath("rt").type(JsonFieldType.NUMBER).description("결과 코드"),
                    fieldWithPath("rtMsg").type(JsonFieldType.STRING).description("결과 메세지"),
                    fieldWithPath("response").type(JsonFieldType.OBJECT).description("결과 데이터"),
                    fieldWithPath("response.version").type(JsonFieldType.STRING).description("템플릿 버전"),
                    fieldWithPath("response.template").type(JsonFieldType.ARRAY).description("문진표 양식"),
                    fieldWithPath("response.template[].sort").type(JsonFieldType.NUMBER).description("문항 정렬"),
                    fieldWithPath("response.template[].key").type(JsonFieldType.STRING).description("문항 고유번호 (제출 시 필요)"),
                    fieldWithPath("response.template[].number").type(JsonFieldType.STRING).description("문항 제목 번호"),
                    fieldWithPath("response.template[].title").type(JsonFieldType.STRING).description("문항 제목"),
                    fieldWithPath("response.template[].description").type(JsonFieldType.STRING).optional().description("문항 설명"),
                    fieldWithPath("response.template[].minimum").type(JsonFieldType.NUMBER).description("문항 최소 개수"),
                    fieldWithPath("response.template[].maximum").type(JsonFieldType.NUMBER).optional().description("문항 최대 개수 (null인 경우 무제한)"),
                    fieldWithPath("response.template[].contents").type(JsonFieldType.ARRAY).description("문항 선택지"),
                    fieldWithPath("response.template[].contents[].sort").type(JsonFieldType.NUMBER).description("문항 선택지 정렬"),
                    fieldWithPath("response.template[].contents[].id").type(JsonFieldType.NUMBER).description("문항 선택지 고유번호 (제출 시 필요)"),
                    fieldWithPath("response.template[].contents[].text").type(JsonFieldType.STRING).description("문항 선택지 내용")
                )
            ));

        verify(questionnaireService).getQuestionnaireTemplate();
    }

    /**
     * 문진표 제출
     */
    @Test
    public void questionnaireSubmit() throws Exception{
        List<QuestionnaireKeyValueDto> form = Arrays.asList(
            new QuestionnaireKeyValueDto("q_1", new Integer[]{1}),
            new QuestionnaireKeyValueDto("q_2", new Integer[]{2}),
            new QuestionnaireKeyValueDto("q_3", new Integer[]{1, 2}),
            new QuestionnaireKeyValueDto("q_4", new Integer[]{3}),
            new QuestionnaireKeyValueDto("q_5", new Integer[]{3, 4}),
            new QuestionnaireKeyValueDto("q_6", new Integer[]{5, 6}),
            new QuestionnaireKeyValueDto("q_7", new Integer[]{0}),
            new QuestionnaireKeyValueDto("q_8", new Integer[]{7, 8}),
            new QuestionnaireKeyValueDto("q_9", new Integer[]{4}),
            new QuestionnaireKeyValueDto("q_10", new Integer[]{1})
        );
        QuestionnaireSubmitRequest request = new QuestionnaireSubmitRequest(form);

        // given
        given(questionnaireService.questionnaireSubmit(any(HttpServletRequest.class), any(QuestionnaireSubmitRequest.class))).willReturn(new QuestionnaireIdDto(1L));

        // when
        ResultActions resultActions = mockMvc.perform(
            RestDocumentationRequestBuilders.post("/questionnaire/submit")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "questionnaire-submit.이호준.AccessToken")
                .with(user("user").roles("USER"))
        );

        // then
        resultActions.andExpect(status().isOk())
            .andExpect(jsonPath("rt").value(200))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andDo(document("questionnaire/submit",
                getDocumentRequest(),
                getDocumentResponse(),
                requestFields(
                    fieldWithPath("form").type(JsonFieldType.ARRAY).description("문진표 항목"),
                    fieldWithPath("form[].key").type(JsonFieldType.STRING).description("문진표 key"),
                    fieldWithPath("form[].value").type(JsonFieldType.ARRAY).description("문진표 value")
                ),
                responseFields(
                    fieldWithPath("rt").type(JsonFieldType.NUMBER).description("결과 코드"),
                    fieldWithPath("rtMsg").type(JsonFieldType.STRING).description("결과 메세지"),
                    fieldWithPath("response").type(JsonFieldType.OBJECT).description("결과 데이터"),
                    fieldWithPath("response.questionnaireId").type(JsonFieldType.NUMBER).description("문진표 고유번호")
                )
            ));

        verify(questionnaireService).questionnaireSubmit(any(HttpServletRequest.class), any(QuestionnaireSubmitRequest.class));
    }

    /**
     * 문진표 결과 조회
     */
    @Test
    public void questionnaireResult() throws Exception{
        List<OralStatusTypeInfoDto> oralStatusList = Arrays.asList(
            new OralStatusTypeInfoDto("A", "양치 관리형", "양치 관리형은 현재 질환이 있거나 질환이 생길 수 있는 상태로, 양치 관리가 필요한 사람을 뜻해요.\r\n구강 위생이 다소 불량하여 치아 관리에 대한 학습이 필요하고, 양치질 습관을 개선할 필요가 있어요. 현재 구강 상태를 정확하게 검진받아 볼 필요성이 있고, 질환이 발생하기 전에 관리할 필요가 있어요. 양치질의 목적은 입 속의 치태, 치석 등을 제거하거나 예방해서 구강 건강을 유지하는 것이지만 잘못된 양치 습관은 치아를 마모시킬 수 있고 치아 민감도를 높이는 문제가 발생할 수 있으므로 반드시 올바른 양치 관리 방법을 확인할 필요가 있어요."),
            new OralStatusTypeInfoDto("B", "충치 관리형", "충치 관리형은 과거 충치 치료를 받았거나 충치가 발생할 가능성이 높은 생활 습관으로 충치 관리가 필요한 사람을 뜻해요. 충치는 음식물을 섭취한 후 남아있는 음식물 찌꺼기가 입 속에 있는 세균과 결합해, 분해되며 발생하는 산이 치아의 법랑질을 공격하며 생기는 손상 현상을 말해요. 충치를 예방하기 위해서는 올바른 양치법이 필요해요. 또한 물을 자주 마시도록 하며 자기 직전에는 음식물 섭취를 자제하는 것이 좋아요. 캐러멜과 젤리와 같은 끈적임이 많은 식품이나 단 음료는 섭취를 줄이시길 권장해요.")
        );

        // given
        given(questionnaireService.questionnaireResult(anyLong())).willReturn(new QuestionnaireResultDto(new Date(), oralStatusList));

        // when
        ResultActions resultActions = mockMvc.perform(
            RestDocumentationRequestBuilders.get("/questionnaire/result?questionnaireId=1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "questionnaire-result.이호준.AccessToken")
                .with(user("user").roles("USER"))
        );

        // then
        resultActions.andExpect(status().isOk())
            .andExpect(jsonPath("rt").value(200))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andDo(document("questionnaire/result",
                getDocumentRequest(),
                getDocumentResponse(),
                queryParameters(
                    parameterWithName("questionnaireId").description("문진표 고유번호")
                ),
                responseFields(
                    fieldWithPath("rt").type(JsonFieldType.NUMBER).description("결과 코드"),
                    fieldWithPath("rtMsg").type(JsonFieldType.STRING).description("결과 메세지"),
                    fieldWithPath("response").type(JsonFieldType.OBJECT).description("결과 데이터"),
                    fieldWithPath("response.created").type(JsonFieldType.STRING).attributes(dateFormat()).description("문진표 제출일"),
                    fieldWithPath("response.oralStatusList").type(JsonFieldType.ARRAY).description("구강 상태 목록"),
                    fieldWithPath("response.oralStatusList[].type").type(JsonFieldType.STRING).description("구강 상태 타입"),
                    fieldWithPath("response.oralStatusList[].title").type(JsonFieldType.STRING).description("구강 상태 제목"),
                    fieldWithPath("response.oralStatusList[].description").type(JsonFieldType.STRING).description("구강 상태 설명")
                )
            ));

        verify(questionnaireService).questionnaireResult(anyLong());
    }
}
