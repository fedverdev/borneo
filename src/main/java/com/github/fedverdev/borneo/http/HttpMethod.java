package com.github.fedverdev.borneo.http;

public enum HttpMethod {
    GET, POST, PUT, DELETE;

    public static HttpMethod fromString(String string) {
        return valueOf(string.toUpperCase());
    }
}
