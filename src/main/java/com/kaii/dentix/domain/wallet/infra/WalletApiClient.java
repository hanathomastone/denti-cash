package com.kaii.dentix.domain.wallet.infra;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class WalletApiClient {

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String CREATE_WALLET_URL = "http://220.149.235.79:5000/acc/create";
    private static final String GET_PRIVATE_KEY_URL = "http://220.149.235.79:5000/acc/get_private_key";
    private static final String TOKEN_TRANSFER_URL = "http://220.149.235.79:5000/token/transfer";
    // 1. 지갑 address 생성
    public String createWalletAddress() {
        Map<String, Object> response = restTemplate.postForObject(CREATE_WALLET_URL, null, Map.class);
        if (response == null || !response.containsKey("address")) {
            throw new RuntimeException("지갑 주소 생성 실패");
        }
        return (String) response.get("address");
    }

    // 2. private_key 조회
    public String getPrivateKey(String address) {
        Map<String, String> request = Map.of("address", address);
        Map<String, Object> response = restTemplate.postForObject(GET_PRIVATE_KEY_URL, request, Map.class);
        if (response == null || !response.containsKey("private_key")) {
            throw new RuntimeException("개인키 조회 실패");
        }
        return (String) response.get("private_key");
    }




    public void transferToken(String contractAddress,
                              String sender,
                              String senderPrivateKey,
                              String receiver,
                              Long amount) {

        Map<String, Object> request = Map.of(
                "contract_address", contractAddress,
                "sender", sender,
                "sender_private_key", senderPrivateKey,
                "receiver", receiver,
                "amount", amount
        );

        Map<String, Object> response = restTemplate.postForObject(TOKEN_TRANSFER_URL, request, Map.class);

        if (response == null || !response.containsKey("Amount")) {
            throw new RuntimeException("토큰 전송 실패: " + response);
        }
    }
}
