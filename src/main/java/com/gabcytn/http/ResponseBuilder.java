package com.gabcytn.http;

import java.util.HashMap;
import java.util.Map;

public class ResponseBuilder
{
    private static final String CRLF = "\r\n";
    private final Map<String, String> headers = new HashMap<>();
    private String httpVersion;
    private String status;
    private int statusCode;
    private String body;

    public ResponseBuilder () {}

    public ResponseBuilder setHttpVersion (String httpVersion) {
        this.httpVersion = httpVersion;
        return this;
    }

    public ResponseBuilder setStatusCode (int statusCode) {
        this.statusCode = statusCode;
        return this;
    }

    public ResponseBuilder setStatus (String status) {
        this.status = status;
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

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getHttpVersion() {
        return httpVersion;
    }

    public String getStatus() {
        return status;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getBody() {
        return body;
    }

    public Response build() {
        return new Response(this);
    }
}
