package com.kaii.dentix.global.common.error;

import com.kaii.dentix.global.common.util.JsonUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.io.IOException;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ErrorResponse {

    private int rt;
    private String rtMsg;

    public static ErrorResponse of(HttpStatus httpStatus, String msg) {
        return new ErrorResponse(httpStatus.value(), msg);
    }

    public static void of(HttpServletResponse response, HttpStatus httpStatus, String message) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(200);
        response.getWriter().write(JsonUtil.convertObjectToJson(ErrorResponse.of(httpStatus, message)));
    }

}
