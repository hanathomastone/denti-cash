package com.kaii.dentix.domain.admin.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminTokenCreateRequest {

    private String tokenName;

    private String tokenSymbol;

    private Long supply;
}