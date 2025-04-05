package com.gabcytn;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Date;

public class App
{
    public static void main( String[] args ) throws IOException
    {
        ServerSocket serverSocket = new ServerSocket(8080);
        System.out.println("Server is listening at port 8080...");
        while (true)
        {
            try (Socket socket = serverSocket.accept())
            {
                BufferedReader bufferedReader =
                        new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String line = bufferedReader.readLine();
                while (!line.isEmpty())
                {
                    System.out.println(line);
                    line = bufferedReader.readLine();
                }
                Date date = new Date();
                String response = "HTTP/1.1 200 OK\r\n\r\n" + date;
                socket.getOutputStream()
                        .write(response.getBytes(StandardCharsets.UTF_8));
            }
            catch (IOException e)
            {
                System.err.println("IOException: " + e.getMessage());
            }
        }
    }
}
