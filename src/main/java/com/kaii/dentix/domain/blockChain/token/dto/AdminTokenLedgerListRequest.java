package com.kaii.dentix.domain.blockChain.token.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminTokenLedgerListRequest {
    private Integer page;
    private Integer size;
    private String userIdentifierOrName;
    private String type;   // TRANSFER, MANUAL, RETRIEVE 등
    private String status; // SUCCESS, FAIL 등
    private String startDate;
    private String endDate;
    private String sort;// ASC, DESC
    private String period;

    // ✅ null 방어용 getter 추가
    public int getPageSafe() {
        return page != null ? page : 0;
    }

    public int getSizeSafe() {
        return size != null ? size : 10;
    }
}