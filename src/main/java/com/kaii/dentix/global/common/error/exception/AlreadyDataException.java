package com.kaii.dentix.global.common.error.exception;

public class AlreadyDataException extends RuntimeException{
    public AlreadyDataException() {
        super("");
    }
    public AlreadyDataException(String message) {
        super(message);
    }
}