package kg.attractor.java.server;

import com.sun.net.httpserver.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public abstract class BasicServer {

    private final HttpServer server;
    private final Map<String, RouteHandler> routes = new HashMap<>();
    private static final Path DATA = Path.of("data");

    protected BasicServer(String host, int port) throws IOException {
        server = HttpServer.create(new InetSocketAddress(host, port), 50);
        System.out.printf("Server started: http://%s:%d/%n", host, port);

        server.createContext("/", this::dispatch);
        server.createContext("/static", new StaticFileHandler(DATA.resolve("static")));
    }

    public final void start()           { server.start(); }

    protected void registerGet (String path, RouteHandler h) { routes.put("GET  " + path, h); }
    protected void registerPost(String path, RouteHandler h) { routes.put("POST " + path, h); }

    protected String getQueryParam(HttpExchange ex, String key) {
        String q = ex.getRequestURI().getRawQuery();
        if (q == null) return null;
        for (String p : q.split("&")) {
            String[] kv = p.split("=", 2);
            if (kv.length == 2 && kv[0].equals(key)) {
                try {
                    return java.net.URLDecoder.decode(kv[1], StandardCharsets.UTF_8.name());
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    return kv[1];
                }
            }
        }
        return null;
    }

    protected String body(HttpExchange ex) {
        try (BufferedReader r = new BufferedReader(
                new InputStreamReader(ex.getRequestBody(), StandardCharsets.UTF_8))) {
            return r.lines().collect(Collectors.joining());
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    protected void redirect303(HttpExchange ex, String to) {
        try {
            ex.getResponseHeaders().add("Location", to);
            ex.sendResponseHeaders(ResponseCodes.REDIRECT_303.getCode(), -1);
        } catch (IOException ignore) {
            System.err.println("Ошибка при перенаправлении: " + ignore.getMessage());
        }
    }

    protected void sendBytes(HttpExchange ex, ResponseCodes code,
                             ContentType ct, byte[] data) throws IOException {
        ex.getResponseHeaders().set("Content-Type", ct.toString());
        ex.sendResponseHeaders(code.getCode(), data.length);
        try (OutputStream os = ex.getResponseBody()) { os.write(data); }
    }

    private void dispatch(HttpExchange ex) throws IOException {
        String path = ex.getRequestURI().getPath();
        String method = ex.getRequestMethod().toUpperCase();
        String key = method + "  " + path;

        RouteHandler handler = routes.get(key);
        if (handler != null) {
            handler.handle(ex);
        } else {
            respond404(ex);
        }
    }

    private void respond404(HttpExchange ex) {
        try {
            sendBytes(ex, ResponseCodes.NOT_FOUND,
                    ContentType.TEXT_PLAIN,
                    "404 Not Found".getBytes(StandardCharsets.UTF_8));
        } catch (IOException ignore) {
            System.err.println("Ошибка при отправке 404: " + ignore.getMessage());
        }
    }

    private static class StaticFileHandler implements HttpHandler {
        private final Path root;
        StaticFileHandler(Path root) { this.root = root; }

        @Override public void handle(HttpExchange ex) throws IOException {
            Path file = root.resolve(
                    ex.getRequestURI().getPath().replaceFirst("/static/?", "")
            ).normalize();

            if (!file.startsWith(root) || Files.notExists(file) || Files.isDirectory(file)) {
                ex.sendResponseHeaders(404, -1);
                return;
            }
            String mime = Files.probeContentType(file);
            if (mime == null) mime = "application/octet-stream";

            byte[] bytes = Files.readAllBytes(file);
            ex.getResponseHeaders().set("Content-Type", mime);
            ex.sendResponseHeaders(200, bytes.length);
            try (OutputStream os = ex.getResponseBody()) { os.write(bytes); }
        }
    }
}