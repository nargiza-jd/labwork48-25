package kg.attractor.java.utils;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

public class Utils {
    public static Map<String, String> parseUrlEncoded(String body, String delimiter) {
        if (body == null || body.isEmpty()) {
            return Collections.emptyMap();
        }
        return Arrays.stream(body.split(delimiter))
                .map(s -> s.split("=", 2))
                .filter(a -> a.length == 2)
                .collect(Collectors.toMap(
                        a -> decode(a[0]),
                        a -> decode(a[1])
                ));
    }

    private static String decode(String s) {
        try {
            return URLDecoder.decode(s, StandardCharsets.UTF_8.name());
        } catch (java.io.UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}