package com.gabcytn;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class RequestHandler implements  Runnable
{
    private static final String CRLF = "\r\n";

    private final Socket socket;
    private final BufferedReader reader;
    private final Map<String, String> requestHeaders = new HashMap<>();

    private String HTTP_VERB;
    private String REQUEST_PATH;
    private String HTTP_VERSION;

    public RequestHandler (Socket socket) throws IOException
    {
        this.socket = socket;
        this.socket.setSoTimeout(5000);
        this.socket.setReuseAddress(true);
        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    @Override
    public void run()
    {
        try
        {
            readStatusLine();
            readRequestHeaders();
            String response = HTTP_VERSION + " 200 OK" + CRLF +
                    "Content-Length: 0" + CRLF + "Connection: close" + CRLF + CRLF;
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

    private void readStatusLine()
    {
        try
        {
            String[] statusLine = reader.readLine().split(" ");
            this.HTTP_VERB = statusLine[0];
            this.REQUEST_PATH = statusLine[1];
            this.HTTP_VERSION = statusLine[2];
        }
        catch (IOException e)
        {
            System.err.println("IOException while reading status line: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void readRequestHeaders ()
    {
        String line;
        try
        {
            while (!(line = reader.readLine()).isEmpty())
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
}
