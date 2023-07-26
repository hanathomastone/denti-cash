package com.kaii.dentix.domain.contents.controller;

import com.kaii.dentix.domain.contents.application.ContentsService;
import com.kaii.dentix.domain.contents.dto.ContentsCategoryListDto;
import com.kaii.dentix.global.common.response.DataResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/contents")
public class ContentsController {

    private final ContentsService contentsService;

    /**
     *  콘텐츠 카테고리 목록 조회
     */
    @GetMapping(value = "/category", name = "콘텐츠 카테고리 목록 조회")
    public DataResponse<ContentsCategoryListDto> contentsCategoryList(HttpServletRequest httpServletRequest){
        DataResponse<ContentsCategoryListDto> response = new DataResponse<>(contentsService.contentsCategoryList(httpServletRequest));
        return response;
    }

}
