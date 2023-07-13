package com.kaii.dentix.domain.oralCheck.application;

import com.kaii.dentix.domain.oralCheck.dto.resoponse.OralCheckAnalysisResponse;
import com.kaii.dentix.domain.oralCheck.dto.resoponse.OralCheckPhotoResponse;
import com.kaii.dentix.domain.user.application.UserService;
import com.kaii.dentix.domain.user.domain.User;
import com.kaii.dentix.global.common.aws.AWSS3Service;
import com.kaii.dentix.global.common.error.exception.BadRequestApiException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class OralCheckService {

    private final UserService userService;

    private final AWSS3Service awss3Service;

    @Value("public/upload/oralCheck/")
    private String folderPath;

    /**
     *  구강검진 사진 촬영
     */
    @Transactional
    public OralCheckPhotoResponse oralCheckPhoto(HttpServletRequest httpServletRequest, byte[] file, String contentType) throws IOException, NoSuchAlgorithmException, InvalidKeyException, InterruptedException  {
        User user = userService.getTokenUser(httpServletRequest);

        String uploadedUrl = awss3Service.upload(file, folderPath, contentType);

        try {

            TimeUnit.SECONDS.sleep(3);

            if (StringUtils.isBlank(uploadedUrl)) throw new BadRequestApiException("파일 저장에 실패했습니다.");

            String findText = "";
            int pathIndex = uploadedUrl.indexOf(findText);
            String imagePath = uploadedUrl.substring(pathIndex + findText.length());

            OralCheckAnalysisResponse analysisData = lambdaService.getPyDentalLambda(imagePath);

        }

    }

}
