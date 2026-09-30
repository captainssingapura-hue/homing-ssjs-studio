package hue.captains.singapura.js.homing.docview.tree;

import hue.captains.singapura.js.homing.workspace.groups.core.models.TreePlacement.Label;
import hue.captains.singapura.js.homing.workspace.groups.core.models.TreePlacement.Name;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Markdown made into a doc tree, read line by line.
 *
 * <ul>
 *   <li><b>Headings</b> are ATX headings, {@code #} to {@code ######}, up to three spaces in. Setext
 *       headings are prose.</li>
 *   <li><b>Nesting is structural.</b> A heading nests under the nearest heading above it of a lower
 *       level, however many levels it skips.</li>
 *   <li><b>The title.</b> A first-level heading that comes first and repeats the doc's title is the
 *       root, not a node.</li>
 *   <li><b>Fences.</b> A fence opens with three or more backticks or tildes and closes with the same
 *       character, at least as many; nothing inside it is a heading.</li>
 *   <li><b>A section's leaf</b> is everything between its heading and the next, split into parts: a
 *       fence at the top level is a code part, its language the first word of its info string,
 *       lower-cased; a pipe table at the top level is a table part; everything else - a fence or a
 *       table inside a list or a quote among it - is prose, kept as markdown text.</li>
 * </ul>
 */
public final class MarkdownTrees {

    private MarkdownTrees() {}

    private static final Pattern HEADING = Pattern.compile("^ {0,3}(#{1,6})(?:[ \\t]+(.*?))?[ \\t]*$");
    private static final Pattern FENCE = Pattern.compile("^( *)(`{3,}|~{3,})(.*)$");
    private static final Pattern DELIMITER = Pattern.compile("^ {0,1}\\|?\\s*:?-+:?\\s*(\\|\\s*:?-+:?\\s*)*\\|?\\s*$");

    /** A doc's markdown as a tree, its root labelled by the doc's title. */
    public static DocTree of(String title, String markdown) {
        Objects.requireNonNull(title, "title");
        Objects.requireNonNull(markdown, "markdown");
        var root = new Building(Optional.empty(), HeadingLabels.of(title), 0);
        read(markdown, root, true);
        return new DocTree(root.build());
    }

    /**
     * Markdown split into parts and nothing more - code, tables and prose - its headings kept in
     * the prose: a markdown segment of a rigid or composed doc, whose headings are the doc's.
     */
    public static List<Part> parts(String markdown) {
        Objects.requireNonNull(markdown, "markdown");
        var only = new Building(Optional.empty(), Label.of(""), 0);
        read(markdown, only, false);
        return List.copyOf(only.leaf);
    }

    private static void read(String markdown, Building root, boolean headings) {
        String[] lines = markdown.replace("\r\n", "\n").replace('\r', '\n').split("\n", -1);
        var stack = new ArrayList<Building>();
        stack.add(root);
        var prose = new ArrayList<String>();
        boolean first = true;
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            Building at = stack.get(stack.size() - 1);
            Matcher fence = FENCE.matcher(line);
            if (fence.matches()) {
                int close = close(lines, i, fence.group(2));
                boolean top = fence.group(1).length() < 2;
                if (top) {
                    at.flush(prose);
                    String info = fence.group(3).strip();
                    String language = info.isEmpty() ? "" : info.split("\\s+")[0].toLowerCase(Locale.ROOT);
                    at.leaf.add(new Part.Code(language, join(lines, i + 1, close)));
                } else {
                    for (int k = i; k <= Math.min(close, lines.length - 1); k++) prose.add(lines[k]);
                }
                first = false;
                i = close;
                continue;
            }
            Matcher heading = HEADING.matcher(line);
            if (headings && heading.matches()) {
                int level = heading.group(1).length();
                String text = heading.group(2) == null ? "" : heading.group(2);
                if (first && level == 1 && same(HeadingLabels.of(text).text(), root.label.text())) {
                    root.label = HeadingLabels.of(text);
                    first = false;
                    continue;
                }
                first = false;
                at.flush(prose);
                while (stack.size() > 1 && stack.get(stack.size() - 1).level >= level) stack.remove(stack.size() - 1);
                Building parent = stack.get(stack.size() - 1);
                Label label = HeadingLabels.of(text);
                var child = new Building(Optional.of(parent.siblings.take(NodeNames.of(label.text()))), label, level);
                parent.children.add(child);
                stack.add(child);
                continue;
            }
            if (i + 1 < lines.length && table(line, lines[i + 1])) {
                at.flush(prose);
                int end = i + 2;
                while (end < lines.length && !lines[end].isBlank() && lines[end].contains("|") && !(headings && HEADING.matcher(lines[end]).matches())) end++;
                at.leaf.add(table(lines, i, end));
                first = false;
                i = end - 1;
                continue;
            }
            if (!line.isBlank()) first = false;
            prose.add(line);
        }
        stack.get(stack.size() - 1).flush(prose);
    }

    /** Whether two headings say the same: the title, repeated. */
    private static boolean same(String a, String b) { return a.strip().equalsIgnoreCase(b.strip()); }

    /** The line a fence opened at {@code open} closes on: the same character, at least as many; the last line when it never does. */
    private static int close(String[] lines, int open, String opener) {
        char c = opener.charAt(0);
        for (int k = open + 1; k < lines.length; k++) {
            String t = lines[k].strip();
            if (t.length() >= opener.length() && t.chars().allMatch(x -> x == c)) return k;
        }
        return lines.length - 1;
    }

    private static String join(String[] lines, int from, int to) {
        var b = new StringBuilder();
        for (int k = from; k < to && k < lines.length; k++) {
            if (k > from) b.append('\n');
            b.append(lines[k]);
        }
        return b.toString();
    }

    /** A pipe table starts here: a row of cells at the top level, then the delimiter row. */
    private static boolean table(String line, String next) {
        if (!line.contains("|") || line.startsWith("  ") || line.stripLeading().startsWith(">")) return false;
        if (!DELIMITER.matcher(next).matches() || !next.contains("-")) return false;
        return next.contains("|") || line.strip().startsWith("|");
    }

    /** The table on lines {@code [from, to)}: its header, its delimiter's alignments, its rows. */
    private static Part.Table table(String[] lines, int from, int to) {
        List<String> header = cells(lines[from]);
        List<String> delimiter = cells(lines[from + 1]);
        var columns = new ArrayList<Part.Column>();
        for (int c = 0; c < header.size(); c++) {
            String d = c < delimiter.size() ? delimiter.get(c).strip() : "";
            String align = d.startsWith(":") && d.endsWith(":") && d.length() > 1 ? "center" : d.endsWith(":") ? "right" : d.startsWith(":") ? "left" : "";
            columns.add(new Part.Column(header.get(c), align));
        }
        var rows = new ArrayList<Part.Row>();
        for (int r = from + 2; r < to; r++) {
            List<String> cells = cells(lines[r]);
            var row = new ArrayList<Part.Cell>();
            for (int c = 0; c < columns.size(); c++) row.add(Part.Cell.of(c < cells.size() ? cells.get(c) : ""));
            rows.add(new Part.Row(row));
        }
        return new Part.Table(columns, rows, "");
    }

    /** A row's cells: split at the pipes that are not escaped nor inside a code span, the outer ones dropped. */
    static List<String> cells(String row) {
        String s = row.strip();
        if (s.startsWith("|")) s = s.substring(1);
        if (s.endsWith("|") && !s.endsWith("\\|")) s = s.substring(0, s.length() - 1);
        var out = new ArrayList<String>();
        var cell = new StringBuilder();
        boolean code = false;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '\\' && i + 1 < s.length() && s.charAt(i + 1) == '|') { cell.append('|'); i++; continue; }
            if (c == '`') code = !code;
            if (c == '|' && !code) { out.add(cell.toString().strip()); cell.setLength(0); continue; }
            cell.append(c);
        }
        out.add(cell.toString().strip());
        return out;
    }

    /** A node while it is read: what it has so far, and the names its children have taken. */
    private static final class Building {
        final Optional<Name> name;
        Label label;
        final int level;
        final List<Part> leaf = new ArrayList<>();
        final List<Building> children = new ArrayList<>();
        final NodeNames.Siblings siblings = new NodeNames.Siblings();

        Building(Optional<Name> name, Label label, int level) {
            this.name = name;
            this.label = label;
            this.level = level;
        }

        /** The prose read since the last part, as a part - its blank lines at either end dropped; nothing, when it is all blank. */
        void flush(List<String> prose) {
            int a = 0, b = prose.size();
            while (a < b && prose.get(a).isBlank()) a++;
            while (b > a && prose.get(b - 1).isBlank()) b--;
            if (a < b) leaf.add(new Part.Prose(String.join("\n", prose.subList(a, b))));
            prose.clear();
        }

        DocTree.Node build() { return new DocTree.Node(name, label, leaf, children.stream().map(Building::build).toList()); }
    }
}
