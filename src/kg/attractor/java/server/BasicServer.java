package kg.attractor.java.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import kg.attractor.java.utils.Utils;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public abstract class BasicServer {


    private final HttpServer server;
    private final Map<String, RouteHandler> getRoutes = new HashMap<>();
    private final Map<String, RouteHandler> postRoutes = new HashMap<>();

    protected BasicServer(String host, int port) throws IOException {
        server = HttpServer.create(new InetSocketAddress(host, port), 50);
        System.out.printf("Server started: http://%s:%d/%n", host, port);

        server.createContext("/", this::dispatch);
        server.createContext("/static", new StaticFileHandler());
    }

    protected interface RouteHandler {
        void handle(HttpExchange ex) throws IOException;
    }

    protected void registerGet(String path, RouteHandler handler) {
        getRoutes.put(path, handler);
    }

    protected void registerPost(String path, RouteHandler handler) {
        postRoutes.put(path, handler);
    }

    private void dispatch(HttpExchange ex) throws IOException {
        String method = ex.getRequestMethod();
        String path = ex.getRequestURI().getPath();

        RouteHandler handler = null;
        if ("GET".equalsIgnoreCase(method)) {
            handler = getRoutes.get(path);
        } else if ("POST".equalsIgnoreCase(method)) {
            handler = postRoutes.get(path);
        }

        if (handler != null) {
            try {
                handler.handle(ex);
            } catch (Exception e) {
                e.printStackTrace();
                sendBytes(ex, ResponseCodes.INTERNAL_SERVER_ERROR, ContentType.TEXT_PLAIN, "Internal server error".getBytes(StandardCharsets.UTF_8));
            }
        } else {
            sendBytes(ex, ResponseCodes.NOT_FOUND, ContentType.TEXT_PLAIN, "404 not found".getBytes(StandardCharsets.UTF_8));
        }
    }

    public void start() {
        server.start();
    }

    protected String getQueryParam(HttpExchange ex, String name) {
        return ex.getRequestURI().getQuery() != null
                ? Optional.ofNullable(Utils.parseUrlEncoded(ex.getRequestURI().getQuery(), "&").get(name)).orElse("")
                : "";
    }

    protected String body(HttpExchange ex) throws IOException {
        return new String(ex.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
    }

    protected void sendBytes(HttpExchange ex, ResponseCodes code, ContentType type, byte[] bytes) throws IOException {
        ex.getResponseHeaders().set("Content-Type", type.toString());
        ex.sendResponseHeaders(code.getCode(), bytes.length);
        try (OutputStream os = ex.getResponseBody()) {
            os.write(bytes);
        }
    }

    protected void redirect303(HttpExchange ex, String path) throws IOException {
        ex.getResponseHeaders().set("Location", path);
        ex.sendResponseHeaders(ResponseCodes.SEE_OTHER.getCode(), -1);
        ex.close();
    }
}