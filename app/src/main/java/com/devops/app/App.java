package com.devops.app;

import com.sun.net.httpserver.HttpServer;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class App {
    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(
            new InetSocketAddress("0.0.0.0", 8080), 0
        );

        server.createContext("/", exchange -> {
            String html = """
                <html>
                <head><title>DevOps Project 06</title></head>
                <body style="font-family: Arial; text-align: center; margin-top: 100px;">
                    <h1>Hello from DevOps Project 06!</h1>
                    <h2>Java Application is Running on Kubernetes!</h2>
                </body>
                </html>
                """;

            exchange.getResponseHeaders().set(
                "Content-Type", "text/html; charset=UTF-8"
            );

            byte[] response = html.getBytes("UTF-8");
            exchange.sendResponseHeaders(200, response.length);

            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response);
            }
        });

        server.start();
        System.out.println("Server started on port 8080");
    }
}
