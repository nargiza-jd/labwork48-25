package kg.attractor.java.server;

public enum ResponseCodes {
    OK(200),
    SEE_OTHER(303),
    NOT_FOUND(404),
    INTERNAL_SERVER_ERROR(500);

    private final int code;

    ResponseCodes(int c) {
        code = c;
    }

    public int getCode() {
        return code;
    }
}