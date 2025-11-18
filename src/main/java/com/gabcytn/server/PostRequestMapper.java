package com.gabcytn.server;

import com.gabcytn.http.HttpStatus;
import com.gabcytn.http.RequestReader;
import com.gabcytn.http.Response;

public class PostRequestMapper {
  private final RequestReader requestReader;
  private final ResponseHandler responseHandler;

  public PostRequestMapper(RequestReader requestReader) {
    this.requestReader = requestReader;
    this.responseHandler = new ResponseHandler(requestReader);
  }

  public Response processRequest() {
    if (!"POST".equals(requestReader.getRequestMethod())) {
      throw new Error("This is not a POST request");
    } else if (requestReader.getRequestPath().startsWith("/file/")) {
      return responseHandler.writeFile(requestReader.getBody());
    } else if ("/register".equals(requestReader.getRequestPath())) {
      return responseHandler.registerUser();
    } else {
      return responseHandler.responseWithoutBody(HttpStatus.NOT_FOUND);
    }
  }
}
