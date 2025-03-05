package com.github.fedverdev.borneo.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class Request {
    private final HttpMethod method;
    private final String path;
    private final Map<String, String> headers = new HashMap<String, String>();
    private final StringBuilder body = new StringBuilder();

    public Request(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line = reader.readLine();
        if (line == null) { throw new IOException("Empty request"); }
        String[] parts = line.split(" ");
        try {
            method = HttpMethod.fromString(parts[0]);
        } catch (Exception e) {
            throw new IOException("Unsupported http method: " + parts[0]);
        }
        path = parts[1];
        while (!(line = reader.readLine()).isEmpty()) {
            parts = line.split(": ");
            headers.put(parts[0], parts[1]);
        }
        if (headers.containsKey("Content-Length")) {
            int contentLength = Integer.parseInt(headers.get("Content-Length"));
            char[] buffer = new char[contentLength];
            reader.read(buffer);
            body.append(buffer);
        }
    }

    public String getPath() { return path; }
    public HttpMethod getMethod() { return method; }
    public Map<String, String> getHeaders() { return headers; }
    public String getBody() { return body.toString(); }
}
