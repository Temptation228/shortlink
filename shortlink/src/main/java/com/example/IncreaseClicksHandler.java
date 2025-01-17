package com.example;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class IncreaseClicksHandler implements HttpHandler {
    private final DatabaseManager databaseManager;

    public IncreaseClicksHandler(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("POST".equals(exchange.getRequestMethod())) {
            try {
                String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                
                JsonObject json = JsonParser.parseString(requestBody).getAsJsonObject();
                int linkId = json.get("linkId").getAsInt(); 
                int newMaxClicks = json.get("newMaxClicks").getAsInt(); 

                databaseManager.updateMaxClicks(linkId, newMaxClicks);

                exchange.sendResponseHeaders(200, 0);
            } catch (Exception e) {
                exchange.sendResponseHeaders(400, 0);
                System.err.println("Ошибка обработки запроса: " + e.getMessage());
            }
        } else {
            exchange.sendResponseHeaders(405, 0);
        }

        OutputStream os = exchange.getResponseBody();
        os.close();
    }
}
