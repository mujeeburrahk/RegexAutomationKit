package com.api;

import java.util.Map;

public class APIResponseModel<body> {
    private final int statusCode;
    private final String statusMessage;
    private final body responseBody;
    private final Map<String, String> headers;

    public APIResponseModel(int statusCode, String statusMessage){
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
        this.responseBody = null;
        this.headers = null;
    }

    public APIResponseModel(int statusCode, String statusMessage, body responseBody, Map<String, String> headers) {
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
        this.responseBody = responseBody;
        this.headers = headers;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public body getResponseBody() {
        return responseBody;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }
}