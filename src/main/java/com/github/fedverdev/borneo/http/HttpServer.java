package com.github.fedverdev.borneo.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class HttpServer implements Server{
    private final int port;
    private Map<String, Map<HttpMethod, Handler>> handlers = new HashMap<>();

    public HttpServer(int port) {
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    @Override
    public void addHandler(String path, HttpMethod httpMethod, Handler handler) {
        if (handlers.get(path) == null) {
            handlers.put(path, new HashMap<>());
        }
        handlers.get(path).put(httpMethod, handler);
    }

    @Override
    public void start() throws IOException {
        ServerSocket serverSocket = new ServerSocket(this.port);
        while(true) {
            Socket socket = serverSocket.accept();

            InputStream inputStream = socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream();
            Request request = new Request(inputStream);
            Response response = new Response(outputStream, HttpStatus.OK, "", new HashMap<String, String>());
            Map<HttpMethod, Handler> handlersMap = handlers.get(request.getPath());
            if (handlersMap == null) {
                response.setStatus(HttpStatus.NOT_FOUND);
                response.setBody("Not found");
            } else {
                if (handlersMap.get(request.getMethod()) == null) {
                    response.setStatus(HttpStatus.METHOD_NOT_ALLOWED);
                    response.setBody("Method not allowed");
                } else {
                    handlersMap.get(request.getMethod()).handle(request, response);
                }
            }
            response.send();

            socket.close();
        }
    }

    @FunctionalInterface
    public interface Handler {
        void handle(Request request, Response response);
    }
}
