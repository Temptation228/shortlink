package com.example;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class RetrieveLinksHandler implements HttpHandler {
    private final DatabaseManager databaseManager;

    public RetrieveLinksHandler(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("POST".equals(exchange.getRequestMethod())) {
            String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            JsonObject json = JsonParser.parseString(requestBody).getAsJsonObject();

            String uuid = json.get("uuid").getAsString();

            List<Link> links = databaseManager.getAllLinksByUUID(uuid);

            JsonArray linksArray = new JsonArray();

            for (Link link : links) {
                JsonObject linkJson = new JsonObject();
                linkJson.addProperty("id", link.getId());
                linkJson.addProperty("originalUrl", link.getOriginalUrl());
                linkJson.addProperty("shortUrl", link.getShortUrl());
                linkJson.addProperty("maxClicks", link.getMaxClicks());
                linkJson.addProperty("clicks", link.getClicks());
                linkJson.addProperty("expirationTime", link.getExpirationTime());
                linkJson.addProperty("createdAt", link.getCreatedAt());

                if ((link.getMaxClicks() != null && link.getClicks() >= link.getMaxClicks()) ||
                        (link.getExpirationTime() > 0 &&
                                (System.currentTimeMillis() / 1000 - link.getCreatedAt() >= link.getExpirationTime()))) {
                    linkJson.addProperty("isActive", false);
                } else {
                    linkJson.addProperty("isActive", true);
                }

                linksArray.add(linkJson);
            }

            JsonObject responseJson = new JsonObject();
            responseJson.add("links", linksArray);

            sendResponse(exchange, responseJson.toString(), 200);
        } else {
            sendResponse(exchange, "{\"message\": \"Метод не разрешен\"}", 405);
        }
    }

    private void sendResponse(HttpExchange exchange, String response, int statusCode) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, response.getBytes(StandardCharsets.UTF_8).length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes(StandardCharsets.UTF_8));
        os.close();
    }
}
