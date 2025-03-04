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
    private Map<String, Handler> handlers = new HashMap<>();

    public HttpServer(int port) {
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    @Override
    public void addHandler(String path, Handler handler) {
        handlers.put(path, handler);
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
            Handler handler = handlers.get(request.getPath());
            if (handler == null) {
                response.setStatus(HttpStatus.NOT_FOUND);
                response.setBody("Not found");
            } else {
                handler.handle(request, response);
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
