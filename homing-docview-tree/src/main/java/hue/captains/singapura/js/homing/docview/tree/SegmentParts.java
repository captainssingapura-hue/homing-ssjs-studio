package hue.captains.singapura.js.homing.docview.tree;

import hue.captains.singapura.js.homing.studio.base.composed.ArticulatedCell;
import hue.captains.singapura.js.homing.studio.base.composed.Articulation;
import hue.captains.singapura.js.homing.studio.base.composed.CodeSegment;
import hue.captains.singapura.js.homing.studio.base.composed.ComposedSegment;
import hue.captains.singapura.js.homing.studio.base.composed.EmbeddedSegment;
import hue.captains.singapura.js.homing.studio.base.composed.ImageSegment;
import hue.captains.singapura.js.homing.studio.base.composed.Listable;
import hue.captains.singapura.js.homing.studio.base.composed.MarkdownSegment;
import hue.captains.singapura.js.homing.studio.base.composed.OrderedListSegment;
import hue.captains.singapura.js.homing.studio.base.composed.ParagraphSegment;
import hue.captains.singapura.js.homing.studio.base.composed.RelationSegment;
import hue.captains.singapura.js.homing.studio.base.composed.Segment;
import hue.captains.singapura.js.homing.studio.base.composed.SvgSegment;
import hue.captains.singapura.js.homing.studio.base.composed.TableSegment;
import hue.captains.singapura.js.homing.studio.base.composed.TextSegment;
import hue.captains.singapura.js.homing.studio.base.composed.TypedCodeSegment;
import hue.captains.singapura.js.homing.studio.base.composed.UnorderedListSegment;
import hue.captains.singapura.js.homing.studio.base.composed.text.Line;
import hue.captains.singapura.js.homing.studio.base.table.TableData;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * A rigid or composed doc's segments as parts of a leaf.
 *
 * <table>
 *   <caption>Segments and their parts</caption>
 *   <tr><th>Segment</th><th>Part</th></tr>
 *   <tr><td>MarkdownSegment</td><td>its markdown split as a section's is - code, tables, prose - its headings kept in the prose</td></tr>
 *   <tr><td>TextSegment, ParagraphSegment</td><td>prose: their text is markdown</td></tr>
 *   <tr><td>CodeSegment, TypedCodeSegment</td><td>code, with its language</td></tr>
 *   <tr><td>RelationSegment, TableSegment</td><td>a table, its caption with it</td></tr>
 *   <tr><td>SvgSegment</td><td>an image, SVG</td></tr>
 *   <tr><td>ImageSegment</td><td>an image, a raster</td></tr>
 *   <tr><td>a list whose items are all prose</td><td>prose, written as a markdown list</td></tr>
 *   <tr><td>a list holding other items</td><td>its items, as parts in order</td></tr>
 *   <tr><td>ComposedSegment</td><td>prose naming the doc it holds: shown as a link, or grafted in, is an open question</td></tr>
 *   <tr><td>EmbeddedSegment</td><td>prose naming what is embedded: not shown yet, an open question</td></tr>
 * </table>
 */
public final class SegmentParts {

    private SegmentParts() {}

    /** The parts of these segments, in order. */
    public static List<Part> of(List<? extends Segment> segments) {
        var out = new ArrayList<Part>();
        for (Segment s : segments) out.addAll(of(s));
        return List.copyOf(out);
    }

    /** A segment's parts. */
    public static List<Part> of(Segment segment) {
        return switch (segment) {
            case MarkdownSegment m -> MarkdownTrees.parts(m.body());
            case TextSegment t -> prose(t.body());
            case ParagraphSegment p -> prose(p.text());
            case CodeSegment c -> List.of(new Part.Code(c.language().toLowerCase(Locale.ROOT), c.body()));
            case TypedCodeSegment c -> List.of(new Part.Code(c.language().tag().toLowerCase(Locale.ROOT), c.body()));
            case RelationSegment r -> List.of(relation(r));
            case TableSegment t -> List.of(table(t.doc().data(), t.resolvedCaption()));
            case SvgSegment s -> List.of(new Part.Image(s.doc().contents(), "", "", s.doc().title(), s.resolvedCaption()));
            case ImageSegment i -> List.of(new Part.Image("", i.doc().resourcePath(), i.doc().mimeType(), i.doc().alt(), i.resolvedCaption()));
            case UnorderedListSegment u -> list(u.items(), false);
            case OrderedListSegment o -> list(o.items(), true);
            case ComposedSegment c -> prose("*" + c.resolvedCaption() + "* - a doc of its own, held here; it is not shown in this view yet.");
            case EmbeddedSegment e -> prose("*" + e.resolvedCaption() + "* - an app embedded here; it is not shown in this view yet.");
        };
    }

    /** The title a segment carries, when it has one: what starts a node of a composed doc. */
    public static Optional<String> title(Segment segment) {
        return switch (segment) {
            case MarkdownSegment m -> m.title();
            case TextSegment t -> t.title();
            case CodeSegment c -> c.title();
            case TypedCodeSegment c -> c.title();
            default -> Optional.empty();
        };
    }

    private static List<Part> prose(String text) { return text.isBlank() ? List.of() : List.of(new Part.Prose(text.strip())); }

    /** A list: prose written as a markdown list when every item is prose; else its items, in order. */
    private static List<Part> list(List<Listable> items, boolean ordered) {
        boolean prose = items.stream().allMatch(i -> i instanceof MarkdownSegment || i instanceof TextSegment || i instanceof ParagraphSegment);
        if (!prose) return of(items);
        var b = new StringBuilder();
        for (int n = 0; n < items.size(); n++) {
            String text = switch (items.get(n)) {
                case MarkdownSegment m -> m.body();
                case TextSegment t -> t.body();
                case ParagraphSegment p -> p.text();
                default -> throw new IllegalStateException("prose items only");
            };
            String marker = ordered ? (n + 1) + ". " : "- ";
            String indent = " ".repeat(marker.length());
            if (n > 0) b.append('\n');
            b.append(marker).append(text.strip().replace("\n", "\n" + indent));
        }
        return List.of(new Part.Prose(b.toString()));
    }

    /** A relation: its headers the columns - their alignment the header's articulation - its rows, each cell articulated as it says. */
    private static Part.Table relation(RelationSegment r) {
        var columns = new ArrayList<Part.Column>();
        for (int c = 0; c < r.headers().size(); c++) columns.add(new Part.Column(r.headers().get(c), align(articulation(r, -1, c).align())));
        var rows = new ArrayList<Part.Row>();
        for (int row = 0; row < r.rows().size(); row++) {
            var cells = new ArrayList<Part.Cell>();
            List<String> texts = r.rows().get(row);
            for (int c = 0; c < texts.size(); c++) {
                Articulation a = articulation(r, row, c);
                cells.add(new Part.Cell(texts.get(c), 1, 1, a.badge().map(SegmentParts::badge).orElse(""), align(a.align()),
                        a.emphasis().map(e -> e.name().toLowerCase(Locale.ROOT)).orElse("")));
            }
            rows.add(new Part.Row(cells));
        }
        return new Part.Table(columns, rows, r.caption().map(Line::raw).orElse(""));
    }

    private static Articulation articulation(RelationSegment r, int row, int col) {
        for (ArticulatedCell a : r.articulations().cells()) if (a.row() == row && a.col() == col) return a.articulation();
        return new Articulation(Optional.empty(), Optional.empty(), Optional.empty());
    }

    /** A table's data: its header cells the columns - or as many untitled columns as its widest row, when it has none. */
    static Part.Table table(TableData data, String caption) {
        var columns = new ArrayList<Part.Column>();
        if (!data.headers().isEmpty()) {
            for (TableData.Cell h : data.headers()) columns.add(new Part.Column(h.text(), align(Optional.ofNullable(h.align()))));
        } else {
            int widest = data.rows().stream().mapToInt(List::size).max().orElse(0);
            for (int c = 0; c < widest; c++) columns.add(new Part.Column("", ""));
        }
        var rows = new ArrayList<Part.Row>();
        for (List<TableData.Cell> row : data.rows()) {
            var cells = new ArrayList<Part.Cell>();
            for (TableData.Cell c : row) {
                cells.add(new Part.Cell(c.text(), c.colspan(), c.rowspan(), c.badge() == null ? "" : badge(c.badge()), align(Optional.ofNullable(c.align())), ""));
            }
            rows.add(new Part.Row(cells));
        }
        return new Part.Table(columns, rows, caption);
    }

    private static String align(Optional<TableData.Align> a) { return a.map(x -> x.name().toLowerCase(Locale.ROOT)).orElse(""); }

    private static String badge(TableData.Badge b) { return b.name().toLowerCase(Locale.ROOT); }
}
