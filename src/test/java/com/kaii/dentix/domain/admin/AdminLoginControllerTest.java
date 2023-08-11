package com.kaii.dentix.domain.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kaii.dentix.common.ControllerTest;
import com.kaii.dentix.domain.admin.admin.application.AdminLoginService;
import com.kaii.dentix.domain.admin.admin.controller.AdminLoginController;
import com.kaii.dentix.domain.admin.admin.dto.AdminLoginDto;
import com.kaii.dentix.domain.admin.admin.dto.AdminSignUpDto;
import com.kaii.dentix.domain.admin.admin.dto.request.AdminLoginRequest;
import com.kaii.dentix.domain.admin.admin.dto.request.AdminSignUpRequest;
import com.kaii.dentix.domain.type.YnType;
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

import static com.kaii.dentix.common.ApiDocumentUtils.getDocumentRequest;
import static com.kaii.dentix.common.ApiDocumentUtils.getDocumentResponse;
import static com.kaii.dentix.common.DocumentOptionalGenerator.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminLoginController.class)
public class AdminLoginControllerTest extends ControllerTest {

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
    private AdminLoginService adminLoginService;

    public AdminSignUpDto adminSignUpDto(){
        return AdminSignUpDto.builder()
                .adminId(1L)
                .adminPassword("dentix2023!")
                .build();
    }

    public AdminLoginDto adminLoginDto(){
        return AdminLoginDto.builder()
                .adminId(1L)
                .isFirstLogin(YnType.Y)
                .adminName("홍길동")
                .accessToken("AccessToken")
                .refreshToken("RefreshToken")
                .isSuper(YnType.N)
                .build();
    }

    /**
     *  관리자 등록
     */
    @Test
    public void adminSignUp() throws Exception{

        // given
        given(adminLoginService.adminSignUp(any(AdminSignUpRequest.class))).willReturn(adminSignUpDto());

        AdminSignUpRequest adminSignUpRequest = AdminSignUpRequest.builder()
                .adminName("홍길동")
                .adminLoginIdentifier("adminhong")
                .adminPhoneNumber("01012345678")
                .build();

        // when
        ResultActions resultActions = mockMvc.perform(
                RestDocumentationRequestBuilders.post("/admin/signUp")
                        .content(objectMapper.writeValueAsString(adminSignUpRequest))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("rt").value(200))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(document("admin/signUp",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                fieldWithPath("adminName").type(JsonFieldType.STRING).description("관리자 이름"),
                                fieldWithPath("adminLoginIdentifier").type(JsonFieldType.STRING).description("관리자 아이디"),
                                fieldWithPath("adminPhoneNumber").type(JsonFieldType.STRING).attributes(userNumberFormat()).description("관리자 연락처")
                        ),
                        responseFields(
                                fieldWithPath("rt").type(JsonFieldType.NUMBER).description("결과 코드"),
                                fieldWithPath("rtMsg").type(JsonFieldType.STRING).description("결과 메세지"),
                                fieldWithPath("response").type(JsonFieldType.OBJECT).description("결과 데이터"),
                                fieldWithPath("response.adminId").type(JsonFieldType.NUMBER).description("관리자 고유 번호"),
                                fieldWithPath("response.adminPassword").type(JsonFieldType.STRING).description("관리자 초기 비밀번호")
                        )
                ));

        verify(adminLoginService).adminSignUp(any(AdminSignUpRequest.class));

    }

    /**
     *  관리자 로그인
     */
    @Test
    public void adminLogin() throws Exception{

        // given
        given(adminLoginService.adminLogin(any(AdminLoginRequest.class))).willReturn(adminLoginDto());

        AdminLoginRequest adminLoginRequest = AdminLoginRequest.builder()
                .adminLoginIdentifier("adminhong")
                .adminPassword("dentix2023!")
                .build();

        // when
        ResultActions resultActions = mockMvc.perform(
                RestDocumentationRequestBuilders.post("/admin/login")
                        .content(objectMapper.writeValueAsString(adminLoginRequest))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("rt").value(200))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(document("admin/login",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                fieldWithPath("adminLoginIdentifier").type(JsonFieldType.STRING).description("관리자 아이디"),
                                fieldWithPath("adminPassword").type(JsonFieldType.STRING).description("관리자 비밀번호")
                        ),
                        responseFields(
                                fieldWithPath("rt").type(JsonFieldType.NUMBER).description("결과 코드"),
                                fieldWithPath("rtMsg").type(JsonFieldType.STRING).description("결과 메세지"),
                                fieldWithPath("response").type(JsonFieldType.OBJECT).description("결과 데이터"),
                                fieldWithPath("response.adminId").type(JsonFieldType.NUMBER).description("관리자 고유 번호"),
                                fieldWithPath("response.adminName").type(JsonFieldType.STRING).description("관리자 이름"),
                                fieldWithPath("response.accessToken").type(JsonFieldType.STRING).description("Access Token"),
                                fieldWithPath("response.refreshToken").type(JsonFieldType.STRING).description("Refresh Token"),
                                fieldWithPath("response.isFirstLogin").type(JsonFieldType.STRING).attributes(yesNoFormat()).description("최초 로그인 여부"),
                                fieldWithPath("response.isSuper").type(JsonFieldType.STRING).attributes(yesNoFormat()).description("관리자 슈퍼계정 여부")
                        )
                ));

        verify(adminLoginService).adminLogin(any(AdminLoginRequest.class));

    }

}
