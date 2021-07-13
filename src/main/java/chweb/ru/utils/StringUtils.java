package chweb.ru.utils;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @author chervinko <br>
 * 30.06.2021
 */
public final class StringUtils {
    public static final String EMPTY_STRING = "";

    private StringUtils() {
    }

    public static String handleNull(String str) {
        return handleNull(str, EMPTY_STRING);
    }

    public static String handleNull(String str, String defaultValue) {
        return str == null ? defaultValue : str;
    }

    public static boolean isEmpty(String s) {
        return (s == null) || (0 == s.length());
    }

    public static String getUrlParams(Map<String, String> params) {
        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            sb.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8));
            sb.append("=");
            sb.append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
            sb.append("&");
        }

        return sb.toString();
    }
}
