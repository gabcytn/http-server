package com.gabcytn.http;

import java.util.HashMap;
import java.util.Map;

public class ResponseBuilder
{
    private final Map<String, String> headers = new HashMap<>();
    private String httpVersion;
    private HttpStatus httpStatus;
    private String body;

    public ResponseBuilder () {}

    public ResponseBuilder setHttpVersion (String httpVersion) {
        this.httpVersion = httpVersion;
        return this;
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
        return httpVersion;
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
