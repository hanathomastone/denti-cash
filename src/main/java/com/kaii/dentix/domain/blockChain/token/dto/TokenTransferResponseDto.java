package com.kaii.dentix.domain.blockChain.token.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 토큰 발급 응답 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenTransferResponseDto {
    private Boolean success;
    private Long ledgerId;
    private String fromAddress;
    private String toAddress;
    private Long amount;
    private String reason;
    private LocalDateTime timestamp;
    private FlaskTransferInfo flaskResponse;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FlaskTransferInfo {
        private String date;
        private String sender;
        private String receiver;
        private Integer amount;
    }

    public static TokenTransferResponseDto from(Map<String, Object> serviceResult) {
        TokenTransferResponseDtoBuilder builder = TokenTransferResponseDto.builder()
                .success((Boolean) serviceResult.getOrDefault("success", false))
                .ledgerId(((Number) serviceResult.getOrDefault("ledgerId", 0L)).longValue())
                .fromAddress((String) serviceResult.getOrDefault("fromAddress", ""))
                .toAddress((String) serviceResult.getOrDefault("toAddress", ""))
                .amount(((Number) serviceResult.getOrDefault("amount", 0L)).longValue())
                .reason((String) serviceResult.getOrDefault("reason", ""))
                .timestamp((LocalDateTime) serviceResult.getOrDefault("timestamp", LocalDateTime.now()));

        // Flask 응답 파싱
        if (serviceResult.containsKey("flaskResponse")) {
            Map<String, Object> flaskData = (Map<String, Object>) serviceResult.get("flaskResponse");
            if (flaskData != null && !flaskData.isEmpty()) {
                // Amount 안전 파싱 (문자열 or 숫자 모두 지원)
                Integer amountValue = null;
                Object amountObj = flaskData.get("Amount");
                if (amountObj instanceof Number) {
                    amountValue = ((Number) amountObj).intValue();
                } else if (amountObj instanceof String) {
                    try {
                        amountValue = Integer.parseInt((String) amountObj);
                    } catch (NumberFormatException e) {
                        amountValue = 0;
                    }
                }

                builder.flaskResponse(FlaskTransferInfo.builder()
                        .date((String) flaskData.getOrDefault("Date", ""))
                        .sender((String) flaskData.getOrDefault("Sender", ""))
                        .receiver((String) flaskData.getOrDefault("Receiver", ""))
                        .amount(amountValue)
                        .build());
            }
        }

        return builder.build();
    }
}