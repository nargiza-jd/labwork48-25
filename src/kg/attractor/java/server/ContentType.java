package kg.attractor.java.server;

public enum ContentType {
    TEXT_HTML ("text/html; charset=UTF-8"),
    TEXT_CSS  ("text/css; charset=UTF-8"),
    TEXT_PLAIN("text/plain; charset=UTF-8"),
    IMAGE_JPEG("image/jpeg"),
    IMAGE_PNG ("image/png");

    private final String v;
    ContentType(String v) { this.v = v; }
    @Override public String toString() { return v; }
}