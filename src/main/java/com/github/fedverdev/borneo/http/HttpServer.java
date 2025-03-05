package com.github.fedverdev.borneo.http;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
    private static Logger logger = LogManager.getLogger(HttpServer.class);

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
        logger.info("\n" +
                "██████╗░░█████╗░██████╗░███╗░░██╗███████╗░█████╗░\n" +
                "██╔══██╗██╔══██╗██╔══██╗████╗░██║██╔════╝██╔══██╗\n" +
                "██████╦╝██║░░██║██████╔╝██╔██╗██║█████╗░░██║░░██║\n" +
                "██╔══██╗██║░░██║██╔══██╗██║╚████║██╔══╝░░██║░░██║\n" +
                "██████╦╝╚█████╔╝██║░░██║██║░╚███║███████╗╚█████╔╝\n" +
                "╚═════╝░░╚════╝░╚═╝░░╚═╝╚═╝░░╚══╝╚══════╝░╚════╝░");

        logger.info("HTTP Server started on port " + this.port);
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
            logger.info(request.getMethod() + " " + request.getPath() + " -> " + response.getStatus());
            response.send();

            socket.close();
        }
    }

    @FunctionalInterface
    public interface Handler {
        void handle(Request request, Response response);
    }
}
