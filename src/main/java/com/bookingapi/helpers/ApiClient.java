package com.bookingapi.helpers;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.Map;
import java.util.Objects;

public class ApiClient {

    private final String baseUrl;

    public ApiClient(String baseUrl) {
        this.baseUrl = Objects.requireNonNull(baseUrl, "baseUrl");
    }

    private RequestSpecification request() {
        return RestAssured.given().baseUri(baseUrl);
    }


    public Response postRequest(String endpoint, Object body, Map<String, String> headers) {
        RequestSpecification req = request()
                .contentType(ContentType.JSON)
                .accept("application/json");

        if (headers != null && !headers.isEmpty()) {
            req.headers(headers);
        }

        if (body != null) {
            req.body(body);
        }

        return req.post(endpoint);
    }

    public Response getRequest(String endpoint,
                               Map<String, String> queryParams,
                               Map<String, String> headers) {

        RequestSpecification req = request()
                .accept("application/json");

        if (headers != null && !headers.isEmpty()) {
            req.headers(headers);
        }

        if (queryParams != null && !queryParams.isEmpty()) {
            req.queryParams(queryParams);
        }

        return req.get(endpoint);
    }



    public Response putRequest(String endpoint, Object body, Map<String, String> headers) {
        RequestSpecification req = request()
                .contentType(ContentType.JSON)
                .accept("application/json");

        if (headers != null && !headers.isEmpty()) {
            req.headers(headers);
        }

        if (body != null) {
            req.body(body);
        }

        return req.put(endpoint);
    }

    public Response patchRequest(String endpoint, Object body, Map<String, String> headers) {
        RequestSpecification req = request()
                .contentType(ContentType.JSON)
                .accept("application/json");

        if (headers != null && !headers.isEmpty()) {
            req.headers(headers);
        }

        if (body != null) {
            req.body(body);
        }

        return req.patch(endpoint);
    }

    public Response deleteRequest(String endpoint, Map<String, String> headers) {
        RequestSpecification req = request()
                .accept("application/json");

        if (headers != null && !headers.isEmpty()) {
            req.headers(headers);
        }

        return req.delete(endpoint);
    }
}
