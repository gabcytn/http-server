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
    private static final String FILES_DIR = "files/";

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
            if (requestReader.getRequestPath().startsWith("/echo/"))
                response = handleEcho();
            else if (requestReader.getRequestPath().startsWith("/file/"))
                response = readFile();
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
    }

    private String handleEcho ()
    {
        String[] paths = requestReader.getRequestPath().split("/");
        if (paths.length != 3)
            return generate404();

        String path = paths[2];
        return new ResponseBuilder()
                .setHttpVersion(requestReader.getHttpVersion())
                .setStatusCode(200)
                .setStatus("OK")
                .setHeader("Content-Type", "text/plain")
                .setHeader("Content-Length", Integer.toString(path.length()))
                .setHeader("Connection", "close")
                .setBody(path)
                .build()
                    .toString();
    }

    private String readFile()
    {
        String[] paths = requestReader.getRequestPath().split("/");
        if (paths.length != 3)
            return generate404();

        String fileName = paths[2];
        String fileContent = readFile(fileName);
        if (fileContent == null)
            return generate404();
        return new ResponseBuilder()
                .setHttpVersion(requestReader.getHttpVersion())
                .setStatusCode(200)
                .setStatus("OK")
                .setHeader("Content-Type", "text/plain")
                .setHeader("Content-Length", Integer.toString(fileContent.length()))
                .setHeader("Connection", "close")
                .setBody(fileContent)
                .build()
                    .toString();
    }

    private String readFile (String fileName)
    {
        try
        {
            File file = new File(FILES_DIR + fileName);
            if (!file.exists())
                throw new FileNotFoundException(fileName + " not found");
            BufferedReader reader = new BufferedReader(new FileReader(file));
            StringBuilder stringBuilder = new StringBuilder();
            String line = reader.readLine();
            stringBuilder.append(line);
            while ((line = reader.readLine()) != null)
                stringBuilder.append("\n").append(line);
            return stringBuilder.toString();
        }
        catch (IOException e)
        {
            System.err.println("File not found: " + e.getMessage());
            e.printStackTrace();
            return null;
        }

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