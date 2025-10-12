package com.kaii.dentix.domain.blockChain.wallet.application;


import com.kaii.dentix.domain.blockChain.token.dao.TokenLedgerRepository;
import com.kaii.dentix.domain.blockChain.token.domain.TokenLedger;
import com.kaii.dentix.domain.blockChain.token.dto.TokenLedgerDto;
import com.kaii.dentix.domain.blockChain.token.type.TokenLedgerType;
import com.kaii.dentix.domain.blockChain.wallet.dao.UserWalletRepository;
import com.kaii.dentix.domain.blockChain.wallet.domain.UserWallet;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserTokenLedgerService {

    private final TokenLedgerRepository tokenLedgerRepository;
    private final UserWalletRepository userWalletRepository;
    private Date toDate(LocalDateTime dateTime) {
        return (dateTime == null) ? null :
                Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
    }


    @Transactional
    public Page<TokenLedgerDto> getLedgerByContract(String contractAddress, int page, int size) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "created"));

        Page<TokenLedger> ledgers = tokenLedgerRepository.findAllByContract_ContractAddress(contractAddress, pageable);

        return ledgers.map(TokenLedgerDto::from);
    }
    public Page<TokenLedgerDto> getLedgerHistoryByCategory(
            String contractAddress,
            String category,   // ISSUE, CHARGE, RECLAIM, ALL
            String sort,
            String allDatePeriod,
            LocalDate startDate,
            LocalDate endDate,
            int page,
            int size
    ) {
        PageRequest pageable = PageRequest.of(
                page,
                size,
                "OLD".equalsIgnoreCase(sort)
                        ? Sort.by(Sort.Direction.ASC, "created")
                        : Sort.by(Sort.Direction.DESC, "created")
        );

        //구분 필터
        List<TokenLedgerType> types = switch (category.toUpperCase()) {
            case "CHARGE" -> List.of(TokenLedgerType.CHARGE);
            case "RECLAIM" -> List.of(TokenLedgerType.RECLAIM);
            case "ISSUE" -> List.of(TokenLedgerType.ISSUE);
            case "REWARD" -> List.of(TokenLedgerType.REWARD);

            default -> List.of(TokenLedgerType.CHARGE, TokenLedgerType.RECLAIM, TokenLedgerType.ISSUE, TokenLedgerType.REWARD);
        };

        //기간 계산
        LocalDateTime from = null;
        LocalDateTime to = null;

        if (allDatePeriod != null) {
            switch (allDatePeriod.toUpperCase()) {
                case "TODAY" -> from = LocalDate.now().atStartOfDay();
                case "1WEEK" -> from = LocalDateTime.now().minusWeeks(1);
                case "1MONTH" -> from = LocalDateTime.now().minusMonths(1);
                case "3MONTH" -> from = LocalDateTime.now().minusMonths(3);
                case "ALL" -> from = null;
            }
        }

        if (startDate != null && endDate != null) {
            from = startDate.atStartOfDay();
            to = endDate.atTime(LocalTime.MAX);
        } else if (from != null && to == null) {
            to = LocalDateTime.now();
        }

        Date start = toDate(from);
        Date end = toDate(to);

        //Repository 호출
        Page<TokenLedger> ledgers;
        boolean hasContract = contractAddress != null && !contractAddress.isBlank();
        boolean hasDate = start != null && end != null;

        if (hasContract && hasDate) {
            ledgers = tokenLedgerRepository.findAllByContract_ContractAddressAndTypeInAndCreatedBetween(
                    contractAddress, types, start, end, pageable);
        } else if (hasContract) {
            ledgers = tokenLedgerRepository.findAllByContract_ContractAddressAndTypeIn(
                    contractAddress, types, pageable);
        } else if (hasDate) {
            ledgers = tokenLedgerRepository.findAllByTypeInAndCreatedBetween(
                    types, start, end, pageable);
        } else {
            ledgers = tokenLedgerRepository.findAllByTypeIn(types, pageable);
        }

        return ledgers.map(TokenLedgerDto::from);
    }

    /**
     * 🔹 전체 사용자 거래내역 조회 (userId 없이)
     */
    public Page<TokenLedgerDto> getAllUserLedgerHistory(int page, int size) {
        PageRequest pageable = PageRequest.of(page, size);
        Page<TokenLedger> ledgers = tokenLedgerRepository.findAllByOrderByCreatedDesc(pageable);
        return ledgers.map(TokenLedgerDto::from);
    }
}