package com.kaii.dentix.domain.blockChain.token.dto;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter @Setter
public class FlaskTransferResponse {
    // 응답이 자유로운 형식이면 map으로 받아두면 안전
    private Map<String, Object> data = new HashMap<>();

    @JsonAnySetter
    public void set(String name, Object value) {
        data.put(name, value);
    }
}