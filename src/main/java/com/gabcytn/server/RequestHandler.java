package com.gabcytn.server;

import com.gabcytn.http.HttpStatus;
import com.gabcytn.http.RequestReader;
import com.gabcytn.http.Response;
import com.gabcytn.http.ResponseBuilder;

import java.io.*;
import java.net.Socket;
import java.net.SocketTimeoutException;
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
            boolean keepAlive;
            do
            {
                // clear previous request's headers
                requestReader.getRequestHeaders().clear();
                requestReader.read();
                if (!requestReader.getHasRequest())
                    break;
                Response response;
                if (!"HTTP/1.1".equals(requestReader.getHttpVersion()))
                    response = responseHandler.generate404();
                else if (requestReader.getRequestPath().startsWith("/echo/") && "GET".equals(requestReader.getRequestMethod()))
                    response = responseHandler.handleEcho();
                else if (requestReader.getRequestPath().startsWith("/file/"))
                {
                    switch (requestReader.getRequestMethod()) {
                        case "GET":
                            response = responseHandler.readFile();
                            break;
                        case "POST":
                            response = responseHandler.writeFile(requestReader.getBody());
                            break;
                        default:
                            response = responseHandler.generate404();
                    }
                }
                else if ("/user-agent".equals(requestReader.getRequestPath()) && "GET".equals(requestReader.getRequestMethod()))
                    response = responseHandler.returnUserAgent();
                else if ("/".equals(requestReader.getRequestPath()) && "GET".equals(requestReader.getRequestMethod()))
                    response = responseHandler.generate200WithoutBody();
                else
                    response = responseHandler.generate404();
                socket.getOutputStream().write(response.toString().getBytes(StandardCharsets.UTF_8));
                keepAlive = "keep-alive".equals(requestReader.getRequestHeaders()
                                .getOrDefault("connection", "keep-alive"));
            } while (keepAlive);
        }
        catch (SocketTimeoutException e)
        {
            System.err.println("SOCKET TIMEOUT!!!");
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
                System.err.println("Failed to close socket: " + e.getMessage());
            }
        }
    }
}