# Java HTTP Server

A simple HTTP/1.1 server built from scratch in Java using `ServerSocket` and `Socket`. This server supports basic HTTP methods, concurrency, and connection keep-alive. It is designed for educational purposes and serves as a foundation for future enhancements.

## Features

- **Concurrency**: Supports multiple concurrent connections using `ExecutorService` with a fixed thread pool of 10 threads.
- **Keep-Alive**: Connections are kept alive for 5 seconds before timing out.
- **GET / HTTP/1.1**: Returns `200 OK` with no body.
- **GET /user-agent**: Returns the user-agent of the client.
- **GET /echo/{str}**: Returns the string `{str}` as the response body.
   - **Accept-Encoding: gzip** request header uses gzip to compress `{str}`.
      - returns **Content-Encoding: gzip** in response header.
- **POST /file/{file_name}**: Writes the body of the POST request to a file in the `/files/` directory
  - returns `201 CREATED`.
- **GET /file/{file_name}**: Reads the contents of the specified file and returns it as the response body with `200 OK`.
   - also supports **gzip**.
   - returns `404 Not Found` when file doesn't exist.
- **POST /register**: Requires a username and password in JSON body to "register" this user.
  - returns `201 Created` if user is successfully registered.
  - returns `400 Bad Request` if username is already taken or if JSON format is incorrect.
- **GET /basic**: Requires an Authorization of Basic scheme (username and password encoded in Base64).
  - returns `200 OK` if authentication is successful.
  - returns `401 Unauthorized` if not.
- `505 HTTP Version Not Supported`: If the HTTP version is not `HTTP/1.1`.
- `404 Not Found`: All other requests return a `404 Not Found` status.

## Future Features

- SSL/TLS

## How to Run

1. Clone the repository:
   ```bash
   git clone https://github.com/gabcytn/http-server.git
   cd http-server
2. Compile and run
   ```bash
   mvn compile
   java -cp ./target/classes com.gabcytn.App
4. The server will start on port 8080. Test it with cURL or Postman.

---
Inspired from: [CodeCrafters](https://codecrafters.io/)
