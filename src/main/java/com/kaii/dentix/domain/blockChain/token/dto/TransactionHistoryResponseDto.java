package com.kaii.dentix.domain.blockChain.token.dto;

import com.kaii.dentix.domain.blockChain.token.domain.TokenLedger;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 거래 내역 조회 응답 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionHistoryResponseDto {
    private List<TransactionDto> transactions;
    private PaginationInfo pagination;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TransactionDto {
        private Long ledgerId;
        private String senderAddress;
        private String receiverAddress;
        private Long amount;
        private String type;
        private String status;
        private String message;
        private String description;
        private LocalDateTime transactionDate;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaginationInfo {
        private Long totalElements;
        private Integer totalPages;
        private Integer currentPage;
        private Integer size;
    }

    @SuppressWarnings("unchecked")
    public static TransactionHistoryResponseDto from(Map<String, Object> serviceResult) {
        Object transactionsObj = serviceResult.get("transactions");
        List<TokenLedger> ledgers;

        if (transactionsObj instanceof List<?>) {
            ledgers = (List<TokenLedger>) transactionsObj;
        } else {
            throw new IllegalArgumentException("transactions 데이터가 List 타입이 아닙니다.");
        }

        List<TransactionDto> transactionDtos = ledgers.stream()
                .map(ledger -> TransactionDto.builder()
                        .ledgerId(ledger.getId())
                        .senderAddress(ledger.getSenderAdminWallet() != null ?
                                ledger.getSenderAdminWallet().getAddress() : null)
                        .receiverAddress(ledger.getReceiverUserWallet() != null ?
                                ledger.getReceiverUserWallet().getAddress() : null)
                        .amount(ledger.getAmount().longValue())
                        .type(ledger.getType().name())
                        .status(ledger.getStatus().name())
                        .message(ledger.getMessage())
                        // Date를 LocalDateTime으로 변환
                        .transactionDate(convertToLocalDateTime(ledger.getCreated()))
                        .build())
                .collect(Collectors.toList());

        return TransactionHistoryResponseDto.builder()
                .transactions(transactionDtos)
                .pagination(PaginationInfo.builder()
                        .totalElements((Long) serviceResult.get("totalElements"))
                        .totalPages((Integer) serviceResult.get("totalPages"))
                        .currentPage((Integer) serviceResult.get("currentPage"))
                        .size((Integer) serviceResult.get("size"))
                        .build())
                .build();
    }

    /**
     * Date를 LocalDateTime으로 변환하는 유틸리티 메서드
     */
    private static LocalDateTime convertToLocalDateTime(Date date) {
        if (date == null) {
            return null;
        }
        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }
}