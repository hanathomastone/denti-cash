package com.kaii.dentix.global.flask.client;

import com.kaii.dentix.domain.blockChain.token.dao.TokenContractRepository;
import com.kaii.dentix.domain.blockChain.token.domain.TokenContract;
import com.kaii.dentix.domain.blockChain.token.dto.*;
import com.kaii.dentix.global.flask.dto.FlaskCreateWalletResponse;
import com.kaii.dentix.global.flask.dto.FlaskPrivateKeyResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class FlaskClient {

    private final RestTemplate restTemplate = new RestTemplate();
    private final TokenContractRepository tokenContractRepository;
    private static final String FLASK_BASE = "http://220.149.235.79:5000";

    /** 지갑 생성 요청 */
    public FlaskCreateWalletResponse createWallet() {
        String url = FLASK_BASE + "/acc/create";
        try {
            ResponseEntity<FlaskCreateWalletResponse> response =
                    restTemplate.exchange(url, HttpMethod.POST, null, FlaskCreateWalletResponse.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody();
            } else {
                throw new RuntimeException("지갑 생성 실패: " + response.getStatusCode());
            }
        } catch (Exception e) {
            log.error("🚨 Flask 지갑 생성 실패", e);
            throw new RuntimeException("Flask 지갑 생성 실패", e);
        }
    }

    /** address 기반 private key 조회 */
    public FlaskPrivateKeyResponse getPrivateKey(String address) {
        String url = FLASK_BASE + "/acc/get_private_key";
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, String>> request = new HttpEntity<>(Map.of("address", address), headers);

            ResponseEntity<FlaskPrivateKeyResponse> response =
                    restTemplate.exchange(url, HttpMethod.POST, request, FlaskPrivateKeyResponse.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody();
            } else {
                throw new RuntimeException("private key 조회 실패: " + response.getStatusCode());
            }
        } catch (Exception e) {
            log.error("🚨 Flask private key 조회 실패", e);
            throw new RuntimeException("Flask private key 조회 실패", e);
        }
    }

    /** admin token 충전 */
    public TokenCreateResponse createToken(TokenCreateRequest request) {
        String url = FLASK_BASE + "/token/create";
        try {
            TokenCreateResponse res = restTemplate.postForObject(
                    url,
                    request,
                    TokenCreateResponse.class
            );
            if (res == null || res.getContractAddress() == null) {
                throw new RuntimeException("Flask에서 contract_address를 반환하지 않았습니다.");
            }
            log.info("✅ Flask Token 생성 완료: {}", res.getContractAddress());
            return res;
        } catch (Exception e) {
            log.error("❌ Flask 토큰 생성 실패", e);
            throw new RuntimeException("Flask 토큰 생성 실패", e);
        }
    }

    /** 토큰 전송 요청 */
//    public TokenTransferResponse transferToken(TokenTransferRequest request) {
//        String url = FLASK_BASE + "/token/transfer";
//        try {
//            TokenTransferResponse res = restTemplate.postForObject(
//                    url,
//                    request,
//                    TokenTransferResponse.class
//            );
//            if (res == null) {
//                throw new RuntimeException("Flask 응답이 없습니다.");
//            }
//            log.info("✅ Flask 토큰 전송 성공: {} -> {} ({}개)", res.getSender(), res.getReceiver(), res.getAmount());
//            return res;
//        } catch (Exception e) {
//            log.error("❌ Flask 토큰 전송 실패", e);
//            throw new RuntimeException("Flask 토큰 전송 실패", e);
//        }
//    }
    // ✅ 관리자 토큰 잔액 조회
//    public Long getAdminWalletBalance(String address) {
//        String url = FLASK_BASE + "/token/balance_list";
//        try {
//            Map<?, ?> response = restTemplate.getForObject(url, Map.class);
//            if (response == null || !response.containsKey("balance")) {
//                throw new RuntimeException("Flask 응답에 balance 필드가 없습니다.");
//            }
//            Long balance = ((Number) response.get("balance")).longValue();
//            log.info("✅ Flask 잔액 조회 성공: {} → {}", address, balance);
//            return balance;
//        } catch (Exception e) {
//            log.error("❌ Flask 잔액 조회 실패: {}", e.getMessage());
//            throw new RuntimeException("Flask 잔액 조회 실패", e);
//        }
//    }

    // ✅ 단일 활성 토큰 기준으로 balance_list 호출
    /**
     * 모든 주소의 잔액 리스트 조회
     */
    public List<List<Object>> getBalanceList() {
        TokenContract activeContract = tokenContractRepository.findActiveContract()
                .orElseThrow(() -> new RuntimeException("활성화된 토큰 컨트랙트가 없습니다."));

        String url = FLASK_BASE + "/token/balance_list";
        Map<String, Object> req = Map.of("contract_address", activeContract.getContractAddress());

        try {
            // ✅ Flask가 [ [address, balance], ... ] 형태로 반환하므로 List<List<Object>> 로 받는다
            List<List<Object>> response = restTemplate.postForObject(url, req, List.class);

            if (response == null || response.isEmpty()) {
                throw new RuntimeException("Flask에서 잔액 리스트를 반환하지 않았습니다.");
            }

            log.info("✅ Flask balance_list 성공 ({}개 항목)", response.size());
            return response;

        } catch (Exception e) {
            log.error("❌ Flask balance_list 실패", e);
            throw new RuntimeException("Flask 잔액 리스트 조회 실패", e);
        }
    }

//    public Map<String, Object> transferToken(String contractAddress,
//                                             String sender,
//                                             String senderPrivateKey,
//                                             String receiver,
//                                             Long amount) {
//
//        String url = FLASK_BASE + "/token/transfer";
//
//        Map<String, Object> req = Map.of(
//                "contract_address", contractAddress,
//                "sender", sender,
//                "sender_private_key", senderPrivateKey,
//                "receiver", receiver,
//                "amount", amount
//        );
//
//        try {
//            Map<String, Object> response = restTemplate.postForObject(url, req, Map.class);
//
//            if (response == null || response.isEmpty()) {
//                throw new RuntimeException("Flask에서 응답이 없습니다.");
//            }
//
//            log.info("✅ Flask 토큰 전송 성공: {}", response);
//            return response;
//
//        } catch (Exception e) {
//            log.error("❌ Flask 토큰 전송 실패", e);
//            throw new RuntimeException("Flask 토큰 전송 실패", e);
//        }
//    }


    public TokenTransferResponse transferToken(String contractAddress, String sender, String senderPrivateKey, String receiver, Long amount) {
        String url = FLASK_BASE + "/token/transfer";

        Map<String, Object> payload = Map.of(
                "contract_address", contractAddress,
                "sender", sender,
                "sender_private_key", senderPrivateKey,
                "receiver", receiver,
                "amount", amount
        );

        ResponseEntity<TokenTransferResponse> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                new HttpEntity<>(payload, jsonHeader()),
                TokenTransferResponse.class
        );

        if (response.getBody() == null || response.getBody().getReceiver() == null) {
            throw new RuntimeException("Flask에서 유효한 응답을 반환하지 않았습니다.");
        }

        return response.getBody();
    }

    private HttpHeaders jsonHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}