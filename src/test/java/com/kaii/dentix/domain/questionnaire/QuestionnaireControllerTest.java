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
import java.util.List;

import static com.kaii.dentix.common.ApiDocumentUtils.getDocumentRequest;
import static com.kaii.dentix.common.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
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
                .multiple(false)
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
                .multiple(true)
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
                    fieldWithPath("response.template[].multiple").type(JsonFieldType.BOOLEAN).description("문항 중복 선택 가능 여부"),
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
        QuestionnaireFormDto form = QuestionnaireFormDto.builder()
            .q_1(1)
            .q_2(2)
            .q_3(new Integer[]{1, 2})
            .q_4(3)
            .q_5(new Integer[]{3, 4})
            .q_6(new Integer[]{5, 6})
            .q_7(0)
            .q_8(new Integer[]{7, 8})
            .q_9(4)
            .q_10(1)
            .build();

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
                    fieldWithPath("form").type(JsonFieldType.OBJECT).description("문진표 항목"),
                    fieldWithPath("form.q_1").type(JsonFieldType.NUMBER).description("문진표 1번 문항"),
                    fieldWithPath("form.q_2").type(JsonFieldType.NUMBER).description("문진표 2번 문항"),
                    fieldWithPath("form.q_3").type(JsonFieldType.ARRAY).description("문진표 3번 문항"),
                    fieldWithPath("form.q_4").type(JsonFieldType.NUMBER).description("문진표 4번 문항"),
                    fieldWithPath("form.q_5").type(JsonFieldType.ARRAY).description("문진표 5번 문항"),
                    fieldWithPath("form.q_6").type(JsonFieldType.ARRAY).description("문진표 6번 문항"),
                    fieldWithPath("form.q_7").type(JsonFieldType.NUMBER).description("문진표 7번 문항"),
                    fieldWithPath("form.q_8").type(JsonFieldType.ARRAY).description("문진표 8번 문항"),
                    fieldWithPath("form.q_9").type(JsonFieldType.NUMBER).description("문진표 9번 문항"),
                    fieldWithPath("form.q_10").type(JsonFieldType.NUMBER).description("문진표 10번 문항")
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
}
