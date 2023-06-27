package com.kaii.dentix.common;

import org.springframework.restdocs.operation.preprocess.OperationRequestPreprocessor;
import org.springframework.restdocs.operation.preprocess.OperationResponsePreprocessor;

import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;

public class ApiDocumentUtils {

    public static OperationRequestPreprocessor getDocumentRequest() {
        return preprocessRequest(modifyUris() // 문서상 uri 를 기본값인 http://localhost:8080 에서
                .scheme("https")                  // https://docs.api.com 으로 변경하기 위해 사용합니다.
                .host("docs.api.com")
                .removePort(), prettyPrint());  // prettyPrint()는 JSON 포맷 정렬
    }

    public static OperationResponsePreprocessor getDocumentResponse() {
        return preprocessResponse(prettyPrint());
    }

}