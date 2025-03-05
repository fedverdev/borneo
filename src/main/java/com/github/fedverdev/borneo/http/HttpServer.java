package com.github.fedverdev.borneo.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private static Logger logger = LoggerFactory.getLogger(HttpServer.class);

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
        logger.warn("Please note that you are using the Borneo server, which was created exclusively for studying the topic!");
        ServerSocket serverSocket = new ServerSocket(this.port);
        logger.info("\n" +
                "██████╗░░█████╗░██████╗░███╗░░██╗███████╗░█████╗░\n" +
                "██╔══██╗██╔══██╗██╔══██╗████╗░██║██╔════╝██╔══██╗\n" +
                "██████╦╝██║░░██║██████╔╝██╔██╗██║█████╗░░██║░░██║\n" +
                "██╔══██╗██║░░██║██╔══██╗██║╚████║██╔══╝░░██║░░██║\n" +
                "██████╦╝╚█████╔╝██║░░██║██║░╚███║███████╗╚█████╔╝\n" +
                "╚═════╝░░╚════╝░╚═╝░░╚═╝╚═╝░░╚══╝╚══════╝░╚════╝░");

        logger.info("HTTP Server started on port {}", this.port);
        startServer(serverSocket);
    }

    public void startServer(ServerSocket serverSocket) {
        while(true) {
            try {
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
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        }

    }

    @FunctionalInterface
    public interface Handler {
        void handle(Request request, Response response);
    }
}
