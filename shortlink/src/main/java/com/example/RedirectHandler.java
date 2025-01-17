package com.example;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;

public class RedirectHandler implements HttpHandler {
    private final DatabaseManager databaseManager;

    public RedirectHandler(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        if (path != null && path.length() > 1) {
            String shortUrl = path.substring(1);

            Link link = databaseManager.getLinkByShortUrl(shortUrl);
            if (link != null) {
                if ((link.getMaxClicks() != null && link.getClicks() >= link.getMaxClicks()) ||
                        (System.currentTimeMillis() / 1000 - link.getCreatedAt() >= link.getExpirationTime())) {
                    sendResponse(exchange, "Ссылка недействительна.", 410);
                    return;
                }

                databaseManager.incrementClickCount(link.getId());

                exchange.getResponseHeaders().set("Location", link.getOriginalUrl());
                exchange.sendResponseHeaders(302, -1);
            } else {
                sendResponse(exchange, "Ссылка не найдена.", 404);
            }
        } else {
            sendResponse(exchange, "Некорректный запрос.", 400);
        }
    }

    private void sendResponse(HttpExchange exchange, String response, int statusCode) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, response.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}
