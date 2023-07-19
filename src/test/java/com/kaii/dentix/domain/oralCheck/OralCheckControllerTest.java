package com.kaii.dentix.domain.oralCheck;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kaii.dentix.common.ControllerTest;
import com.kaii.dentix.domain.oralCheck.application.OralCheckService;
import com.kaii.dentix.domain.oralCheck.controller.OralCheckController;
import com.kaii.dentix.domain.oralCheck.dto.OralCheckResultDto;
import com.kaii.dentix.domain.oralCheck.dto.resoponse.OralCheckPhotoResponse;
import com.kaii.dentix.domain.type.oral.OralCheckDivisionCommentType;
import com.kaii.dentix.domain.type.oral.OralCheckDivisionScoreType;
import com.kaii.dentix.domain.type.oral.OralCheckResultTotalType;
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

import static com.kaii.dentix.common.ApiDocumentUtils.getDocumentRequest;
import static com.kaii.dentix.common.ApiDocumentUtils.getDocumentResponse;
import static com.kaii.dentix.common.DocumentOptionalGenerator.*;
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

        OralCheckPhotoResponse response = OralCheckPhotoResponse.builder()
                .rt(200)
                .rtMsg("API Call successful")
                .oralCheckId(1L)
                .build();

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
                                fieldWithPath("oralCheckId").type(JsonFieldType.NUMBER).description("구강 검진 사진 촬영 고유번호")
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
                                fieldWithPath("oralCheckResultDto").type(JsonFieldType.OBJECT).description("시용자 구강검진 결과 정보"),
                                fieldWithPath("oralCheckResultDto.userId").type(JsonFieldType.NUMBER).description("사용자 고유 번호"),
                                fieldWithPath("oralCheckResultDto.oralCheckResultTotalType").type(JsonFieldType.STRING).attributes(oralCheckResultTotalFormat()).description("전체 구강 상태"),
                                fieldWithPath("oralCheckResultDto.created").type(JsonFieldType.STRING).attributes(dateFormat()).description("구강 검진일"),
                                fieldWithPath("oralCheckResultDto.oralCheckTotalRange").type(JsonFieldType.NUMBER).description("전체 평균 플라그 비율"),
                                fieldWithPath("oralCheckResultDto.oralCheckUpRightRange").type(JsonFieldType.NUMBER).description("상악우측 플라그 비율"),
                                fieldWithPath("oralCheckResultDto.oralCheckUpRightScoreType").type(JsonFieldType.STRING).attributes(oralCheckDivisionScoreFormat()).description("상악우측 상태"),
                                fieldWithPath("oralCheckResultDto.oralCheckUpLeftRange").type(JsonFieldType.NUMBER).description("상악좌측 플라그 비율"),
                                fieldWithPath("oralCheckResultDto.oralCheckUpLeftScoreType").type(JsonFieldType.STRING).attributes(oralCheckDivisionScoreFormat()).description("상악좌측 상태"),
                                fieldWithPath("oralCheckResultDto.oralCheckDownLeftRange").type(JsonFieldType.NUMBER).description("하악좌측 플라그 비율"),
                                fieldWithPath("oralCheckResultDto.oralCheckDownLeftScoreType").type(JsonFieldType.STRING).attributes(oralCheckDivisionScoreFormat()).description("하악좌측 상태"),
                                fieldWithPath("oralCheckResultDto.oralCheckDownRightRange").type(JsonFieldType.NUMBER).description("하악우측 플라그 비율"),
                                fieldWithPath("oralCheckResultDto.oralCheckDownRightScoreType").type(JsonFieldType.STRING).attributes(oralCheckDivisionScoreFormat()).description("하악우측 상태"),
                                fieldWithPath("oralCheckResultDto.oralCheckDivisionCommentType").type(JsonFieldType.STRING).attributes(oralCheckDivisionCommentFormat()).description("부위별 구강 상태 코멘트")
                        )
                ));

        verify(oralCheckService).oralCheckResult(any(HttpServletRequest.class), any(Long.class));

    }

}
