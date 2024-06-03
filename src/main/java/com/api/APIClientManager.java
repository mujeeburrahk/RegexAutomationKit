package com.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.HashMap;
import java.util.Map;

public class APIClientManager {
    private static final Logger log = LogManager.getLogger(APIClientManager.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void setBaseURI(String baseURI) {
        log.info("Setting {} base URI for RestAssured", baseURI);
        RestAssured.baseURI = baseURI;
    }

    public static <T, R> APIResponseModel<R> sendRequest(APIRequestModel<T> requestModel, Class<R> responseClass) {
        log.info("Sending {} request", requestModel.getRequestType());
        try {
            String requestBodyJson = objectMapper.writeValueAsString(requestModel.getRequestBody());
            log.info("Request body JSON which will be sent is: {}", requestBodyJson);
            ContentType contentType = getContentType(requestBodyJson);
            log.info("Content type which will be used is: {}", contentType);
            var requestSpec = RestAssured.given();
            if (requestModel.getHeaders() != null && !requestModel.getHeaders().isEmpty()) {
                log.info("Request headers which will be sent is: {}", requestModel.getHeaders());
                requestSpec.headers(requestModel.getHeaders());
            }
            requestSpec.contentType(contentType).body(requestBodyJson);
            Response response = requestSpec.request(String.valueOf(requestModel.getRequestType()), requestModel.getEndPoints());
            log.info("Raw response body is: {}", response);
            int statusCode = response.getStatusCode();
            log.info("Response status code is: {}", statusCode);
            String message = response.getStatusLine();
            log.info("Response status message is: {}", message);
            Map<String, String> responseHeaders = getResponseHeaders(response.getHeaders());
            log.info("Response headers is: {}", message);
            R responseBody = objectMapper.readValue(response.getBody().asString(), responseClass);
            log.info("Parsed response body is: {}", message);
            return new APIResponseModel<>(statusCode, message, responseBody, responseHeaders);
        } catch (JsonProcessingException e) {
            log.error("Error processing request body: " + e.getMessage(), e);
            return new APIResponseModel<>(500, "Error processing request body: " + e.getMessage());
        } catch (Exception e) {
            log.error("Error processing request: " + e.getMessage(), e);
            return new APIResponseModel<>(500, "Error processing request: " + e.getMessage());
        }
    }

    private static ContentType getContentType(String requestBodyJson) {
        if (requestBodyJson.startsWith("{") || requestBodyJson.startsWith("["))
            return ContentType.JSON;
        return ContentType.ANY;
    }

    private static Map<String, String> getResponseHeaders(Headers headers) {
        Map<String, String> responseHeaders = new HashMap<>();
        for (Header header : headers) responseHeaders.put(header.getName(), header.getValue());
        return responseHeaders;
    }
}