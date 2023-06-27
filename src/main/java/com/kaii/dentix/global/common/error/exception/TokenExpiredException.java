package com.kaii.dentix.global.common.error.exception;

import com.kaii.dentix.global.common.response.ResponseMessage;

public class TokenExpiredException extends RuntimeException {

    public TokenExpiredException() { super(ResponseMessage.FORBIDDEN_MSG); }

}
