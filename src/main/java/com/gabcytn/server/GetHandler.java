package com.gabcytn.server;

import com.gabcytn.http.HttpStatus;
import com.gabcytn.http.RequestReader;
import com.gabcytn.http.Response;

public class GetHandler {
  private final RequestReader requestReader;
  private final ResponseHandler responseHandler;

  public GetHandler(RequestReader requestReader) {
    this.requestReader = requestReader;
    this.responseHandler = new ResponseHandler(requestReader);
  }

  public Response processRequest() {
    if (!"GET".equals(requestReader.getRequestMethod())) {
      throw new Error("This is not a GET request!");
    }

    String requestPath = requestReader.getRequestPath();

    if (requestPath.startsWith("/echo/")) {
      return responseHandler.handleEcho();
    } else if (requestPath.startsWith("/file/")) {
      return responseHandler.readFile();
    } else if ("/user-agent".equals(requestPath)) {
      return responseHandler.returnUserAgent();
    } else if ("/".equals(requestPath)) {
      return responseHandler.responseWithoutBody(HttpStatus.OK);
    } else {
      return responseHandler.responseWithoutBody(HttpStatus.NOT_FOUND);
    }
  }
}
