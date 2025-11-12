package com.gabcytn.server;

import com.gabcytn.App;
import com.gabcytn.exception.DuplicateUsernameException;
import com.gabcytn.http.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class ResponseHandler {
  private static final Logger LOG = LogManager.getLogger(ResponseHandler.class);
  private final RequestReader requestReader;
  private static final String FILES_DIR = "files/";

  public ResponseHandler(RequestReader requestReader) {
    this.requestReader = requestReader;
  }

  public Response handleEcho() {
    String[] paths = requestReader.getRequestPath().split("/");
    if (paths.length != 3) return responseWithoutBody(HttpStatus.NOT_FOUND);

    String stringedBody = paths[2];
    ResponseBuilder responseBuilder =
        new ResponseBuilder()
            .setHttpStatus(HttpStatus.OK)
            .setHeader("Content-Type", "text/plain")
            .setHeader(
                "Connection",
                requestReader.getRequestHeaders().getOrDefault("connection", "keep-alive"));

    String acceptEncoding = requestReader.getRequestHeaders().getOrDefault("accept-encoding", "");
    byte[] body;
    if (acceptEncoding.contains("gzip")) {
      GzipCompressor gzipCompressor = new GzipCompressor();
      body = gzipCompressor.compress(stringedBody);
      responseBuilder.setHeader("Content-Encoding", "gzip");
    } else body = stringedBody.getBytes(StandardCharsets.UTF_8);

    return responseBuilder
        .setHeader("Content-Length", Integer.toString(body.length))
        .setBody(body)
        .build();
  }

  public Response writeFile(String message) {
    String[] paths = requestReader.getRequestPath().split("/");
    if (paths.length != 3) return responseWithoutBody(HttpStatus.NOT_FOUND);
    String fileName = paths[2];
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILES_DIR + fileName))) {
      writer.write(message);
      return new ResponseBuilder()
          .setHttpStatus(HttpStatus.CREATED)
          .setHeader("Content-Length", "0")
          .setHeader(
              "Connection",
              requestReader.getRequestHeaders().getOrDefault("connection", "keep-alive"))
          .build();
    } catch (IOException e) {
      LOG.error("Error writing file: {}", e.getMessage());
      e.printStackTrace();
      return responseWithoutBody(HttpStatus.NOT_FOUND);
    }
  }

  public Response readFile() {
    String[] paths = requestReader.getRequestPath().split("/");
    if (paths.length != 3) return responseWithoutBody(HttpStatus.NOT_FOUND);

    String fileName = paths[2];
    String fileContent = readFile(fileName);
    if (fileContent == null) return responseWithoutBody(HttpStatus.NOT_FOUND);

    ResponseBuilder builder =
        new ResponseBuilder()
            .setHttpStatus(HttpStatus.OK)
            .setHeader(
                "Content-Type",
                requestReader.getRequestHeaders().getOrDefault("accept", "text/plain"))
            .setHeader(
                "Connection",
                requestReader.getRequestHeaders().getOrDefault("connection", "keep-alive"));

    byte[] body;
    if (requestReader.getRequestHeaders().getOrDefault("accept-encoding", "").contains("gzip")) {
      GzipCompressor gzipCompressor = new GzipCompressor();
      body = gzipCompressor.compress(fileContent);
      builder.setHeader("Content-Encoding", "gzip");
    } else body = fileContent.getBytes(StandardCharsets.UTF_8);

    return builder.setBody(body).setHeader("Content-Length", Integer.toString(body.length)).build();
  }

  private String readFile(String fileName) {
    try {
      File file = new File(FILES_DIR + fileName);
      if (!file.exists()) throw new FileNotFoundException(fileName + " not found");
      BufferedReader reader = new BufferedReader(new FileReader(file));
      StringBuilder stringBuilder = new StringBuilder();
      String line = reader.readLine();
      stringBuilder.append(line);
      while ((line = reader.readLine()) != null) {
        stringBuilder.append("\n").append(line);
      }
      reader.close();
      return stringBuilder.toString();
    } catch (IOException e) {
      LOG.error("File not found: {}", e.getMessage());
      e.printStackTrace();
      return null;
    }
  }

  public Response returnUserAgent() {
    String userAgent = requestReader.getRequestHeaders().get("user-agent");
    return responseWithBody(HttpStatus.OK, userAgent);
  }

  public Response registerUser() {
    try {
      String reqBody = requestReader.getBody();
      String[] json = reqBody.split(",");
      if (json.length != 2) throw new Exception("Incorrect JSON format");
      json[0] = json[0].trim().toLowerCase().replaceAll("[{},\", ,\t,\n]", "");
      json[1] = json[1].trim().toLowerCase().replaceAll("[{},\", ,\t,\n]", "");
      if (json[0].startsWith("username:") && json[1].startsWith("password:")) {
        String username = json[0].split(":")[1];
        String password = json[1].split(":")[1];
        boolean successful = App.createUser(username, password);
        if (!successful) throw new DuplicateUsernameException("User already exists");
        return responseWithoutBody(HttpStatus.CREATED);
      }
      throw new Exception("Incorrect JSON format");
    } catch (DuplicateUsernameException e) {
      return responseWithBody(HttpStatus.BAD_REQUEST, e.getMessage());
    } catch (Exception e) {
      LOG.error(e.getMessage());
      return responseWithoutBody(HttpStatus.BAD_REQUEST);
    }
  }

  public Response httpBasic() {
    String authorizationHeader =
        requestReader.getRequestHeaders().getOrDefault("authorization", "");
    if (authorizationHeader.isEmpty() || !authorizationHeader.startsWith("Basic ")) {
      return responseWithoutBody(HttpStatus.UNAUTHORIZED);
    }

    String encryptedVal = authorizationHeader.split(" ")[1];
    String decryptedVal = new String(Base64.getDecoder().decode(encryptedVal));
    String[] credentials = decryptedVal.split(":");
    String username = credentials[0];
    String password = credentials[1];

    String message = "HTTP Basic authentication successful!";

    if (!App.login(username, password)) return responseWithoutBody(HttpStatus.UNAUTHORIZED);

    return responseWithBody(HttpStatus.OK, message);
  }

  public Response responseWithoutBody(HttpStatus httpStatus) {
    return new ResponseBuilder()
        .setHttpStatus(httpStatus)
        .setHeader("Content-Length", "0")
        .setHeader(
            "Connection",
            requestReader.getRequestHeaders().getOrDefault("connection", "keep-alive"))
        .build();
  }

  public Response responseWithBody(HttpStatus httpStatus, String body) {
    return new ResponseBuilder()
        .setHttpStatus(httpStatus)
        .setHeader("Content-Length", Integer.toString(body.getBytes(StandardCharsets.UTF_8).length))
        .setHeader(
            "Connection",
            requestReader.getRequestHeaders().getOrDefault("connection", "keep-alive"))
        .setHeader(
            "Content-Type", requestReader.getRequestHeaders().getOrDefault("accept", "text/plain"))
        .setBody(body.getBytes(StandardCharsets.UTF_8))
        .build();
  }
}
