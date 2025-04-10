# Java HTTP Server

A simple HTTP server built from scratch in Java using `ServerSocket` and `Socket`. This server supports basic HTTP methods, concurrency, and connection keep-alive. It is designed for educational purposes and serves as a foundation for future enhancements.

## Features

- **GET / HTTP/1.1**: Returns `200 OK` with no body.
- **GET /echo/{str}**: Returns the string `{str}` as the response body.
- **POST /file/{file_name}**: Writes the body of the POST request to a file in the `/files/` directory. Returns `201 CREATED`.
- **GET /file/{file_name}**: Reads the contents of the specified file and returns it as the response body with `200 OK`.
- **Concurrency**: Supports multiple concurrent connections using `ExecutorService` with a fixed thread pool of 10 threads.
- **Keep-Alive**: Connections are kept alive for 5 seconds before timing out.
- **404 Not Found**: All other requests return a `404 Not Found` status.

## Future Features

- GZIP compression support.
- HTTP Basic Authentication.

## How to Run

1. Clone the repository:
   ```bash
   git clone https://github.com/gabcytn/http-server.git
   cd http-server
2. Run the server in IntelliJ
3. The server will start on port 8080. You can test it with curl or Postman.
