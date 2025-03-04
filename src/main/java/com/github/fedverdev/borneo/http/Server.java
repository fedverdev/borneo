package com.github.fedverdev.borneo.http;

import java.io.IOException;

public interface Server {
    void start() throws IOException;
    void addHandler(String path, HttpServer.Handler handler);
}
