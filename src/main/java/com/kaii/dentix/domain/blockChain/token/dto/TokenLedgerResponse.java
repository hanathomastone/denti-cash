package com.kaii.dentix.domain.blockChain.token.dto;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenLedgerResponse {
    private Long ledgerId;
    private String type;
    private String userName;
    private String userLoginIdentifier;
    private String reason;
    private String note;
    private Long amount;
    private String status;
    private Date created;
}