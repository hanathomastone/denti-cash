package com.kaii.dentix.infra.tokensync;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class TokenSyncClient {

    private final RestTemplate tokenSyncRestTemplate;
    private final TokenSyncProperties props;

    private String url(String path) { return props.getBaseUrl() + path; }

    /** POST /token/balance_list {contract_address} -> { "0x..": INT, ... } */
    public Map<String, Object> balanceList(String contractAddress) {
        Map<String, Object> req = Map.of("contract_address", contractAddress);
        return tokenSyncRestTemplate.postForObject(url("/token/balance_list"), req, Map.class);
    }

    /** POST /token/refresh_balance 동일 응답 */
    public Map<String, Object> refreshBalance(String contractAddress) {
        Map<String, Object> req = Map.of("contract_address", contractAddress);
        return tokenSyncRestTemplate.postForObject(url("/token/refresh_balance"), req, Map.class);
    }

    /** POST /token/token_list -> [{token name, contract address, date, amount}, ...] */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> tokenList() {
        return tokenSyncRestTemplate.postForObject(url("/token/token_list"), null, List.class);
    }
}