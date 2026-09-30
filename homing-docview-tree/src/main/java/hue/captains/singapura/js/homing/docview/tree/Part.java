package hue.captains.singapura.js.homing.docview.tree;

import java.util.List;
import java.util.Objects;

/**
 * A part of a node's leaf: one widget's worth of content, and the content it is asked for.
 * Plain parts - prose, code, a table - travel in the doc's payload; an image is fetched when
 * it is wanted. Each is its own type, and its content is itself.
 */
public sealed interface Part permits Part.Prose, Part.Code, Part.Table, Part.Image {

    /** The widget type that shows it, and the content type it is asked of. */
    String type();

    /** Markdown text, rendered in the browser: paragraphs, lists, quotes, and what is inline in them. */
    record Prose(String text) implements Part {
        public Prose { Objects.requireNonNull(text, "Prose.text"); }
        @Override public String type() { return "prose"; }
    }

    /**
     * Source in a language: the renderer registered for the language shows it - a diagram for
     * {@code mermaid}, the source as code for any other.
     *
     * @param language lower case, the first word of a fence's info string; empty when none is said
     */
    record Code(String language, String source) implements Part {
        public Code {
            Objects.requireNonNull(language, "Code.language");
            Objects.requireNonNull(source, "Code.source");
        }
        @Override public String type() { return "code"; }
    }

    /**
     * Columns and rows; every cell markdown text, as prose's inline elements are.
     *
     * @param caption shown with it, or empty
     */
    record Table(List<Column> columns, List<Row> rows, String caption) implements Part {
        public Table {
            columns = List.copyOf(Objects.requireNonNull(columns, "Table.columns"));
            rows = List.copyOf(Objects.requireNonNull(rows, "Table.rows"));
            Objects.requireNonNull(caption, "Table.caption");
        }
        @Override public String type() { return "table"; }
    }

    /**
     * A column: its title - markdown text - and its alignment.
     *
     * @param align {@code left}, {@code center}, {@code right}, or empty when none is said
     */
    record Column(String title, String align) {
        public Column {
            Objects.requireNonNull(title, "Column.title");
            Objects.requireNonNull(align, "Column.align");
        }
    }

    /** A row: its cells, in the columns' order. */
    record Row(List<Cell> cells) {
        public Row { cells = List.copyOf(Objects.requireNonNull(cells, "Row.cells")); }
    }

    /**
     * A cell: markdown text, the columns and rows it spans, and what marks it.
     *
     * @param badge    the badge's word - {@code success}, {@code warning}, {@code error} - or empty
     * @param align    {@code left}, {@code center}, {@code right}, or empty for its column's
     * @param emphasis {@code strong}, {@code muted}, or empty
     */
    record Cell(String text, int colSpan, int rowSpan, String badge, String align, String emphasis) {
        public Cell {
            Objects.requireNonNull(text, "Cell.text");
            Objects.requireNonNull(badge, "Cell.badge");
            Objects.requireNonNull(align, "Cell.align");
            Objects.requireNonNull(emphasis, "Cell.emphasis");
            if (colSpan < 1 || rowSpan < 1) throw new IllegalArgumentException("a cell spans one column and one row or more, not " + colSpan + " by " + rowSpan);
        }

        public static Cell of(String text) { return new Cell(text, 1, 1, "", "", ""); }
    }

    /**
     * An image: SVG markup, drawn inline so it takes the theme's colours; or a raster, fetched
     * when it is wanted by the resource it is read from. Its alt text is its accessible name,
     * its caption, when it has one, shown under it.
     *
     * @param svg      the markup, or empty for a raster
     * @param resource the raster's resource on the classpath, or empty for SVG
     * @param mime     the raster's media type, or empty for SVG
     * @param alt      its accessible name
     * @param caption  shown under it, or empty
     */
    record Image(String svg, String resource, String mime, String alt, String caption) implements Part {
        public Image {
            Objects.requireNonNull(svg, "Image.svg");
            Objects.requireNonNull(resource, "Image.resource");
            Objects.requireNonNull(mime, "Image.mime");
            Objects.requireNonNull(alt, "Image.alt");
            Objects.requireNonNull(caption, "Image.caption");
            if (svg.isEmpty() == resource.isEmpty()) throw new IllegalArgumentException("an image is SVG markup or a raster's resource - one of them");
        }
        @Override public String type() { return "image"; }

        /** Whether it is fetched when wanted, a raster, rather than drawn from its markup. */
        public boolean raster() { return svg.isEmpty(); }
    }
}
