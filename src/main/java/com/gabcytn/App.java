package com.gabcytn;

import com.gabcytn.server.RequestHandler;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class App {
  public static void main(String[] args) throws IOException {
    ServerSocket serverSocket = new ServerSocket(8080);
    ExecutorService executorService = Executors.newFixedThreadPool(10);
    System.out.println("Server is listening at port 8080...");
    while (true) {
      try {
        Socket socket = serverSocket.accept();
        Runnable requestHandler = new RequestHandler(socket);
        executorService.submit(requestHandler);
      } catch (IOException e) {
        System.err.println("IOException: " + e.getMessage());
      }
    }
  }
}
