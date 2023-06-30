package com.kaii.dentix.global.common.response;

public class ResponseMessage {

    public static final String SUCCESS_MSG = "API Call successful";

    public static final String BAD_REQUEST_MSG = "Bad request";

    public static final String UNAUTHORIZED_MSG = "Authentication failed {Msg}";

    public static final String FORBIDDEN_MSG = "Permission error";

    public static final String METHOD_NOT_ALLOWED_MSG = "Method not allow";

    public static final String HTTP_MESSAGE_NOT_READABLE_MSG = "Http message not readable error({msg})";

    public static final String EXPECTATION_FAILED_MSG = "{FieldName} is required";

    public static final String UNPROCESSABLE_ENTITY_MSG = "Not found Data {DataName}";

    public static final String ALREADY_DATA_MSG = "Already Data";

    public static final String INTERNAL_SERVER_ERROR_MSG = "Unknown error";

    public static final String REQUIRED_VERSION_INFO_MSG = "기기 종류 및 버전 정보가 없거나 잘못되었습니다.";

    public static final String UPDATE_REQUIRED_MSG = "앱 업데이트가 필요합니다.";
}
