package com.example;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class DeleteLinkHandler implements HttpHandler {
    private final DatabaseManager databaseManager;

    public DeleteLinkHandler(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("DELETE".equalsIgnoreCase(exchange.getRequestMethod())) {
            try {
                String[] pathParts = exchange.getRequestURI().getPath().split("/");
                if (pathParts.length < 3) {
                    sendResponse(exchange, "{\"message\": \"Недоступный URL\"}", 400);
                    return;
                }

                int linkId = Integer.parseInt(pathParts[pathParts.length - 1]);

                databaseManager.deleteLinkById(linkId);

                sendResponse(exchange, "{\"message\": \"Ссылка успешно удалена\"}", 200);
            } catch (NumberFormatException e) {
                sendResponse(exchange, "{\"message\": \"Неверный ID ссылки\"}", 400);
            } catch (Exception e) {
                sendResponse(exchange, "{\"message\": \"Internal Server Error\"}", 500);
                e.printStackTrace();
            }
        } else {
            sendResponse(exchange, "{\"message\": \"Метод не разрешен\"}", 405);
        }
    }

    private void sendResponse(HttpExchange exchange, String response, int statusCode) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, response.getBytes(StandardCharsets.UTF_8).length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes(StandardCharsets.UTF_8));
        }
    }
}
