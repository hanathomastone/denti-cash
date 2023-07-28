package com.kaii.dentix.domain.serviceAgreement;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kaii.dentix.common.ControllerTest;
import com.kaii.dentix.domain.serviceAgreement.application.ServiceAgreementService;
import com.kaii.dentix.domain.serviceAgreement.controller.ServiceAgreementController;
import com.kaii.dentix.domain.serviceAgreement.dto.ServiceAgreementListDto;
import com.kaii.dentix.domain.serviceAgreement.dto.ServiceAgreementPathDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ServiceAgreementController.class)
public class ServiceAgreementControllerTest extends ControllerTest {

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
    private ServiceAgreementService serviceAgreementService;

    /**
     *  약관 전체 조회
     */
    @Test
    public void serviceAgreement() throws Exception{

        // given
        List<ServiceAgreementPathDto> serviceAgreementList = Arrays.asList(
                ServiceAgreementPathDto.builder()
                        .path("http://dthi-dev.kai-i.com/")
                        .build(),
                ServiceAgreementPathDto.builder()
                        .path("http://dtroka-dev.kai-i.com/")
                        .build(),
                ServiceAgreementPathDto.builder()
                        .path("http://dentix-api-dev.kai-i.com/docs/app-api-guide.html")
                        .build()
        );

        given(serviceAgreementService.serviceAgreementPath()).willReturn(new ServiceAgreementListDto(serviceAgreementList));

        // when
        ResultActions resultActions = mockMvc.perform(
                RestDocumentationRequestBuilders.get("/service-agreement")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("rt").value(200))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(document("service-agreement",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        responseFields(
                                fieldWithPath("rt").type(JsonFieldType.NUMBER).description("결과 코드"),
                                fieldWithPath("rtMsg").type(JsonFieldType.STRING).description("결과 메세지"),
                                fieldWithPath("response").type(JsonFieldType.OBJECT).description("결과 데이터"),
                                fieldWithPath("response.serviceAgreement").type(JsonFieldType.ARRAY).description("서비스 동의 경로"),
                                fieldWithPath("response.serviceAgreement[].path").type(JsonFieldType.STRING).description("서비스 동의 경로")
                        )
                ));

        verify(serviceAgreementService).serviceAgreementPath();

    }

}