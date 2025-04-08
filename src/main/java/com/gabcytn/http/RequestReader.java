package com.gabcytn.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class RequestReader {
    private final BufferedReader bufferedReader;

    private String requestMethod;
    private String requestPath;
    private String httpVersion;
    private final Map<String, String> requestHeaders;

    public RequestReader (InputStream inputStream) {
        this.bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        this.requestHeaders = new HashMap<>();
    }

    public void read ()
    {
        String line;
        try
        {
            String[] statusLine = bufferedReader.readLine().split(" ");
            this.requestMethod = statusLine[0];
            this.requestPath = statusLine[1];
            this.httpVersion = statusLine[2];
            while (!(line = bufferedReader.readLine()).isEmpty())
            {
                String[] header = line.split(":" );
                requestHeaders.put(header[0].trim().toLowerCase(), header[1].trim().toLowerCase());
            }
        }
        catch (IOException e)
        {
            System.err.println("Error while reading request headers");
            System.err.println("Message: " + e.getMessage());
        }
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
}
