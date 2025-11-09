package com.gabcytn.server;

import com.gabcytn.http.HttpStatus;
import com.gabcytn.http.RequestReader;
import com.gabcytn.http.Response;
import java.io.*;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;

public class RequestHandler implements Runnable {
  private final Socket socket;
  private final OutputStream outputStream;
  private final RequestReader requestReader;
  private final ResponseHandler responseHandler;

  public RequestHandler(Socket socket) throws IOException {
    this.socket = socket;
    this.socket.setSoTimeout(5000);
    this.socket.setReuseAddress(true);
    this.outputStream = socket.getOutputStream();
    this.requestReader = new RequestReader(socket.getInputStream());
    this.responseHandler = new ResponseHandler(requestReader);
  }

  @Override
  public void run() {
    try {
      do {
        requestReader.read();
        // break if there's no request to read;
        if (!requestReader.getHasRequest()) {
          break;
        }
        Response response;
        if (!"HTTP/1.1".equals(requestReader.getHttpVersion())) {
          response = responseHandler.responseWithoutBody(HttpStatus.HTTP_VERSION_NOT_SUPPORTED);
        } else {
          response = processGetOrPostRequest();
        }

        writeResponseInOutputStream(response);
        clearRequestHeaders();
      } while (isKeepAlive());
    } catch (SocketTimeoutException e) {
      System.err.println("SOCKET TIMEOUT!!!");
    } finally {
      try {
        socket.close();
      } catch (IOException e) {
        System.err.println("Failed to close socket: " + e.getMessage());
      }
    }
  }

  private Response processGetOrPostRequest() {
    Response response;
    switch (requestReader.getRequestMethod()) {
      case "GET":
        response = new GetHandler(requestReader).processRequest();
        break;
      case "POST":
        response = new PostHandler(requestReader).processRequest();
        break;
      default:
        response = responseHandler.responseWithoutBody(HttpStatus.NOT_FOUND);
        break;
    }
    return response;
  }

  private void writeResponseInOutputStream(Response response) {
    try {
      outputStream.write(response.toString().getBytes(StandardCharsets.UTF_8));
      if (response.getBody().length != 0) outputStream.write(response.getBody());
    } catch (IOException e) {
      System.err.println("Error writing the response in output stream.");
      System.err.println("Message: " + e.getMessage());
    }
  }

  private boolean isKeepAlive() {
    return "keep-alive"
        .equals(requestReader.getRequestHeaders().getOrDefault("connection", "keep-alive"));
  }

  private void clearRequestHeaders() {
    requestReader.getRequestHeaders().clear();
  }
}
