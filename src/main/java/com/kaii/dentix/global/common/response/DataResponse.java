package com.kaii.dentix.global.common.response;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class DataResponse<T> extends SuccessResponse {

    public T response;

    public DataResponse(int rt, String rtMsg, T response) {
        super(rt, rtMsg);
        this.response = response;
    }
}
