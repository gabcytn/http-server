package com.gabcytn.http;

import java.util.HashMap;
import java.util.Map;

public class ResponseBuilder
{
    private static final String HTTP_VERSION = "HTTP/1.1";
    private final Map<String, String> headers = new HashMap<>();
    private HttpStatus httpStatus;
    private String body;

    public ResponseBuilder () {}

    public Response responseWithoutBody (HttpStatus httpStatus) {
        return this.setHttpStatus(httpStatus)
                .setHeader("Content-Length", "0")
                .setHeader("Connection", headers.getOrDefault("connection", "keep-alive"))
                .build();
    }

    public ResponseBuilder setHttpStatus (HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
        return this;
    }

    public ResponseBuilder setHeader (String key, String value) {
        this.headers.put(key, value);
        return this;
    }

    public ResponseBuilder setBody (String body) {
        this.body = body;
        return this;
    }

    public Map<String, String> getHeaders () {
        return headers;
    }

    public String getHttpVersion () {
        return HTTP_VERSION;
    }

    public HttpStatus getHttpStatus () {
        return httpStatus;
    }

    public String getBody () {
        return body;
    }

    public Response build () {
        return new Response(this);
    }
}
