package com.gabcytn.server;

import com.gabcytn.http.RequestReader;
import com.gabcytn.http.ResponseBuilder;

import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class RequestHandler implements  Runnable
{
    private final Socket socket;
    private final RequestReader requestReader;

    public RequestHandler (Socket socket) throws IOException
    {
        this.socket = socket;
        this.socket.setSoTimeout(5000);
        this.socket.setReuseAddress(true);
        this.requestReader = new RequestReader(socket.getInputStream());
    }

    @Override
    public void run()
    {
        try
        {
            requestReader.read();
            String response;
            if (requestReader.getRequestPath().startsWith("/echo"))
                response = handleEcho(requestReader);
            else if ("/".equals(requestReader.getRequestPath())) {
                response = new ResponseBuilder()
                        .setHttpVersion(requestReader.getHttpVersion())
                        .setStatusCode(200)
                        .setStatus("OK")
                        .setHeader("Content-Length", "0")
                        .setHeader("Connection", "close")
                        .build()
                            .toString();
            }
            else
                response = generate404();
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

    private String handleEcho (RequestReader requestReader)
    {
        String[] paths = requestReader.getRequestPath().split("/");
        if (paths.length != 3)
            return generate404();

        String path = paths[2];
        return new ResponseBuilder()
                .setHttpVersion(requestReader.getHttpVersion())
                .setStatusCode(200)
                .setStatus("OK")
                .setHeader("Content-Length", Integer.toString(path.length()))
                .setHeader("Connection", "close")
                .setBody(path)
                .build()
                    .toString();
    }

    private String generate404 ()
    {
        return new ResponseBuilder()
                .setHttpVersion(requestReader.getHttpVersion())
                .setStatusCode(404)
                .setStatus("Not Found")
                .setHeader("Content-Length", "0")
                .setHeader("Connection", "close")
                .build()
                    .toString();
    }
}