package com.example.kvserver;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {
    private final String server;
    private final String apiKey;

    public KVTaskClient(String server) {
        this.server = server;
        this.apiKey = register(server);
    }

    private String register(String server) {
        try {
            final HttpClient client = HttpClient.newHttpClient();
            final HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(server + "register"))
                    .GET()
                    .build();
            final HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new RuntimeException("");
            }
            return response.body();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException();
        }
    }

    public String load(String key) {
        try {
            final HttpClient client = HttpClient.newHttpClient();
            final HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(server + "load/" + key + "?API_TOKEN=" + this.apiKey))
                    .GET()
                    .build();
            final HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new RuntimeException("");
            }
            return response.body();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException();
        }
    }

    public void put(String key, String value) {
        try {
            final HttpClient client = HttpClient.newHttpClient();
            HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(value);
            final HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(server + "save/" + key + "?API_TOKEN=" + this.apiKey))
                    .POST(body)
                    .build();
            final HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new RuntimeException("");
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException();
        }
    }
}
