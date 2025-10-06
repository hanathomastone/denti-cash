package com.kaii.dentix.domain.blockChain.token.application;

import com.kaii.dentix.domain.blockChain.token.dao.TokenContractRepository;
import com.kaii.dentix.domain.blockChain.token.domain.TokenContract;
import com.kaii.dentix.domain.blockChain.token.dto.TokenCreateRequest;
import com.kaii.dentix.domain.blockChain.token.dto.TokenCreateResponse;
import com.kaii.dentix.global.flask.client.FlaskClient;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenContractService {

    private final TokenContractRepository tokenContractRepository;
    private final FlaskClient flaskTokenClient;

    @Transactional
    public TokenContract createToken(TokenCreateRequest request) {
        // 1️⃣ Flask 요청
        TokenCreateResponse res = flaskTokenClient.createToken(request);

        // 2️⃣ DB 저장
        TokenContract contract = TokenContract.builder()
                .contractAddress(res.getContractAddress())
                .name(request.getTokenName())
                .decimals(18)
                .active(true)
                .build();

        tokenContractRepository.save(contract);
        log.info("✅ 새 토큰 저장 완료: {} ({})", contract.getName(), contract.getContractAddress());
        return contract;
    }
}