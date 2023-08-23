package com.kaii.dentix.domain.userServiceAgreement.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.kaii.dentix.domain.type.YnType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.Date;

@Getter @Builder
@AllArgsConstructor
public class UserModifyServiceAgreeDto {

    private Long serviceAgreeId;

    private YnType isUserServiceAgree;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private Date date;

}
