package com.example;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.logging.Logger;

public class RequestController implements HttpHandler {
    private static final Logger LOGGER = Logger.getLogger(RequestController.class.getName());
    private final RegisterHandler registerHandler;
    private final AuthHandler authHandler;
    private final ShortenLinkHandler shortenLinkHandler;
    private final RetrieveLinksHandler retrieveLinksHandler;
    private final RedirectHandler redirectHandler;
    private final DeleteLinkHandler deleteLinkHandler;

    public RequestController(DatabaseManager databaseManager) {
        this.shortenLinkHandler = new ShortenLinkHandler(databaseManager);
        this.redirectHandler = new RedirectHandler(databaseManager);
        this.registerHandler = new RegisterHandler(databaseManager);
        this.authHandler = new AuthHandler(databaseManager);
        this.retrieveLinksHandler = new RetrieveLinksHandler(databaseManager);
        this.deleteLinkHandler = new DeleteLinkHandler(databaseManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        LOGGER.info("Полученый запрос: " + exchange.getRequestMethod() + " " + exchange.getRequestURI());
        CORSHandler.addCORSHeaders(exchange);

        if ("OPTIONS".equals(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        String path = exchange.getRequestURI().getPath();
        if ("/register".equals(path)) {
            registerHandler.handle(exchange);
        } else if ("/auth".equals(path)) {
            authHandler.handle(exchange);
        } else if ("/shorten".equals(path)) {
            shortenLinkHandler.handle(exchange);
        } else if ("/links".equals(path)) {
            retrieveLinksHandler.handle(exchange);
        } else if (path.startsWith("/links/delete")) { 
            deleteLinkHandler.handle(exchange);
        } else {
            redirectHandler.handle(exchange);
        }
    }
}