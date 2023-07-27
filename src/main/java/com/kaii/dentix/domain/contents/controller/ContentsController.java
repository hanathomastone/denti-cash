package com.kaii.dentix.domain.contents.controller;

import com.kaii.dentix.domain.contents.application.ContentsService;
import com.kaii.dentix.domain.contents.dto.ContentsCardListDto;
import com.kaii.dentix.domain.contents.dto.ContentsListDto;
import com.kaii.dentix.global.common.response.DataResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/contents")
public class ContentsController {

    private final ContentsService contentsService;

    /**
     *  콘텐츠 조회
     */
    @GetMapping(name = "콘텐츠 조회")
    public DataResponse<ContentsListDto> contentsList(HttpServletRequest httpServletRequest){
        DataResponse<ContentsListDto> response = new DataResponse<>(contentsService.contentsList(httpServletRequest));
        return response;
    }

    /**
     *  콘텐츠 카드뉴스
     */
    @GetMapping(value = "/card", name = "콘텐츠 카드뉴스")
    public DataResponse<ContentsCardListDto> contentsCard(@RequestParam Long contentsId){
        DataResponse<ContentsCardListDto> response = new DataResponse<>(contentsService.contentsCard(contentsId));
        return response;
    }


}
