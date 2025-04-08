package com.gabcytn.server;

import com.gabcytn.http.RequestReader;
import com.gabcytn.http.ResponseBuilder;

import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class RequestHandler implements  Runnable
{
    private final Socket socket;

    public RequestHandler (Socket socket) throws IOException
    {
        this.socket = socket;
        this.socket.setSoTimeout(5000);
        this.socket.setReuseAddress(true);
    }

    @Override
    public void run()
    {
        try
        {
            RequestReader requestReader = new RequestReader(socket.getInputStream());
            requestReader.read();
            Map<String, String> requestHeaders = requestReader.getRequestHeaders();
            String response = new ResponseBuilder()
                    .setHttpVersion(requestReader.getHttpVersion())
                    .setStatusCode(200)
                    .setStatus("OK")
                    .setHeader("Content-Length", "5")
                    .setHeader("Connection", "close")
                    .setBody("12345")
                    .build()
                            .toString();
            socket.getOutputStream().write(response.getBytes(StandardCharsets.UTF_8));
            socket.close();
        }
        catch (IOException e)
        {
            System.err.println("IOException in run() method: " + e.getMessage());
        }
        finally
        {
            try {
                socket.close();
            } catch (IOException e) {
                System.err.println("Error closing socket connection");
            }
        }
    }
}
