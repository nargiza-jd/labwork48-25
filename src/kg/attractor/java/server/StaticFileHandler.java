package kg.attractor.java.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;

public class StaticFileHandler implements HttpHandler {

    public StaticFileHandler() {
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String requestUriPath = exchange.getRequestURI().getPath();

        String resourcePathInClasspath = requestUriPath;

        System.out.println("Attempting to load resource from Classpath: " + resourcePathInClasspath);

        InputStream is = null;
        try {
            is = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourcePathInClasspath);

            if (is == null) {
                if (resourcePathInClasspath.startsWith("/")) {
                    is = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourcePathInClasspath.substring(1));
                    if (is != null) {
                        System.out.println("Found resource using relative path (no leading slash): " + resourcePathInClasspath.substring(1));
                    }
                }
            }

            if (is == null) {
                System.err.println("Static resource NOT found (after all attempts): " + resourcePathInClasspath);
                exchange.sendResponseHeaders(404, -1);
                return;
            }

            String contentType = getContentType(resourcePathInClasspath);
            exchange.getResponseHeaders().set("Content-Type", contentType);
            exchange.sendResponseHeaders(200, 0);
            is.transferTo(exchange.getResponseBody());

        } finally {
            if (is != null) {
                is.close();
            }
            exchange.close();
        }
    }

    private String getContentType(String resourcePath) {
        String fileName = resourcePath;
        if (fileName.endsWith(".html")) return ContentType.TEXT_HTML.toString();
        if (fileName.endsWith(".css")) return ContentType.TEXT_CSS.toString();
        if (fileName.endsWith(".js")) return ContentType.APPLICATION_JAVASCRIPT.toString();
        if (fileName.endsWith(".png")) return ContentType.IMAGE_PNG.toString();
        if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) return ContentType.IMAGE_JPEG.toString();
        if (fileName.endsWith(".gif")) return ContentType.IMAGE_GIF.toString();
        return ContentType.APPLICATION_OCTET_STREAM.toString();
    }
}