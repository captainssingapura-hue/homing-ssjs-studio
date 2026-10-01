package hue.captains.singapura.js.homing.docview.gallery;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * The gallery's specimens: for each primitive, the parts of the reference docs it is shown
 * with - each by the doc's authentic path and the part's key, which is all a widget is made
 * from - and one that names no part, to show what a widget says when its content is
 * unavailable.
 */
public final class Specimens {

    private Specimens() {}

    /** Where the reference docs are placed in the gallery. */
    public static final String DOCS = "/docs";

    /**
     * A specimen: what it is called, what it shows, and the params its widget is made with.
     *
     * @param doc a reference doc's authentic path
     * @param key the part's key in it
     */
    public record Specimen(String title, String note, String doc, String key) {
        public Specimen {
            Objects.requireNonNull(title, "Specimen.title");
            Objects.requireNonNull(note, "Specimen.note");
            Objects.requireNonNull(doc, "Specimen.doc");
            Objects.requireNonNull(key, "Specimen.key");
        }
    }

    private static Specimen of(String title, String note, String doc, String key) { return new Specimen(title, note, DOCS + "/" + doc, key); }

    /** A specimen naming no part: its widget says the content is unavailable. */
    private static Specimen missing(String kind) {
        return of("A part that is not there", "Its key names nothing in the doc: the widget says it is unavailable, and why.", "markdown", "nowhere-" + kind + ":0");
    }

    /** The specimens, by the primitive they show: prose, code, table, image. */
    public static final Map<String, List<Specimen>> BY_KIND;

    static {
        var by = new LinkedHashMap<String, List<Specimen>>();
        by.put("prose", List.of(
                of("An introduction", "The root's own prose: paragraphs.", "markdown", ":0"),
                of("Lists, emphasis, a quote", "A list nested in a list, code spans, strong and emphasis, a quote.", "markdown", "prose-only:0"),
                of("A fence inside a list", "An ordered list whose item holds a fence: it stays in the prose.", "markdown", "code-inside-a-list:0"),
                of("A quote holding a table", "A table inside a quote, and a setext heading: both prose.", "markdown", "a-quote-holding-a-table:0"),
                of("A rigid doc's text segment", "Emphasis and code in a rigid node's text.", "rigid", "prose-segments:0"),
                of("A list of a composed doc", "A list segment of prose items, written as a markdown list.", "composed", "titled-markdown:2"),
                missing("prose")));
        by.put("code", List.of(
                of("Java", "A fence said to be java.", "markdown", "code-between-prose:1"),
                of("No language said", "A tilde fence with no language: its source, a hash line and all.", "markdown", "code-between-prose:3"),
                of("Mermaid, drawn", "A diagram: drawn by the diagram steward in the background, its source a pick away.", "markdown", "a-diagram:0"),
                of("Mermaid that cannot be read", "The steward says why it cannot be drawn; the source is still there.", "markdown", "a-diagram:2"),
                of("A composed doc's code segment", "Said to be bash.", "composed", "a-titled-code-segment:0"),
                of("A rigid doc by names", "A typed code segment, java.", "named-rigid", "how_it_reads:0"),
                missing("code")));
        by.put("table", List.of(
                of("Alignment, an escaped pipe", "Columns left, centred and right; a pipe escaped in a cell, and one in a code span.", "markdown", "a-table-between-prose:1"),
                of("A table first", "A section that opens with its table.", "markdown", "a-table-first:0"),
                of("A rigid doc's relation", "A relation segment, with its caption.", "rigid", "code-and-a-relation:1"),
                of("A badge, set strong", "A relation's cell articulated: a success badge, strong.", "composed", "titled-markdown:3"),
                of("A table held as a doc", "A table doc's cells: a badge, and one spanning two columns, centred.", "composed", "figures-and-tables:1"),
                missing("table")));
        by.put("image", List.of(
                of("An SVG in a rigid doc", "Drawn inline, in the text's colour; its caption under it.", "rigid", "a-picture:0"),
                of("An SVG in a composed doc", "Its caption the SVG's own title.", "composed", "figures-and-tables:2"),
                missing("image")));
        BY_KIND = java.util.Collections.unmodifiableMap(by);
    }

    /** The specimens as the page has them: {@code const GALLERY_SPECIMENS = Object.freeze({ prose: [{ title, note, doc, key }], … });} */
    public static String js() {
        String kinds = BY_KIND.entrySet().stream().map(e -> quote(e.getKey()) + ": Object.freeze([" + e.getValue().stream()
                        .map(s -> "Object.freeze({ title: " + quote(s.title()) + ", note: " + quote(s.note()) + ", doc: " + quote(s.doc()) + ", key: " + quote(s.key()) + " })")
                        .collect(Collectors.joining(", ")) + "])")
                .collect(Collectors.joining(", "));
        return "const GALLERY_SPECIMENS = Object.freeze({ " + kinds + " });";
    }

    private static String quote(String s) { return "\"" + s.replace("\\", "\\\\").replace("\"", "\\\"") + "\""; }
}
