package kg.attractor.java.server;

public enum ContentType {
    TEXT_HTML("text/html; charset=UTF-8"),
    TEXT_CSS("text/css; charset=UTF-8"),
    TEXT_PLAIN("text/plain; charset=UTF-8"),
    APPLICATION_JAVASCRIPT("application/javascript"),
    IMAGE_PNG("image/png"),
    IMAGE_JPEG("image/jpeg"),
    IMAGE_GIF("image/gif"),
    APPLICATION_JSON("application/json; charset=UTF-8"),
    APPLICATION_OCTET_STREAM("application/octet-stream");

    private final String header;

    ContentType(String header) {
        this.header = header;
    }

    @Override
    public String toString() {
        return header;
    }
}