package com.api;

import com.enums.RequestType;
import java.util.Map;

public class APIRequestModel<body> {
    private final RequestType requestType;
    private final String endPoints;
    private body requestBody;
    private Map<String, String> headers;

    public APIRequestModel(RequestType requestType, String endPoints) {
        this.requestType = requestType;
        this.endPoints = endPoints;
    }

    public RequestType getRequestType() {
        return requestType;
    }

    public String getEndPoints() {
        return endPoints;
    }

    public void setRequestBody(body requestBody) {
        this.requestBody = requestBody;
    }

    public body getRequestBody() {
        return requestBody;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }
}