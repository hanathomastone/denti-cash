package com.kaii.dentix.global.common.response;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class DataResponse<T> extends SuccessResponse {

    public T response;

    @Override
    public String toString() {
        return "{"
            + "\"response\":" + response
            + "}";
    }
}
