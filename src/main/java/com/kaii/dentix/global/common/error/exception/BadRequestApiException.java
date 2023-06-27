package com.kaii.dentix.global.common.error.exception;

public class BadRequestApiException extends RuntimeException {
    public BadRequestApiException(String message) {
        super(message);
    }
}
