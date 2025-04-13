package com.gabcytn.http;

import java.util.Map;

public class Response
{
    private static final String CRLF = "\r\n";
    private final Map<String, String> headers;
    private final String httpVersion;
    private final HttpStatus httpStatus;
    private final String body;

    public Response (ResponseBuilder responseBuilder) {
        this.httpVersion = responseBuilder.getHttpVersion();
        this.httpStatus = responseBuilder.getHttpStatus();
        this.headers = responseBuilder.getHeaders();
        this.body = responseBuilder.getBody();
    }

    @Override
    public String toString () {
        if (body != null) {
            return getStatusLine() +
                    getHeaders() +
                    body;
        }
        return getStatusLine() +
                getHeaders();
    }

    private String getStatusLine () {
        return httpVersion +
                " " + httpStatus.getStatusCode() +
                " " + httpStatus.getStatus() +
                CRLF;
    }

    private String getHeaders () {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> map : headers.entrySet()) {
            sb.append(map.getKey())
                    .append(": ")
                    .append(map.getValue())
                    .append(CRLF);
        }
        sb.append(CRLF);
        return sb.toString();
    }
}
