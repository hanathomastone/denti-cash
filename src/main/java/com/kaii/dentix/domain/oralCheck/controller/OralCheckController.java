package com.kaii.dentix.domain.oralCheck.controller;

import com.kaii.dentix.domain.oralCheck.application.OralCheckService;
import com.kaii.dentix.domain.oralCheck.dto.OralCheckResultDto;
import com.kaii.dentix.domain.oralCheck.dto.resoponse.OralCheckPhotoResponse;
import com.kaii.dentix.domain.oralCheck.dto.resoponse.OralCheckResultResponse;
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
    public OralCheckPhotoResponse oralCheckPhoto(HttpServletRequest httpServletRequest, @RequestParam MultipartFile file) throws IOException, NoSuchAlgorithmException, InvalidKeyException, InterruptedException {
        OralCheckPhotoResponse response = oralCheckService.oralCheckPhoto(httpServletRequest, file);
        return response;
    }

    /**
     *  구강검진 결과
     */
    @GetMapping(value = "/result", name = "구강검진 결과")
    public OralCheckResultResponse oralCheckResult(HttpServletRequest httpServletRequest, @RequestParam Long oralCheckId){
        OralCheckResultDto oralCheckResultDto = oralCheckService.oralCheckResult(httpServletRequest, oralCheckId);
        OralCheckResultResponse response = new OralCheckResultResponse(oralCheckResultDto);
        return response;
    }

}
