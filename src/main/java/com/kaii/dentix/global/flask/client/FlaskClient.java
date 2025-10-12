package com.kaii.dentix.global.flask.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kaii.dentix.domain.blockChain.token.dao.TokenContractRepository;
import com.kaii.dentix.domain.blockChain.token.domain.TokenContract;
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

    /**
     * Flask 공통 응답 검증
     * state = OK → 성공
     * state = OOPS → 실패 (예외 발생)
     */
    private void checkFlaskResponse(Map<String, Object> body, String action) {
        if (body == null || body.isEmpty()) {
            throw new RuntimeException("Flask 응답이 비어 있습니다. (" + action + ")");
        }

        String state = body.getOrDefault("state", "OK").toString().toUpperCase();
        if ("OOPS".equals(state)) {
            String msg = body.getOrDefault("msg", "Flask에서 알 수 없는 오류 발생").toString();
            log.error("Flask {} 실패: {}", action, msg);
            throw new RuntimeException("Flask " + action + " 실패: " + msg);
        }
    }

    // ============================================
    // 계정(Account) 관련 API
    // ============================================

    /**
     * 1. 지갑 생성 요청
     * POST /acc/create
     */
    public FlaskCreateWalletResponse createWallet() {
        String url = FLASK_BASE + "/acc/create";
        log.info("Flask 지갑 생성 요청: {}", url);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, null, Map.class);
            Map<String, Object> body = response.getBody();

            checkFlaskResponse(body, "지갑 생성");

            FlaskCreateWalletResponse result = new FlaskCreateWalletResponse();
            result.setAddress(body.get("address").toString());
            return result;
        } catch (Exception e) {
            log.error("Flask 지갑 생성 실패", e);
            throw new RuntimeException("Flask 지갑 생성 실패: " + e.getMessage(), e);
        }
    }

    /**
     * 2. address 기반 private key 조회
     * POST /acc/get_private_key
     */
    public FlaskPrivateKeyResponse getPrivateKey(String address) {
        String url = FLASK_BASE + "/acc/get_private_key";
        log.info("Flask private key 조회: address={}", address);

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, String> requestBody = Map.of("address", address);
            HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, request, Map.class);
            Map<String, Object> body = response.getBody();
            log.info("Flask 응답 Body: {}", body);

            checkFlaskResponse(body, "private key 조회");

            // 실제 값 추출
            String privateKey = (String) body.get("private_key");
            if (privateKey == null) {
                throw new RuntimeException("Flask 응답에 private_key가 없습니다.");
            }

            // DTO에 값 세팅 후 반환
            FlaskPrivateKeyResponse result = FlaskPrivateKeyResponse.builder()
                    .private_key(privateKey)
                    .build();

            log.info("Flask private key 응답: {}", result.getPrivate_key());
            return result;

        } catch (Exception e) {
            log.error("Flask private key 조회 실패", e);
            throw new RuntimeException("Flask private key 조회 실패: " + e.getMessage(), e);
        }
    }

    /**
     * 3. 모든 계정 목록 조회
     * GET or POST /acc/get_list
     */
    public Map<String, Object> getAllAccounts() {
        String url = FLASK_BASE + "/acc/get_list";
        log.info("Flask 계정 목록 조회: {}", url);

        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            Map<String, Object> body = response.getBody();
            checkFlaskResponse(body, "계정 목록 조회");

            return body;
        } catch (Exception e) {
            log.error("Flask 계정 목록 조회 실패", e);
            throw new RuntimeException("계정 목록 조회 실패: " + e.getMessage(), e);
        }
    }

    // ============================================
    // 토큰(Token) 관련 API
    // ============================================

    /**
     * 4. 토큰 컨트랙트 생성
     * POST /token/create
     *
     * Request: {
     *   "token_name": "MyToken",
     *   "token_symbol": "MTK",
     *   "supply": 1000000
     * }
     * Response: {
     *   "contract_address": "0xabc..."
     * }
     */
    public Map<String, Object> createToken(String name, String symbol, Long supply) {
        String url = FLASK_BASE + "/token/create";

        Map<String, Object> req = Map.of(
                "token_name", name,
                "token_symbol", symbol,
                "supply", supply
        );

        log.info("Flask 토큰 생성 요청: {}", req);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, req, String.class);
            log.info("Flask raw response: {}", response.getBody());

            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(response.getBody(), Map.class);
        } catch (Exception e) {
            log.error("Flask createToken 실패", e);
            throw new RuntimeException("Flask createToken 실패: " + e.getMessage());
        }
    }
    /**
     * 5. 토큰 전송
     * POST /token/transfer
     *
     * Request: {
     *   "contract_address": "0xabc...",
     *   "sender": "0x123...",
     *   "sender_private_key": "privatekey...",
     *   "receiver": "0x456...",
     *   "amount": 100
     * }
     * Response: {
     *   "Date": "2025-07-29T01:23:45",
     *   "Sender": "0x123...",
     *   "Receiver": "0x456...",
     *   "Amount": 100
     * }
     */
    public Map<String, Object> transferToken(String sender, String senderPrivateKey,
                                             String receiver, Long amount) {
        String contractAddress = getActiveContractAddress();

        String url = FLASK_BASE + "/token/transfer";
        log.info("Flask 토큰 전송 요청: sender={}, receiver={}, amount={}, contract={}",
                sender, receiver, amount, contractAddress);

        Map<String, Object> body = new HashMap<>();
        body.put("contract_address", contractAddress);
        body.put("sender", sender);
        body.put("sender_private_key", senderPrivateKey);
        body.put("receiver", receiver);
        body.put("amount", amount);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);


        try {
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
            Map<String, Object> responseBody = response.getBody();

            checkFlaskResponse(responseBody, "토큰 전송");
            log.info("Flask 토큰 전송 성공: {}", responseBody);
            return responseBody;
        } catch (Exception e) {
            log.error("Flask /token/transfer 실패", e);
            throw new RuntimeException("Flask 토큰 전송 실패: " + e.getMessage(), e);
        }
    }

    /**
     * 6. 모든 주소의 잔액 리스트 조회
     * POST /token/balance_list
     *
     * Request: {
     *   "contract_address": "0xabc..."
     * }
     * Response: [
     *   ["0x123...", 900],
     *   ["0x456...", 100]
     * ]
     */
    public List<List<Object>> getBalanceList() {
        String contractAddress = "0x182827C979cd00DAB4C02C812fCdF2D3E84AeD5afca";
        String url = FLASK_BASE + "/token/balance_list";
        Map<String, Object> req = Map.of("contract_address", contractAddress);

        try {
            ResponseEntity<String> response = new org.springframework.web.client.RestTemplate()
                    .postForEntity(url, req, String.class);
            String body = response.getBody();

            if (body == null || body.isBlank()) {
                throw new RuntimeException("Flask 응답이 비어 있습니다.");
            }

            ObjectMapper mapper = new ObjectMapper();

            // JSON 루트가 배열로 시작할 때
            if (body.trim().startsWith("[")) {
                List<List<Object>> list = mapper.readValue(body, new TypeReference<List<List<Object>>>() {});
                System.out.println("Flask 잔액 리스트 수신: " + list.size());
                return list;
            }

            // JSON 루트가 객체일 경우 (status, data 포함)
            Map<String, Object> json = mapper.readValue(body, Map.class);
            Object data = json.get("data");

            if (data instanceof List<?> list) {
                return (List<List<Object>>) list;
            }

            throw new RuntimeException("Flask data 필드 형식이 올바르지 않습니다.");

        } catch (Exception e) {
            throw new RuntimeException("Flask 잔액 조회 실패: " + e.getMessage(), e);
        }
    }

    public Map<String, Object> getTokenBalance(String address) {
        String url = FLASK_BASE + "/token/balance";
        log.info("📤 Flask 잔액 조회 요청: {}", address);

        Map<String, Object> req = Map.of("address", address);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, req, String.class);
            log.info("📦 Flask raw response: {}", response.getBody());

            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> body = mapper.readValue(response.getBody(), Map.class);
            return body;

        } catch (Exception e) {
            log.error("Flask 잔액 조회 실패: {}", e.getMessage(), e);
            return Map.of(
                    "status", "error",
                    "message", e.getMessage()
            );
        }
    }
    /**
     * 7. 토큰 회수 (Owner → Holder)
     * POST /token/retrieve
     *
     * Request: {
     *   "contract_address": "0xabc...",
     *   "holder": "0x123...",
     *   "receiver": "0xowner...",
     *   "amount": 50
     * }
     * Response: {
     *   "Date": "2025-07-29T01:45:00",
     *   "Target": "0x123...",
     *   "Amount": 50
     * }
     */
    /**
     * Flask 서버에 토큰 전송/회수 요청
     */
    public void transferToken(Map<String, Object> body) {
        String url = FLASK_BASE + "/token/transfer";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
            log.info("Flask 응답: {}", response.getBody());
        } catch (Exception e) {
            log.error("Flask 요청 실패", e);
            throw new RuntimeException("Flask 요청 실패: " + e.getMessage());
        }
    }

    // ============================================
    // 유틸리티 메서드
    // ============================================

    /**
     * 활성화된 토큰 컨트랙트 주소 조회
     */
    private String getActiveContractAddress() {
        TokenContract activeContract = tokenContractRepository.findActiveContract()
                .orElseThrow(() -> new RuntimeException("활성화된 토큰 컨트랙트가 없습니다."));
        return activeContract.getContractAddress();
    }
}