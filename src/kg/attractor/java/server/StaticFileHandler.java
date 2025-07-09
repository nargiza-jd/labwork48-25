package kg.attractor.java.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class StaticFileHandler implements HttpHandler {
    private final Path basePath;

    public StaticFileHandler(Path basePath) {
        this.basePath = basePath;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Path filePath = basePath.resolve(exchange.getRequestURI().getPath().substring("/static".length())).normalize();

        if (!filePath.startsWith(basePath) || !Files.exists(filePath) || Files.isDirectory(filePath)) {
            exchange.sendResponseHeaders(404, -1);
            return;
        }

        String contentType = getContentType(filePath);
        exchange.getResponseHeaders().set("Content-Type", contentType);
        exchange.sendResponseHeaders(200, 0);
        Files.copy(filePath, exchange.getResponseBody());
        exchange.close();
    }

    private String getContentType(Path filePath) {
        String fileName = filePath.getFileName().toString();
        if (fileName.endsWith(".html")) return "text/html";
        if (fileName.endsWith(".css")) return "text/css";
        if (fileName.endsWith(".js")) return "application/javascript";
        if (fileName.endsWith(".png")) return "image/png";
        if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) return "image/jpeg";
        if (fileName.endsWith(".gif")) return "image/gif";
        return "application/octet-stream";
    }
}