package com.kaii.dentix.domain.oralCheck.controller;

import com.kaii.dentix.domain.oralCheck.application.OralCheckService;
import com.kaii.dentix.domain.oralCheck.dto.OralCheckPhotoDto;
import com.kaii.dentix.domain.oralCheck.dto.OralCheckResultDto;
import com.kaii.dentix.global.common.response.DataResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/oralCheck")
public class OralCheckController {

    private final OralCheckService oralCheckService;

    /**
     * 구강검진 사진 촬영
     */
    @PostMapping(value = "/photo", name = "구강검진 사진 촬영")
    public DataResponse<OralCheckPhotoDto> oralCheckPhoto(HttpServletRequest httpServletRequest, @RequestParam MultipartFile file) throws IOException, NoSuchAlgorithmException, InvalidKeyException, InterruptedException {
        DataResponse<OralCheckPhotoDto> response = oralCheckService.oralCheckPhoto(httpServletRequest, file);
        return response;
    }

    /**
     *  구강검진 결과
     */
    @GetMapping(value = "/result", name = "구강검진 결과")
    public DataResponse<OralCheckResultDto> oralCheckResult(HttpServletRequest httpServletRequest, @RequestParam Long oralCheckId){
        DataResponse<OralCheckResultDto> response = new DataResponse<>(oralCheckService.oralCheckResult(httpServletRequest, oralCheckId));
        return response;
    }

}
