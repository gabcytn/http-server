package com.gabcytn.server;

import com.gabcytn.http.*;

import java.io.*;
import java.nio.charset.StandardCharsets;


public class ResponseHandler {
    private final RequestReader requestReader;
    private static final String FILES_DIR = "files/";

    public ResponseHandler (RequestReader requestReader) {
        this.requestReader = requestReader;
    }

    public Response handleEcho ()
    {
        String[] paths = requestReader.getRequestPath().split("/");
        if (paths.length != 3)
            return generate404();

        String stringedBody = paths[2];
        ResponseBuilder responseBuilder = new ResponseBuilder()
                .setHttpStatus(HttpStatus.OK)
                .setHeader("Content-Type", "text/plain")
                .setHeader("Connection", requestReader.getRequestHeaders().getOrDefault("connection", "keep-alive"));

        String acceptEncoding = requestReader.getRequestHeaders().getOrDefault("accept-encoding", "");
        byte[] body;
        if (acceptEncoding.contains("gzip"))
        {
            GzipCompressor gzipCompressor = new GzipCompressor();
            body = gzipCompressor.compress(stringedBody);
            responseBuilder.setHeader("Content-Encoding", "gzip");
        }
        else
            body = stringedBody.getBytes(StandardCharsets.UTF_8);

        return responseBuilder.setHeader("Content-Length", Integer.toString(body.length))
                .setBody(body)
                .build();
    }

    public Response writeFile (String message)
    {
        String[] paths = requestReader.getRequestPath().split("/");
        if (paths.length != 3)
            return generate404();
        String fileName = paths[2];
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILES_DIR + fileName)))
        {
            writer.write(message);
            return new ResponseBuilder()
                    .setHttpStatus(HttpStatus.CREATED)
                    .setHeader("Content-Length", "0")
                    .setHeader("Connection", requestReader.getRequestHeaders().getOrDefault("connection", "keep-alive"))
                    .build();
        }
        catch (IOException e)
        {
            System.err.println("Error writing file: " + e.getMessage());
            e.printStackTrace();
            return generate404();
        }
    }

    public Response readFile()
    {
        String[] paths = requestReader.getRequestPath().split("/");
        if (paths.length != 3)
            return generate404();

        String fileName = paths[2];
        String fileContent = readFile(fileName);
        if (fileContent == null)
            return generate404();

        ResponseBuilder builder = new ResponseBuilder()
                .setHttpStatus(HttpStatus.OK)
                .setHeader("Content-Type", "text/plain")
                .setHeader("Connection", requestReader.getRequestHeaders().getOrDefault("connection", "keep-alive"));

        byte[] body;
        if (requestReader.getRequestHeaders().getOrDefault("accept-encoding", "").contains("gzip"))
        {
            GzipCompressor gzipCompressor = new GzipCompressor();
            body = gzipCompressor.compress(fileContent);
            builder.setHeader("Content-Encoding", "gzip");
        }
        else
            body = fileContent.getBytes(StandardCharsets.UTF_8);

        return builder
                .setBody(body)
                .setHeader("Content-Length", Integer.toString(body.length))
                .build();
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

    public Response returnUserAgent ()
    {
        String userAgent = requestReader.getRequestHeaders().get("user-agent");
        return new ResponseBuilder()
                .setHttpStatus(HttpStatus.OK)
                .setHeader("Connection", requestReader.getRequestHeaders().getOrDefault("connection", "keep-alive"))
                .setHeader("Content-Type", "text/plain")
                .setHeader("Content-Length", Integer.toString(userAgent.getBytes(StandardCharsets.UTF_8).length))
                .setBody(userAgent.getBytes(StandardCharsets.UTF_8))
                .build();
    }

    public Response generate404 ()
    {
        return new ResponseBuilder()
                .setHttpStatus(HttpStatus.NOT_FOUND)
                .setHeader("Content-Length", "0")
                .setHeader("Connection", requestReader.getRequestHeaders().getOrDefault("connection", "keep-alive"))
                .build();
    }

    public Response generate200WithoutBody ()
    {
        return new ResponseBuilder()
                .setHttpStatus(HttpStatus.OK)
                .setHeader("Content-Length", "0")
                .setHeader("Connection", requestReader.getRequestHeaders().getOrDefault("connection", "keep-alive"))
                .build();
    }
}
