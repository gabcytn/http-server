package com.gabcytn;

import com.gabcytn.server.RequestHandler;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {
  private static final Map<String, String> USERS = new HashMap<>();
  private static final Logger LOG = LoggerFactory.getLogger(App.class);

  public static void main(String[] args) throws IOException {
    ServerSocket serverSocket = new ServerSocket(8080);
    ExecutorService executorService = Executors.newFixedThreadPool(10);
    LOG.info("Server is listening at port 8080...");
    while (true) {
      try {
        var socket = serverSocket.accept();
        Runnable requestHandler = new RequestHandler(socket);
        executorService.submit(requestHandler);
      } catch (IOException e) {
        LOG.error("IOException: {}", e.getMessage());
      }
    }
  }

  public static synchronized Boolean createUser(String username, String password) {
    if (USERS.containsKey(username)) return false;

    USERS.put(username, password);
    return true;
  }

  public static synchronized Boolean login(String username, String password) {
    return password.equals(USERS.getOrDefault(username, ""));
  }
}
