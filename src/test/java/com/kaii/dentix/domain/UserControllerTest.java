package com.kaii.dentix.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kaii.dentix.common.ControllerTest;
import com.kaii.dentix.domain.type.GenderType;
import com.kaii.dentix.domain.user.application.UserService;
import com.kaii.dentix.domain.user.controller.UserController;
import com.kaii.dentix.domain.user.dto.UserLoginDto;
import com.kaii.dentix.domain.user.dto.request.*;
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
import static com.kaii.dentix.common.DocumentOptionalGenerator.genderFormat;
import static com.kaii.dentix.common.DocumentOptionalGenerator.userBirthFormat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerTest extends ControllerTest {

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
    private UserService userService;

    private UserLoginDto userLoginDto(){
        return UserLoginDto.builder()
                .accessToken("Access Token")
                .refreshToken("Refresh Token")
                .userId(1L)
                .userLoginId("dentix123")
                .build();
    }

    /**
     *  사용자 자동 로그인
     */
    @Test
    public void userAutoLogin() throws Exception{

        // given
        given(userService.userAutoLogin(any(HttpServletRequest.class), any(UserAutoLoginRequest.class))).willReturn(userLoginDto());

        UserAutoLoginRequest userAutoLoginRequest = UserAutoLoginRequest.builder()
                .userDeviceModel("iPhone 14 Pro")
                .userDeviceManufacturer("APPLE")
                .userOsVersion("1.1")
                .userDeviceToken("DeviceToken")
                .build();

        // when
        ResultActions resultActions = mockMvc.perform(
                RestDocumentationRequestBuilders.put("/user/auto-login")
                        .content(objectMapper.writeValueAsString(userAutoLoginRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("deviceType", "iOS")
                        .header("appVersion", 1.1)
                        .header(HttpHeaders.AUTHORIZATION, "user-info.고유경.AccessToken")
                        .with(user("user").roles("USER"))
        );

        // then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("rt").value(200))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(document("user/auto-login",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                fieldWithPath("userDeviceModel").type(JsonFieldType.STRING).optional().description("사용자 기기 모델"),
                                fieldWithPath("userDeviceManufacturer").type(JsonFieldType.STRING).optional().description("사용자 기기 제조사"),
                                fieldWithPath("userOsVersion").type(JsonFieldType.STRING).optional().description("사용자 기기 OS 버전"),
                                fieldWithPath("userDeviceToken").type(JsonFieldType.STRING).optional().description("사용자 기기 푸시토큰")
                        ),
                        responseFields(
                                fieldWithPath("rt").type(JsonFieldType.NUMBER).description("결과 코드"),
                                fieldWithPath("rtMsg").type(JsonFieldType.STRING).description("결과 메세지"),
                                fieldWithPath("userLoginDto").type(JsonFieldType.OBJECT).description("사용자 회원가입 정보"),
                                fieldWithPath("userLoginDto.accessToken").type(JsonFieldType.STRING).description("Access Token"),
                                fieldWithPath("userLoginDto.refreshToken").type(JsonFieldType.STRING).description("Refresh Token"),
                                fieldWithPath("userLoginDto.userId").type(JsonFieldType.NUMBER).description("사용자 고유 번호"),
                                fieldWithPath("userLoginDto.userLoginId").type(JsonFieldType.STRING).description("사용자 아이디")
                        )
                ));

        verify(userService).userAutoLogin(any(HttpServletRequest.class), any(UserAutoLoginRequest.class));

    }

    /**
     *  사용자 비밀번호 확인
     */
    @Test
    public void userPasswordVerify() throws Exception{

        // given
        doNothing().when(userService).userPasswordVerify(any(HttpServletRequest.class), any(UserPasswordVerifyRequest.class));

        UserPasswordVerifyRequest userPasswordVerifyRequest = UserPasswordVerifyRequest.builder()
                .userPassword("password")
                .build();

        // when
        ResultActions resultActions = mockMvc.perform(
                RestDocumentationRequestBuilders.post("/user/password-verify")
                        .content(objectMapper.writeValueAsString(userPasswordVerifyRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "user-info.고유경.AccessToken")
                        .with(user("user").roles("USER"))
        );

        // then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("rt").value(200))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(document("user/password-verify",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                fieldWithPath("userPassword").type(JsonFieldType.STRING).description("사용자 비밀번호")
                        ),
                        responseFields(
                                fieldWithPath("rt").type(JsonFieldType.NUMBER).description("결과 코드"),
                                fieldWithPath("rtMsg").type(JsonFieldType.STRING).description("결과 메세지")
                        )
                ));

        verify(userService).userPasswordVerify(any(HttpServletRequest.class), any(UserPasswordVerifyRequest.class));

    }

    /**
     *  사용자 보안정보수정 - 비밀번호 변경
     */
    @Test
    public void userModifyPassword() throws Exception{

        // given
        doNothing().when(userService).userModifyPassword(any(HttpServletRequest.class), any(UserInfoModifyPasswordRequest.class));

        UserInfoModifyPasswordRequest userInfoModifyPasswordRequest = UserInfoModifyPasswordRequest.builder()
                .userPassword("password")
                .build();

        // when
        ResultActions resultActions = mockMvc.perform(
                RestDocumentationRequestBuilders.put("/user/password")
                        .content(objectMapper.writeValueAsString(userInfoModifyPasswordRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "user-info.고유경.AccessToken")
                        .with(user("user").roles("USER"))
        );

        // then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("rt").value(200))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(document("user/password",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                fieldWithPath("userPassword").type(JsonFieldType.STRING).description("사용자 비밀번호")
                        ),
                        responseFields(
                                fieldWithPath("rt").type(JsonFieldType.NUMBER).description("결과 코드"),
                                fieldWithPath("rtMsg").type(JsonFieldType.STRING).description("결과 메세지")
                        )
                ));

        verify(userService).userModifyPassword(any(HttpServletRequest.class), any(UserInfoModifyPasswordRequest.class));

    }

    /**
     *  사용자 보안정보수정 - 질문과 답변 수정
     */
    @Test
    public void userModifyQnA() throws Exception {

        // given
        doNothing().when(userService).userModifyQnA(any(HttpServletRequest.class), any(UserInfoModifyQnARequest.class));

        UserInfoModifyQnARequest userInfoModifyQnARequest = UserInfoModifyQnARequest.builder()
                .findPwdQuestionId(2L)
                .findPwdAnswer("덴티엑스초등학교")
                .build();

        // when
        ResultActions resultActions = mockMvc.perform(
                RestDocumentationRequestBuilders.put("/user/qna")
                        .content(objectMapper.writeValueAsString(userInfoModifyQnARequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "user-info.고유경.AccessToken")
                        .with(user("user").roles("USER"))
        );

        // then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("rt").value(200))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(document("user/qna",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                fieldWithPath("findPwdQuestionId").type(JsonFieldType.NUMBER).description("사용자 비밀번호 찾기 질문"),
                                fieldWithPath("findPwdAnswer").type(JsonFieldType.STRING).description("사용자 비밀번호 찾기 답변")
                        ),
                        responseFields(
                                fieldWithPath("rt").type(JsonFieldType.NUMBER).description("결과 코드"),
                                fieldWithPath("rtMsg").type(JsonFieldType.STRING).description("결과 메세지")
                        )
                ));

        verify(userService).userModifyQnA(any(HttpServletRequest.class), any(UserInfoModifyQnARequest.class));

    }

    /**
     *  사용자 회원정보 수정
     */
    @Test
    public void userModifyInfo() throws Exception{

        // given
        doNothing().when(userService).userModifyInfo(any(HttpServletRequest.class), any(UserInfoModifyRequest.class));

        UserInfoModifyRequest userInfoModifyRequest = UserInfoModifyRequest.builder()
                .userName("강덴티")
                .userGender(GenderType.W)
                .userBirth("20000801")
                .build();

        // when
        ResultActions resultActions = mockMvc.perform(
                RestDocumentationRequestBuilders.put("/user")
                        .content(objectMapper.writeValueAsString(userInfoModifyRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "user-info.고유경.AccessToken")
                        .with(user("user").roles("USER"))
        );

        // then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("rt").value(200))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(document("user",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                fieldWithPath("userName").type(JsonFieldType.STRING).description("사용자 이름"),
                                fieldWithPath("userGender").type(JsonFieldType.STRING).attributes(genderFormat()).description("사용자 성별"),
                                fieldWithPath("userBirth").type(JsonFieldType.STRING).attributes(userBirthFormat()).description("사용자 생년월일")
                        ),
                        responseFields(
                                fieldWithPath("rt").type(JsonFieldType.NUMBER).description("결과 코드"),
                                fieldWithPath("rtMsg").type(JsonFieldType.STRING).description("결과 메세지")
                        )
                ));

        verify(userService).userModifyInfo(any(HttpServletRequest.class), any(UserInfoModifyRequest.class));

    }

    /**
     *  사용자 로그아웃
     */
    @Test
    public void userLogout() throws Exception{

        // given
        doNothing().when(userService).userLogout(any(HttpServletRequest.class));

        // when
        ResultActions resultActions = mockMvc.perform(
                RestDocumentationRequestBuilders.put("/user/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("deviceType", "iOS")
                        .header("appVersion", 1.1)
                        .header(HttpHeaders.AUTHORIZATION, "user-info.고유경.AccessToken")
                        .with(user("user").roles("USER"))
        );

        // then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("rt").value(200))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(document("user/logout",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        responseFields(
                                fieldWithPath("rt").type(JsonFieldType.NUMBER).description("결과 코드"),
                                fieldWithPath("rtMsg").type(JsonFieldType.STRING).description("결과 메세지")
                        )
                ));

        verify(userService).userLogout(any(HttpServletRequest.class));

    }

}
