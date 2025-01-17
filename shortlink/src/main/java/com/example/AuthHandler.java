package com.example;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

public class AuthHandler implements HttpHandler {
    private static final Logger LOGGER = Logger.getLogger(AuthHandler.class.getName());
    private final DatabaseManager databaseManager;

    public AuthHandler(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        LOGGER.info("Обработка /auth запроса");
        CORSHandler.addCORSHeaders(exchange);

        String response;
        int responseCode = 200;

        if ("POST".equals(exchange.getRequestMethod())) {
            String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            LOGGER.info("Тело запроса: " + requestBody);

            JsonObject json = JsonParser.parseString(requestBody).getAsJsonObject();
            String uuid = json.get("body").getAsJsonObject().get("uuid").getAsString();
            String password = json.get("body").getAsJsonObject().get("password").getAsString();

            boolean isAuthenticated = databaseManager.checkUUIDAndPassword(uuid, password);
            if (isAuthenticated) {
                response = "{\"message\": \"Авторизован\"}";
            } else {
                responseCode = 401;
                response = "{\"message\": \"Неправильный uuid или пароль.\"}";
            }
        } else {
            responseCode = 405;
            response = "{\"message\": \"Метод не доступен\"}";
        }

        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(responseCode, response.getBytes(StandardCharsets.UTF_8).length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes(StandardCharsets.UTF_8));
        os.close();
    }
}
