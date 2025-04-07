package com.gabcytn;

import com.gabcytn.server.RequestHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class App
{
    public static void main( String[] args ) throws IOException
    {
        ServerSocket serverSocket = new ServerSocket(8080);
        System.out.println("Server is listening at port 8080...");
        while (true)
        {
            try
            {
                Socket socket = serverSocket.accept();
                Runnable requestHandler = new RequestHandler(socket);
                Thread thread = new Thread(requestHandler);
                thread.start();
            }
            catch (IOException e)
            {
                System.err.println("IOException: " + e.getMessage());
            }
        }
    }
}
