package com.kaii.dentix.domain.blockChain.token.application;

import com.kaii.dentix.domain.blockChain.token.dao.TokenLedgerRepository;
import com.kaii.dentix.domain.blockChain.token.domain.TokenLedger;
import com.kaii.dentix.domain.blockChain.token.dto.TokenLedgerDto;
import com.kaii.dentix.domain.blockChain.token.dto.TokenLedgerSummaryDto;
import com.kaii.dentix.domain.blockChain.token.dto.UserTokenSummaryDto;
import com.kaii.dentix.domain.blockChain.token.type.TokenLedgerType;
import com.kaii.dentix.domain.blockChain.wallet.dao.UserWalletRepository;
import com.kaii.dentix.domain.blockChain.wallet.domain.UserWallet;
import com.kaii.dentix.domain.jwt.JwtTokenUtil;
import com.kaii.dentix.domain.user.dao.UserRepository;
import com.kaii.dentix.domain.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.List;
@Slf4j
@Service
@RequiredArgsConstructor
public class TokenLedgerService {
    private final UserRepository userRepository;
    private final UserWalletRepository userWalletRepository;
    private final TokenLedgerRepository tokenLedgerRepository;
    private final JwtTokenUtil jwtTokenUtil;
    /**
     * 사용자의 토큰 거래내역 조회
     */
    @Transactional(readOnly = true)
    public List<TokenLedgerDto> getUserTokenHistory(Long userId) {
        UserWallet userWallet = userWalletRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new RuntimeException("사용자 지갑을 찾을 수 없습니다."));

        List<TokenLedger> ledgers = tokenLedgerRepository.findAllByUserWallet(userWallet);

        return ledgers.stream()
                .map(TokenLedgerDto::from)
                .toList();
    }

    /**
     *사용자 잔여 토큰 수량 조회
     */
    @Transactional(readOnly = true)
    public Long getUserTokenBalance(Long userId) {
        UserWallet userWallet = userWalletRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new RuntimeException("사용자 지갑을 찾을 수 없습니다."));
        return userWallet.getBalance();
    }

    @Transactional(readOnly = true)
    public UserTokenSummaryDto getUserTokenSummary(Long userId) {
        //사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        //사용자 지갑 조회
        UserWallet wallet = userWalletRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new RuntimeException("사용자 지갑이 존재하지 않습니다."));

        //잔액
        Long balance = wallet.getBalance();

        //적립 내역 (REWARD)
        List<TokenLedgerSummaryDto> earnedList = tokenLedgerRepository
                .findByReceiverUserWallet(wallet).stream()
                .filter(l -> l.getType() == TokenLedgerType.REWARD)
                .map(TokenLedgerSummaryDto::fromEntity)
                .toList();

        //사용 내역 (TRANSFER or USE)
        List<TokenLedgerSummaryDto> usedList = tokenLedgerRepository
                .findBySenderUserWallet(wallet).stream()
                .filter(l -> l.getType() == TokenLedgerType.USE || l.getType() == TokenLedgerType.TRANSFER)
                .map(TokenLedgerSummaryDto::fromEntity)
                .toList();

        //결과 조립
        return UserTokenSummaryDto.builder()
                .balance(balance)
                .earnedList(earnedList)
                .usedList(usedList)
                .build();
    }

    @Transactional(readOnly = true)
    public UserTokenSummaryDto getUserRewardLedgers(Long userId, String period, String sort) {
        //기간 계산
        LocalDateTime fromDateTime = switch (period.toUpperCase()) {
            case "1D" -> LocalDateTime.now().minusDays(1);
            case "3D" -> LocalDateTime.now().minusDays(3);
            case "7D" -> LocalDateTime.now().minusDays(7);
            default -> null;
        };

        Date fromDate = (fromDateTime != null)
                ? Date.from(fromDateTime.atZone(ZoneId.systemDefault()).toInstant())
                : null;

        Sort sortOption = Sort.by(sort.equalsIgnoreCase("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC, "created");

        //사용자 지갑 찾기
        UserWallet wallet = userWalletRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new RuntimeException("사용자 지갑이 존재하지 않습니다."));

        //Ledger 조회
        List<TokenLedger> ledgers = (fromDate == null)
                ? tokenLedgerRepository.findByReceiverUserWalletAndType(wallet, TokenLedgerType.REWARD, sortOption)
                : tokenLedgerRepository.findByReceiverUserWalletAndTypeAndCreatedAfter(wallet, TokenLedgerType.REWARD, fromDate, sortOption);

        //balance + earnedList 담아서 반환
        return UserTokenSummaryDto.builder()
                .balance(wallet.getBalance()) //현재 잔액 추가
                .earnedList(ledgers.stream()
                        .map(TokenLedgerSummaryDto::fromEntity)
                        .toList())
                .usedList(Collections.emptyList())
                .build();
    }

    public UserTokenSummaryDto getUserUseLedgers(Long userId, String period, String sort) {
        //기간 계산
        LocalDateTime fromDateTime = switch (period.toUpperCase()) {
            case "1D" -> LocalDateTime.now().minusDays(1);
            case "3D" -> LocalDateTime.now().minusDays(3);
            case "7D" -> LocalDateTime.now().minusDays(7);
            default -> null;
        };

        Date fromDate = (fromDateTime != null)
                ? Date.from(fromDateTime.atZone(ZoneId.systemDefault()).toInstant())
                : null;

        Sort sortOption = Sort.by(sort.equalsIgnoreCase("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC, "created");

        //사용자 지갑 찾기
        UserWallet wallet = userWalletRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new RuntimeException("사용자 지갑이 존재하지 않습니다."));

        //사용 내역 타입 정의
        List<TokenLedgerType> useTypes = List.of(
                TokenLedgerType.TRANSFER,
                TokenLedgerType.MANUAL,
                TokenLedgerType.RETRIEVE
        );

        //Ledger 조회
        List<TokenLedger> ledgers = (fromDate == null)
                ? tokenLedgerRepository.findBySenderUserWalletAndTypeIn(wallet, useTypes, sortOption)
                : tokenLedgerRepository.findBySenderUserWalletAndTypeInAndCreatedAfter(wallet, useTypes, fromDate, sortOption);

        log.info("💸 [사용 내역 조회] userId={}, period={}, sort={}, fromDate={}, 결과={}",
                userId, period, sort, fromDate, ledgers.size());

        //DTO로 감싸서 balance 함께 반환
        return UserTokenSummaryDto.builder()
                .balance(wallet.getBalance()) // 현재 보유 잔액 추가
                .usedList(ledgers.stream()
                        .map(TokenLedgerSummaryDto::fromEntity)
                        .toList())
                .earnedList(Collections.emptyList()) //null 방지 (빈 리스트로)
                .build();
    }


}