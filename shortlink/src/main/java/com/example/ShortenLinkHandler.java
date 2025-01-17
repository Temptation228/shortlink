package com.example;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Random;

public class ShortenLinkHandler implements HttpHandler {
    private final DatabaseManager databaseManager;

    public ShortenLinkHandler(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("POST".equals(exchange.getRequestMethod())) {
            String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            JsonObject json = JsonParser.parseString(requestBody).getAsJsonObject();
            
            String uuid = json.get("body").getAsJsonObject().get("uuid").getAsString();
            String originalUrl = json.get("body").getAsJsonObject().get("url").getAsString();
            Integer maxClicks = json.get("body").getAsJsonObject().has("maxClicks")
                    ? json.get("body").getAsJsonObject().get("maxClicks").getAsInt()
                    : null;

            long expirationTime = json.get("body").getAsJsonObject().has("expirationTime")
                    ? json.get("body").getAsJsonObject().get("expirationTime").getAsLong()
                    : 60;

            String shortUrl = generateShortUrl();

            databaseManager.saveLink(uuid, originalUrl, shortUrl, maxClicks, expirationTime);

            JsonObject responseJson = new JsonObject();
            responseJson.addProperty("shortUrl", "http://localhost:8000/" + shortUrl);

            sendResponse(exchange, responseJson.toString(), 201);
        } else {
            sendResponse(exchange, "{\"message\": \"Method not allowed\"}", 405);
        }
    }

    private String generateShortUrl() {
        String alphabet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder shortUrl = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            shortUrl.append(alphabet.charAt(random.nextInt(alphabet.length())));
        }
        return shortUrl.toString();
    }

    private void sendResponse(HttpExchange exchange, String response, int statusCode) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, response.getBytes(StandardCharsets.UTF_8).length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes(StandardCharsets.UTF_8));
        os.close();
    }
}
