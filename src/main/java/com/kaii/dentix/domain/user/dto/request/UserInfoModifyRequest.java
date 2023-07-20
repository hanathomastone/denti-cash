package com.kaii.dentix.domain.user.dto.request;

import com.kaii.dentix.domain.type.GenderType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @Builder
@AllArgsConstructor @NoArgsConstructor
public class UserInfoModifyRequest {

    @NotBlank @Size(min = 2, max = 6)
    private String userName;

    @NotNull
    private GenderType userGender;

}
