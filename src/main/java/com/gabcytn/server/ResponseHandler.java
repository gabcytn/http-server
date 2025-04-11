package com.gabcytn.server;

import com.gabcytn.http.RequestReader;
import com.gabcytn.http.ResponseBuilder;

import java.io.*;
import java.nio.charset.StandardCharsets;


public class ResponseHandler {
    private final RequestReader requestReader;
    private static final String FILES_DIR = "files/";

    public ResponseHandler (RequestReader requestReader) {
        this.requestReader = requestReader;
    }

    public String handleEcho ()
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
                .setHeader("Content-Length", Integer.toString(path.getBytes(StandardCharsets.UTF_8).length))
                .setHeader("Connection", requestReader.getRequestHeaders().getOrDefault("connection", "keep-alive"))
                .setBody(path)
                .build()
                .toString();
    }

    public String writeFile (String message)
    {
        String[] paths = requestReader.getRequestPath().split("/");
        if (paths.length != 3)
            return generate404();
        String fileName = paths[2];
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILES_DIR + fileName)))
        {
            writer.write(message);
            return new ResponseBuilder()
                    .setHttpVersion(requestReader.getHttpVersion())
                    .setStatusCode(201)
                    .setStatus("Created")
                    .setHeader("Content-Length", "0")
                    .setHeader("Connection", requestReader.getRequestHeaders().getOrDefault("connection", "keep-alive"))
                    .build()
                        .toString();
        }
        catch (IOException e)
        {
            System.err.println("Error writing file: " + e.getMessage());
            e.printStackTrace();
            return generate404();
        }
    }

    public String readFile()
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
                .setHeader("Content-Length", Integer.toString(fileContent.getBytes(StandardCharsets.UTF_8).length))
                .setHeader("Connection", requestReader.getRequestHeaders().getOrDefault("connection", "keep-alive"))
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

    public String returnUserAgent ()
    {
        String userAgent = requestReader.getRequestHeaders().get("user-agent");
        return new ResponseBuilder()
                .setHttpVersion(requestReader.getHttpVersion())
                .setStatusCode(200)
                .setStatus("OK")
                .setHeader("Connection", requestReader.getRequestHeaders().getOrDefault("connection", "keep-alive"))
                .setHeader("Content-Length", Integer.toString(userAgent.getBytes(StandardCharsets.UTF_8).length))
                .setBody(userAgent)
                .build()
                    .toString();
    }

    public String generate404 ()
    {
        return new ResponseBuilder()
                .setHttpVersion(requestReader.getHttpVersion())
                .setStatusCode(404)
                .setStatus("Not Found")
                .setHeader("Content-Length", "0")
                .setHeader("Connection", requestReader.getRequestHeaders().getOrDefault("connection", "keep-alive"))
                .build()
                .toString();
    }

    public String generate200WithoutBody ()
    {

        return new ResponseBuilder()
                .setHttpVersion(requestReader.getHttpVersion())
                .setStatusCode(200)
                .setStatus("OK")
                .setHeader("Content-Length", "0")
                .setHeader("Connection", requestReader.getRequestHeaders().getOrDefault("connection", "keep-alive"))
                .build()
                    .toString();
    }
}
