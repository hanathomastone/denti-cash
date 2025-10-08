package com.kaii.dentix.domain.blockChain.token.dto;

import lombok.*;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenRetrieveResponseDto {
    private String date;
    private String target;
    private Integer amount;

    public static TokenRetrieveResponseDto from(Map<String, Object> map) {
        if (map == null || map.isEmpty()) {
            return new TokenRetrieveResponseDto("", "", 0);
        }

        Object amountObj = map.get("Amount");
        Integer amountValue = 0;

        if (amountObj instanceof Number) {
            amountValue = ((Number) amountObj).intValue();
        } else if (amountObj instanceof String) {
            try {
                amountValue = Integer.parseInt((String) amountObj);
            } catch (NumberFormatException e) {
                amountValue = 0;
            }
        }

        return TokenRetrieveResponseDto.builder()
                .date((String) map.getOrDefault("Date", ""))
                .target((String) map.getOrDefault("Target", ""))
                .amount(amountValue)
                .build();
    }
}