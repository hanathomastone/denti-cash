package com.kaii.dentix.domain.user.dto;

import com.kaii.dentix.domain.userServiceAgreement.dto.request.UserServiceAgreementRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter @Builder
@AllArgsConstructor
public class UserVerifyDto {

    private Long patientId;

}
