package com.gabcytn.server;

import com.gabcytn.http.RequestReader;
import com.gabcytn.http.ResponseBuilder;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class RequestHandler implements  Runnable
{
    private final Socket socket;
    private final RequestReader requestReader;
    private final ResponseHandler responseHandler;

    public RequestHandler (Socket socket) throws IOException
    {
        this.socket = socket;
        this.socket.setSoTimeout(5000);
        this.socket.setReuseAddress(true);
        this.requestReader = new RequestReader(socket.getInputStream());
        this.responseHandler = new ResponseHandler(requestReader);
    }

    @Override
    public void run()
    {
        try
        {
            requestReader.read();
            String response;
            if (requestReader.getRequestPath().startsWith("/echo/"))
                response = responseHandler.handleEcho();
            else if (requestReader.getRequestPath().startsWith("/file/"))
                response = responseHandler.readFile();
            else if ("/".equals(requestReader.getRequestPath()))
                response = responseHandler.generate200WithoutBody();
            else
                response = responseHandler.generate404();
            socket.getOutputStream().write(response.getBytes(StandardCharsets.UTF_8));
            socket.close();
        }
        catch (IOException e)
        {
            System.err.println("IOException in run() method: " + e.getMessage());
        }
    }
}