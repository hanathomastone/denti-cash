package com.kaii.dentix.domain.toothBrushing.controller;

import com.kaii.dentix.domain.toothBrushing.application.ToothBrushingService;
import com.kaii.dentix.domain.toothBrushing.dto.ToothBrushingRegisterDto;
import com.kaii.dentix.global.common.response.DataResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/toothBrushing")
public class ToothBrushingController {

    private final ToothBrushingService toothBrushingService;

    /**
     *  양치질 기록
     */
    @PostMapping(name = "양치질 기록")
    public DataResponse<ToothBrushingRegisterDto> toothBrushing(HttpServletRequest httpServletRequest){
        DataResponse<ToothBrushingRegisterDto> response = new DataResponse<>(toothBrushingService.toothBrushing(httpServletRequest));
        return response;
    }

}
