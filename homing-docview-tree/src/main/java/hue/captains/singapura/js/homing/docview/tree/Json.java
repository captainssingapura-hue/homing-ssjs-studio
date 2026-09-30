package hue.captains.singapura.js.homing.docview.tree;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/** JSON text, written: a string quoted, an object of its entries in the order given, an array of its items. */
final class Json {

    private Json() {}

    static String str(String s) {
        var b = new StringBuilder(s.length() + 2).append('"');
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '"' -> b.append("\\\"");
                case '\\' -> b.append("\\\\");
                case '\n' -> b.append("\\n");
                case '\r' -> b.append("\\r");
                case '\t' -> b.append("\\t");
                case ' ' -> b.append("\\u2028");
                case ' ' -> b.append("\\u2029");
                default -> {
                    if (c < 0x20) b.append(String.format("\\u%04x", (int) c));
                    else b.append(c);
                }
            }
        }
        return b.append('"').toString();
    }

    /** An object: its entries' values already JSON. */
    static String obj(Map<String, String> entries) {
        return entries.entrySet().stream().map(e -> str(e.getKey()) + ":" + e.getValue()).collect(Collectors.joining(",", "{", "}"));
    }

    static <T> String arr(List<T> items, Function<T, String> json) {
        return items.stream().map(json).collect(Collectors.joining(",", "[", "]"));
    }

    static String num(int n) { return Integer.toString(n); }
}
