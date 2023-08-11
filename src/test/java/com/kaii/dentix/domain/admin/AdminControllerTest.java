package com.kaii.dentix.domain.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kaii.dentix.common.ControllerTest;
import com.kaii.dentix.domain.admin.admin.application.AdminService;
import com.kaii.dentix.domain.admin.admin.controller.AdminController;
import com.kaii.dentix.domain.admin.admin.dto.AdminSignUpDto;
import com.kaii.dentix.domain.admin.admin.dto.request.AdminModifyPasswordRequest;
import com.kaii.dentix.domain.admin.admin.dto.request.AdminSignUpRequest;
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

import static com.kaii.dentix.common.ApiDocumentUtils.getDocumentRequest;
import static com.kaii.dentix.common.ApiDocumentUtils.getDocumentResponse;
import static com.kaii.dentix.common.DocumentOptionalGenerator.userNumberFormat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminController.class)
public class AdminControllerTest extends ControllerTest {

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
    private AdminService adminService;

    public AdminSignUpDto adminSignUpDto(){
        return AdminSignUpDto.builder()
                .adminId(1L)
                .adminPassword("dentix2023!")
                .build();
    }

    /**
     *  관리자 등록
     */
    @Test
    public void adminSignUp() throws Exception{

        // given
        given(adminService.adminSignUp(any(AdminSignUpRequest.class))).willReturn(adminSignUpDto());

        AdminSignUpRequest adminSignUpRequest = AdminSignUpRequest.builder()
                .adminName("홍길동")
                .adminLoginIdentifier("adminhong")
                .adminPhoneNumber("01012345678")
                .build();

        // when
        ResultActions resultActions = mockMvc.perform(
                RestDocumentationRequestBuilders.post("/admin/account")
                        .content(objectMapper.writeValueAsString(adminSignUpRequest))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("rt").value(200))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(document("admin/account",
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

        verify(adminService).adminSignUp(any(AdminSignUpRequest.class));

    }


    /**
     *  관리자 비밀번호 변경
     */
    @Test
    public void adminModifyPassword() throws Exception{

        // given
        AdminModifyPasswordRequest request = new AdminModifyPasswordRequest("qwer1234!");

        // when
        ResultActions result = mockMvc.perform(
                RestDocumentationRequestBuilders.put("/admin/account/password")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "account-password-modify.고유경.AccessToken")
                        .with(user("user").roles("ADMIN"))
        );

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("rt").value(200))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(document("admin/account/password-modify",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                fieldWithPath("adminPassword").type(JsonFieldType.STRING).description("관리자 비밀번호")
                        ),
                        responseFields(
                                fieldWithPath("rt").type(JsonFieldType.NUMBER).description("결과 코드"),
                                fieldWithPath("rtMsg").type(JsonFieldType.STRING).description("결과 메세지")
                        )
                ));

        verify(adminService).adminModifyPassword(any(HttpServletRequest.class), any(AdminModifyPasswordRequest.class));
    }

}
