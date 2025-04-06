package com.gabcytn;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class App
{
    final static String HTTP_VERSION = "HTTP/1.1";
    final static String CRLF = "\r\n\r\n";

    final static Map<String, String> requestHeaders = new HashMap<>();

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

                String[] statusLine = bufferedReader.readLine().split(" ");
                String httpVerb = statusLine[0];
                String requestPath = statusLine[1];

                System.out.println("HTTP Verb: " + httpVerb);
                System.out.println("Request path: " + requestPath);

                readRequestHeaders(bufferedReader);

                System.out.println("---------HEADERS---------");
                for (Map.Entry<String, String> headers : requestHeaders.entrySet())
                    System.out.println(headers.getKey() + ": " + headers.getValue());
                System.out.println("---------HEADERS---------");

                String response = HTTP_VERSION + " 200 OK" + CRLF + CRLF;
                socket.getOutputStream()
                        .write(response.getBytes(StandardCharsets.UTF_8));
            }
            catch (IOException e)
            {
                System.err.println("IOException: " + e.getMessage());
            }
        }
    }

    private static void readRequestHeaders (BufferedReader reader)
    {
        String line;
        try
        {
            while (!(line = reader.readLine()).isEmpty())
            {
                String[] header = line.split(":");
                requestHeaders.put(header[0].trim().toLowerCase(), header[1].trim().toLowerCase());
            }
        }
        catch (IOException e)
        {
            System.err.println("Error while reading request headers");
            System.err.println("Message: " + e.getMessage());
        }
    }
}
