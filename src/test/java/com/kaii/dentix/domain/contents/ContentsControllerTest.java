package com.kaii.dentix.domain.contents;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kaii.dentix.common.ControllerTest;
import com.kaii.dentix.domain.contents.application.ContentsService;
import com.kaii.dentix.domain.contents.controller.ContentsController;
import com.kaii.dentix.domain.contents.dto.*;
import com.kaii.dentix.domain.type.ContentsType;
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

import static com.kaii.dentix.common.ApiDocumentUtils.getDocumentResponse;
import static com.kaii.dentix.common.DocumentOptionalGenerator.contentsTypeFormat;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ContentsController.class)
public class ContentsControllerTest extends ControllerTest {

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
    private ContentsService contentsService;

    /**
     *  콘텐츠 조회
     */
    @Test
    public void contentsList() throws Exception{

        // given
        List<ContentsCategoryDto> categories = Arrays.asList(
                ContentsCategoryDto.builder()
                        .contentsCategoryId(0L)
                        .contentsCategoryName("김덴티님 맞춤")
                        .contentsCategoryColor(null)
                        .contentsCategorySort(0)
                        .build(),
                ContentsCategoryDto.builder()
                        .contentsCategoryId(1L)
                        .contentsCategoryName("질병")
                        .contentsCategoryColor("#98B4ED")
                        .contentsCategorySort(1)
                        .build(),
                ContentsCategoryDto.builder()
                        .contentsCategoryId(2L)
                        .contentsCategoryName("양치")
                        .contentsCategoryColor("#4B79EC")
                        .contentsCategorySort(2)
                        .build()
        );

        List<ContentsCategoryIdsDto> contentsLists = Arrays.asList(
                ContentsCategoryIdsDto.builder().contentsCategoryId(1L).build(),
                ContentsCategoryIdsDto.builder().contentsCategoryId(2L).build()
        );

        List<ContentsDto> contents = Arrays.asList(
                ContentsDto.builder()
                        .contentsId(1L)
                        .contentsSort(1)
                        .contentsTitle("백살도 거뜬한 건강한 치아관리 방법")
                        .contentsType(ContentsType.CARD)
                        .contentsTypeColor("#FF9F06")
                        .contentsThumbnail("http://dentix-api-dev.kai-i.com")
                        .contentsPath(null)
                        .categoryIds(contentsLists)
                        .build()
        );


        given(contentsService.contentsList(any(HttpServletRequest.class))).willReturn(new ContentsListDto(categories, contents));

        // when
        ResultActions result = mockMvc.perform(
                RestDocumentationRequestBuilders.get("/contents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "contents-list.고유경.AccessToken")
                        .with(user("user").roles("USER"))
        );

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("rt").value(200))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(document("contents",
                        getDocumentResponse(),
                        responseFields(
                                fieldWithPath("rt").type(JsonFieldType.NUMBER).description("결과 코드"),
                                fieldWithPath("rtMsg").type(JsonFieldType.STRING).description("결과 메세지"),
                                fieldWithPath("response").type(JsonFieldType.OBJECT).description("결과 데이터"),
                                fieldWithPath("response.categories").type(JsonFieldType.ARRAY).description("콘텐츠 카테고리 목록"),
                                fieldWithPath("response.categories[].contentsCategoryId").type(JsonFieldType.NUMBER).description("콘텐츠 카테고리 고유 번호"),
                                fieldWithPath("response.categories[].contentsCategoryName").type(JsonFieldType.STRING).description("콘텐츠 카테고리 이름"),
                                fieldWithPath("response.categories[].contentsCategoryColor").type(JsonFieldType.STRING).optional().description("콘텐츠 카테고리 색상"),
                                fieldWithPath("response.categories[].contentsCategorySort").type(JsonFieldType.NUMBER).description("콘텐츠 카테고리 정렬 순서"),
                                fieldWithPath("response.contents").type(JsonFieldType.ARRAY).description("콘텐츠 목록"),
                                fieldWithPath("response.contents[].contentsId").type(JsonFieldType.NUMBER).description("콘텐츠 고유 번호"),
                                fieldWithPath("response.contents[].contentsTitle").type(JsonFieldType.STRING).description("콘텐츠 제목"),
                                fieldWithPath("response.contents[].contentsSort").type(JsonFieldType.NUMBER).description("콘텐츠 정렬 순서"),
                                fieldWithPath("response.contents[].contentsType").type(JsonFieldType.STRING).attributes(contentsTypeFormat()).description("콘텐츠 타입"),
                                fieldWithPath("response.contents[].contentsTypeColor").type(JsonFieldType.STRING).description("콘텐츠 제목 색상"),
                                fieldWithPath("response.contents[].contentsThumbnail").type(JsonFieldType.STRING).description("콘텐츠 썸네일"),
                                fieldWithPath("response.contents[].contentsPath").type(JsonFieldType.STRING).optional().description("콘텐츠 경로"),
                                fieldWithPath("response.contents[].categoryIds").type(JsonFieldType.ARRAY).description("콘텐츠 카테고리"),
                                fieldWithPath("response.contents[].categoryIds[].contentsCategoryId").type(JsonFieldType.NUMBER).description("콘텐츠 카테고리 고유 번호")
                        )
                ));

        verify(contentsService).contentsList(any(HttpServletRequest.class));

    }

    /**
     *  콘텐츠 카드 뉴스 조회
     */
    @Test
    public void contentsCard() throws Exception{

        // given
        List<ContentsCardDto> contentsCardList = Arrays.asList(
                ContentsCardDto.builder()
                        .contentsCardNumber(1)
                        .contentsCardPath("http://dentix-api-dev.kai-i.com")
                        .build(),
                ContentsCardDto.builder()
                        .contentsCardNumber(2)
                        .contentsCardPath("http://dentix-api-dev.kai-i.com")
                        .build(),
                ContentsCardDto.builder()
                        .contentsCardNumber(3)
                        .contentsCardPath("http://dentix-api-dev.kai-i.com")
                        .build()
        );

        given(contentsService.contentsCard(any(Long.class))).willReturn(new ContentsCardListDto(contentsCardList));

        // when
        ResultActions result = mockMvc.perform(
                RestDocumentationRequestBuilders.get("/contents/card?contentsId={contentsId}", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(user("user").roles("USER"))
        );

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("rt").value(200))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(document("contents/card",
                        getDocumentResponse(),
                        queryParameters(
                                parameterWithName("contentsId").description("콘텐츠 고유 번호")
                        ),
                        responseFields(
                                fieldWithPath("rt").type(JsonFieldType.NUMBER).description("결과 코드"),
                                fieldWithPath("rtMsg").type(JsonFieldType.STRING).description("결과 메세지"),
                                fieldWithPath("response").type(JsonFieldType.OBJECT).description("결과 데이터"),
                                fieldWithPath("response.cardList").type(JsonFieldType.ARRAY).description("콘텐츠 카드뉴스 목록"),
                                fieldWithPath("response.cardList[].contentsCardNumber").type(JsonFieldType.NUMBER).description("콘텐츠 카드뉴스 카드 번호"),
                                fieldWithPath("response.cardList[].contentsCardPath").type(JsonFieldType.STRING).description("콘텐츠 카드뉴스 경로")
                        )
                ));

        verify(contentsService).contentsCard(any(Long.class));

    }

}
