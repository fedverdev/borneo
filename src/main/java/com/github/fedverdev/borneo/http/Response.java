package com.github.fedverdev.borneo.http;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

public class Response {
    private HttpStatus status;
    private String body;
    private Map<String, String> headers;
    private OutputStream outputStream;

    public Response(OutputStream outputStream, HttpStatus status, String body, Map<String, String> headers) {
        this.outputStream = outputStream;
        this.status = status;
        this.body = body;
        this.headers = headers;
    }

    public void send() throws IOException {
        StringBuilder formResponse = new StringBuilder();
        formResponse.append(String.format("HTTP/1.1 %s %s\r\n", this.status.getCode(), this.status.getMessage()));
        headers.forEach((key, value) -> formResponse.append(String.format("%s: %s\r\n", key, value)));
        formResponse.append(String.format("Content-Length: %d\r\n\r\n", this.body.length()));
        formResponse.append(body);
        outputStream.write(formResponse.toString().getBytes());
        outputStream.flush();
    }

    public void setBody(String body) { this.body = body; }
    public void setStatus(HttpStatus status) { this.status = status; }

    public HttpStatus getStatus() { return status; }
    public String getBody() { return body; }
    public Map<String, String> getHeaders() { return headers; }
    public OutputStream getOutputStream() { return outputStream; }
}
