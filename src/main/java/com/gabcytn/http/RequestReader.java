package com.gabcytn.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class RequestReader {
    private final BufferedReader bufferedReader;

    private String requestMethod;
    private String requestPath;
    private String httpVersion;
    private final Map<String, String> requestHeaders;
    private String body;
    private Boolean hasRequest = true;

    public RequestReader (InputStream inputStream) {
        this.bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        this.requestHeaders = new HashMap<>();
    }

    public void read () throws SocketTimeoutException
    {
        String line;
        try
        {
            // throws NPE if there's no request to read
            String[] statusLine = bufferedReader.readLine().split(" ");
            if (statusLine.length < 3)
                throw new NullPointerException("No request found");
            this.requestMethod = statusLine[0];
            this.requestPath = statusLine[1];
            this.httpVersion = statusLine[2];
            while (!(line = bufferedReader.readLine()).isEmpty())
            {
                String[] header = line.split(":" );
                requestHeaders.put(header[0].trim().toLowerCase(), header[1].trim().toLowerCase());
            }
            int contentLength = Integer.parseInt(requestHeaders.getOrDefault("content-length", "0"));
            if (contentLength != 0)
                readBody(contentLength);
        }
        catch (SocketTimeoutException e)
        {
            throw e;
        }
        catch (NullPointerException e)
        {
            this.hasRequest = false;
        }
        catch (IOException | RuntimeException e)
        {
            System.err.println("Error while reading request");
            System.err.println("\tMessage: " + e.getMessage());
            e.printStackTrace();
            this.body = "";
        }
    }

    private void readBody (int contentLength)
    {
        char[] bodyChars = new char[contentLength];
        int charsRead = 0;
        while (charsRead < contentLength)
        {
            int result;
            try {
                result = bufferedReader.read(bodyChars, charsRead, contentLength - charsRead);
            } catch (IOException e) {
                System.err.println("Error reading request body: " + e.getMessage());
                result = -1;
            }
            if (result == -1)
                break;
            charsRead += result;
        }
        this.body = new String(bodyChars, 0, charsRead);
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public String getRequestPath() {
        return requestPath;
    }

    public String getHttpVersion() {
        return httpVersion;
    }

    public Map<String, String> getRequestHeaders() {
        return requestHeaders;
    }

    public String getBody () {
        return body;
    }

    public Boolean getHasRequest () {
        return hasRequest;
    }
}
