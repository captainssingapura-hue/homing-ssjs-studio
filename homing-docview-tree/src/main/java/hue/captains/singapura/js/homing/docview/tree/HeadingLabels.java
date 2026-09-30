package hue.captains.singapura.js.homing.docview.tree;

import hue.captains.singapura.js.homing.workspace.groups.core.models.TreePlacement.Label;
import hue.captains.singapura.js.homing.workspace.groups.core.models.TreePlacement.Run;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A markdown heading's text as a label: its plain text, and the runs that draw it - a code
 * span as code, {@code **strong**} as strong, {@code *emphasis*} as emphasis. A link or an
 * image is its text; an escaped character is itself; an ATX heading's closing hashes are
 * dropped. A heading of plain text alone has no runs. Anything the reader of the source
 * would not see as markup is kept as written.
 */
public final class HeadingLabels {

    private HeadingLabels() {}

    /** The label of a heading's text, the hashes that open it already taken off. */
    public static Label of(String heading) {
        Objects.requireNonNull(heading, "heading");
        String text = heading.strip().replaceAll("(^|\\s+)#+\\s*$", "").strip();
        List<Run> runs = merge(parse(text));
        if (runs.stream().allMatch(r -> r instanceof Run.Text)) return Label.of(plain(runs));
        return new Label(plain(runs), runs);
    }

    private static String plain(List<Run> runs) {
        var b = new StringBuilder();
        for (Run r : runs) b.append(r.text());
        return b.toString();
    }

    /** The runs of a line of inline markdown. */
    private static List<Run> parse(String s) {
        var out = new ArrayList<Run>();
        var text = new StringBuilder();
        int i = 0, n = s.length();
        while (i < n) {
            char c = s.charAt(i);
            if (c == '\\' && i + 1 < n && isPunct(s.charAt(i + 1))) { text.append(s.charAt(i + 1)); i += 2; continue; }
            if (c == '`') {
                int ticks = run(s, i, '`');
                int close = closing(s, i + ticks, ticks);
                if (close >= 0) {
                    flush(text, out);
                    String code = s.substring(i + ticks, close);
                    if (code.length() > 1 && code.startsWith(" ") && code.endsWith(" ") && !code.isBlank()) code = code.substring(1, code.length() - 1);
                    if (!code.isEmpty()) out.add(new Run.Code(code));
                    i = close + ticks;
                } else { text.append(s, i, i + ticks); i += ticks; }
                continue;
            }
            if ((c == '!' && i + 1 < n && s.charAt(i + 1) == '[') || c == '[') {
                int open = c == '!' ? i + 1 : i;
                int end = bracket(s, open);
                if (end > 0 && end + 1 < n && s.charAt(end + 1) == '(') {
                    int paren = s.indexOf(')', end + 2);
                    if (paren > 0) {
                        flush(text, out);
                        out.addAll(parse(s.substring(open + 1, end)));
                        i = paren + 1;
                        continue;
                    }
                }
                text.append(c);
                i++;
                continue;
            }
            if ((c == '*' || c == '_') && i + 1 < n && s.charAt(i + 1) == c && opens(s, i, 2)) {
                int close = s.indexOf("" + c + c, i + 2);
                if (close > i + 2 && closes(s, close, 2)) {
                    flush(text, out);
                    out.add(new Run.Strong(plain(merge(parse(s.substring(i + 2, close))))));
                    i = close + 2;
                    continue;
                }
            }
            if ((c == '*' || c == '_') && opens(s, i, 1)) {
                int close = single(s, i + 1, c);
                if (close > i + 1 && closes(s, close, 1)) {
                    flush(text, out);
                    out.add(new Run.Emphasis(plain(merge(parse(s.substring(i + 1, close))))));
                    i = close + 1;
                    continue;
                }
            }
            text.append(c);
            i++;
        }
        flush(text, out);
        return out;
    }

    /** Adjacent text runs made one; empty runs dropped. */
    private static List<Run> merge(List<Run> runs) {
        var out = new ArrayList<Run>();
        for (Run r : runs) {
            if (r.text().isEmpty()) continue;
            if (r instanceof Run.Text t && !out.isEmpty() && out.get(out.size() - 1) instanceof Run.Text last) {
                out.set(out.size() - 1, new Run.Text(last.text() + t.text()));
            } else out.add(r);
        }
        return out;
    }

    private static void flush(StringBuilder text, List<Run> out) {
        if (!text.isEmpty()) { out.add(new Run.Text(text.toString())); text.setLength(0); }
    }

    private static int run(String s, int from, char c) {
        int k = from;
        while (k < s.length() && s.charAt(k) == c) k++;
        return k - from;
    }

    /** Where a code span of this many backticks closes: a run of exactly as many. */
    private static int closing(String s, int from, int ticks) {
        for (int k = from; k < s.length(); ) {
            if (s.charAt(k) == '`') {
                int r = run(s, k, '`');
                if (r == ticks) return k;
                k += r;
            } else k++;
        }
        return -1;
    }

    /** The bracket closing the one at {@code open}, nesting counted; -1 when none. */
    private static int bracket(String s, int open) {
        int depth = 0;
        for (int k = open; k < s.length(); k++) {
            char c = s.charAt(k);
            if (c == '\\') { k++; continue; }
            if (c == '[') depth++;
            else if (c == ']' && --depth == 0) return k;
        }
        return -1;
    }

    /** A single delimiter's close, not half of a double. */
    private static int single(String s, int from, char c) {
        for (int k = from; k < s.length(); k++) {
            if (s.charAt(k) != c) continue;
            if (k + 1 < s.length() && s.charAt(k + 1) == c) { k++; continue; }
            return k;
        }
        return -1;
    }

    /** A delimiter opens where it is followed by a letter or digit, and - an underscore - is not inside a word. */
    private static boolean opens(String s, int at, int len) {
        if (at + len >= s.length() || Character.isWhitespace(s.charAt(at + len))) return false;
        return s.charAt(at) != '_' || at == 0 || !Character.isLetterOrDigit(s.charAt(at - 1));
    }

    private static boolean closes(String s, int at, int len) {
        if (at == 0 || Character.isWhitespace(s.charAt(at - 1))) return false;
        return s.charAt(at) != '_' || at + len >= s.length() || !Character.isLetterOrDigit(s.charAt(at + len));
    }

    private static boolean isPunct(char c) { return c < 128 && !Character.isLetterOrDigit(c) && !Character.isWhitespace(c); }
}
