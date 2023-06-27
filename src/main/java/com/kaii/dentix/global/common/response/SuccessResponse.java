package com.kaii.dentix.global.common.response;

public class SuccessResponse {

    public int rt;

    public String rtMsg;

    public SuccessResponse() {
        this.rt = 200;
        this.rtMsg = ResponseMessage.SUCCESS_MSG;
    }

    @Override
    public String toString() {
        return "{"
                + "\"rt\":\"" + rt + "\""
                + ", \"rtMsg\":\"" + rtMsg + "\""
                + "}";
    }

}
