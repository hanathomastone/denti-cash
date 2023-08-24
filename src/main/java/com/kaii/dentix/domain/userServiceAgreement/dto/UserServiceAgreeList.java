package com.kaii.dentix.domain.userServiceAgreement.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.kaii.dentix.domain.type.YnType;
import lombok.*;

import java.util.Date;

@Getter @Builder
@AllArgsConstructor @NoArgsConstructor
public class UserServiceAgreeList {

    private Long serviceAgreeId;

    private YnType isUserServiceAgree;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private Date date;

    public UserServiceAgreeList(Long serviceAgreeId, String isUserServiceAgree, Date date) {
        this.serviceAgreeId = serviceAgreeId;
        this.isUserServiceAgree = YnType.valueOf(isUserServiceAgree);
        this.date = date;
    }
}
