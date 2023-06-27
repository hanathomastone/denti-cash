package com.kaii.dentix.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kaii.dentix.common.ControllerTest;
import com.kaii.dentix.domain.type.YnType;
import com.kaii.dentix.domain.user.application.UserLoginService;
import com.kaii.dentix.domain.user.controller.UserLoginController;
import com.kaii.dentix.domain.user.dto.UserVerifyDto;
import com.kaii.dentix.domain.user.dto.request.UserVerifyRequest;
import com.kaii.dentix.domain.userServiceAgreement.dto.request.UserServiceAgreementRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Arrays;

import static com.kaii.dentix.common.ApiDocumentUtils.getDocumentRequest;
import static com.kaii.dentix.common.ApiDocumentUtils.getDocumentResponse;
import static com.kaii.dentix.common.DocumentOptionalGenerator.userNumberFormat;
import static com.kaii.dentix.common.DocumentOptionalGenerator.yesNoFormat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(UserLoginController.class)
public class UserLoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserLoginService userLoginService;

    private UserVerifyDto userVerifyDto(){
        return UserVerifyDto.builder()
                .patientId(1L)
                .build();
    }

    @Test
    public void userVerify() throws Exception{

        // given
        given(userLoginService.userVerify(any(UserVerifyRequest.class))).willReturn(userVerifyDto());

        UserVerifyRequest userVerifyRequest = UserVerifyRequest.builder()
                .patientPhoneNumber("01012345678")
                .patientName("김덴티")
                .userServiceAgreementRequest(Arrays.asList(
                        new UserServiceAgreementRequest(1L, YnType.Y),
                        new UserServiceAgreementRequest(2L, YnType.Y),
                        new UserServiceAgreementRequest(3L, YnType.Y),
                        new UserServiceAgreementRequest(4L, YnType.N)
                ))
                .build();

        // when
        ResultActions resultActions = mockMvc.perform(
                RestDocumentationRequestBuilders.post("/verify")
                        .content(objectMapper.writeValueAsString(userVerifyRequest))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("rt").value(200))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(document("verify",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                fieldWithPath("patientPhoneNumber").type(JsonFieldType.STRING).attributes(userNumberFormat()).description("사용자(환자) 연락처"),
                                fieldWithPath("patientName").type(JsonFieldType.STRING).description("사용자(환자) 실명"),
                                fieldWithPath("userServiceAgreementRequest[]").type(JsonFieldType.ARRAY).description("사용자 서비스 동의"),
                                fieldWithPath("userServiceAgreementRequest[].serviceAgreeId").type(JsonFieldType.NUMBER).description("사용자 서비스 동의 고유 번호"),
                                fieldWithPath("userServiceAgreementRequest[].isUserServiceAgree").type(JsonFieldType.STRING).attributes(yesNoFormat()).description("사용자 서비스 동의 여부")
                        ),
                        responseFields(
                                fieldWithPath("rt").type(JsonFieldType.NUMBER).description("결과 코드"),
                                fieldWithPath("rtMsg").type(JsonFieldType.STRING).description("결과 메세지"),
                                fieldWithPath("patientId").type(JsonFieldType.NUMBER).description("환자 고유 번호")
                        )
                ));

        verify(userLoginService).userVerify(any(UserVerifyRequest.class));
    }


}