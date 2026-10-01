package hue.captains.singapura.js.homing.docview.tree;

import hue.captains.singapura.js.homing.workspace.groups.core.models.ArrangedWidget;
import hue.captains.singapura.js.homing.workspace.groups.core.models.Arrangement;
import hue.captains.singapura.js.homing.workspace.groups.core.models.TreePlacement;
import hue.captains.singapura.js.homing.workspace.groups.core.models.WidgetRef;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;

/**
 * A doc as a page loads it, in one: its tree - every heading, its name and label, and the parts
 * of its leaf, each by its type and its key - from which the page makes its arrangement itself;
 * and the content of every plain part - prose, code, a table - each with the params it is asked
 * by, as a content party carries them ({@code [{ name, value }]}, in the order of their names).
 * An image is not in it: it is fetched when it is wanted, by its key ({@link #content}).
 *
 * <pre>{@code
 * { "doc": "/reference/markdown",
 *   "tree": { "name": "", "label": { "text": "…", "runs": [] }, "leaf": [{ "type": "prose", "key": ":0" }],
 *             "children": [ { "name": "prose-only", … }, … ] },
 *   "items": [ { "type": "prose", "params": [{ "name": "doc", "value": "…" }, { "name": "key", "value": ":0" }],
 *                "content": { "text": "…" } }, … ],
 *   "references": [ { "name": "rigid", "kind": "doc", "title": "…", "summary": "…", "to": "/reference/rigid",
 *                     "citedIn": ["citations"] }, … ] }
 * }</pre>
 * The references are the site's to resolve ({@link DocRef}): a doc's tree knows what it declares
 * and cites, and the site where every doc is placed.
 */
public final class DocPayload {

    private DocPayload() {}

    /** How a raster's address is made from the doc's address and the part's key: the route that serves it. */
    public static final BiFunction<String, String, String> NO_RASTERS = (doc, key) -> "";

    /** The payload of a doc's tree, at its address: no references. */
    public static String json(DocTree tree, String doc) { return json(tree, doc, List.of()); }

    /** The payload of a doc's tree, at its address, with its references as the site resolved them. */
    public static String json(DocTree tree, String doc, List<DocRef> references) {
        var items = new ArrayList<String>();
        for (DocTree.Spot s : tree.spots()) {
            if (s.part() instanceof Part.Image) continue;
            items.add(Json.obj(ordered("type", Json.str(s.part().type()), "params", params(doc, s.key()), "content", content(s.part(), doc, s.key(), NO_RASTERS))));
        }
        return Json.obj(ordered("doc", Json.str(doc), "tree", tree(tree.root(), ""), "items", "[" + String.join(",", items) + "]",
                "references", Json.arr(references, DocPayload::reference)));
    }

    /** A reference as the page reads it: { name, kind, title, summary, to, citedIn }. */
    private static String reference(DocRef r) {
        return Json.obj(ordered("name", Json.str(r.name()), "kind", Json.str(r.kind()), "title", Json.str(r.title()),
                "summary", Json.str(r.summary()), "to", Json.str(r.to()), "citedIn", Json.arr(r.citedIn(), Json::str)));
    }

    /** A node of the tree as the page reads it: its name, its label, its leaf's parts by type and key, its children. */
    private static String tree(DocTree.Node n, String path) {
        var leaf = new ArrayList<String>();
        for (int i = 0; i < n.leaf().size(); i++) {
            leaf.add(Json.obj(ordered("type", Json.str(n.leaf().get(i).type()), "key", Json.str(path + ":" + i))));
        }
        return Json.obj(ordered("name", Json.str(n.name().map(TreePlacement.Name::value).orElse("")), "label", label(n.label()),
                "leaf", "[" + String.join(",", leaf) + "]", "children", Json.arr(n.children(), c -> tree(c, DocTree.pathOf(path, c)))));
    }

    /** One part's content by its key, as JSON - a raster's address made as {@code src} says; empty when the tree has no such part. */
    public static Optional<String> content(DocTree tree, String doc, String key, BiFunction<String, String, String> src) {
        return tree.part(key).map(p -> Json.obj(ordered("type", Json.str(p.type()), "params", params(doc, key), "content", content(p, doc, key, src))));
    }

    /**
     * The arrangement Java makes of a tree, as the tree layout reads it: what the page makes for itself
     * from the payload's tree - a page's and Java's are held to be the same.
     */
    public static String arrangement(Arrangement<DocViewSpec, TreePlacement> a) {
        var widgets = new LinkedHashMap<String, String>();
        for (ArrangedWidget w : a.widgets()) {
            var params = new LinkedHashMap<String, String>();
            w.params().forEach((k, v) -> params.put(k, Json.str(v)));
            widgets.put(w.ref().value(), Json.obj(ordered("kind", Json.str(w.kind().value()), "params", Json.obj(params))));
        }
        return Json.obj(ordered("engine", Json.str(a.engine().value()), "workspace", Json.str(a.workspace().workspaceKind().value()),
                "widgets", Json.obj(widgets), "root", node(a.placement().root())));
    }

    private static String node(TreePlacement.Node n) {
        return Json.obj(ordered("name", Json.str(n.name().map(TreePlacement.Name::value).orElse("")), "label", label(n.label()),
                "leaf", Json.arr(n.leaf(), (WidgetRef r) -> Json.str(r.value())), "children", Json.arr(n.children(), DocPayload::node)));
    }

    private static String label(TreePlacement.Label l) {
        return Json.obj(ordered("text", Json.str(l.text()), "runs", Json.arr(l.runs(), r -> Json.obj(ordered("kind", Json.str(kind(r)), "text", Json.str(r.text()))))));
    }

    private static String kind(TreePlacement.Run r) {
        return switch (r) {
            case TreePlacement.Run.Text t -> "text";
            case TreePlacement.Run.Code c -> "code";
            case TreePlacement.Run.Strong s -> "strong";
            case TreePlacement.Run.Emphasis e -> "emphasis";
        };
    }

    /** A widget's params as a party carries them: in the order of their names. */
    static String params(String doc, String key) {
        return "[" + Json.obj(ordered("name", Json.str(DocArrangements.DOC), "value", Json.str(doc))) + ","
                + Json.obj(ordered("name", Json.str(DocArrangements.KEY), "value", Json.str(key))) + "]";
    }

    /** A part's content, of its type's shape. */
    static String content(Part part, String doc, String key, BiFunction<String, String, String> src) {
        return switch (part) {
            case Part.Prose p -> Json.obj(ordered("text", Json.str(p.text())));
            case Part.Code c -> Json.obj(ordered("language", Json.str(c.language()), "source", Json.str(c.source())));
            case Part.Table t -> Json.obj(ordered(
                    "columns", Json.arr(t.columns(), c -> Json.obj(ordered("title", Json.str(c.title()), "align", Json.str(c.align())))),
                    "rows", Json.arr(t.rows(), DocPayload::row),
                    "caption", Json.str(t.caption())));
            case Part.Image i -> Json.obj(ordered("svg", Json.str(i.svg()), "src", Json.str(i.raster() ? src.apply(doc, key) : ""),
                    "alt", Json.str(i.alt()), "caption", Json.str(i.caption())));
        };
    }

    private static String row(Part.Row r) { return Json.obj(ordered("cells", Json.arr(r.cells(), DocPayload::cell))); }

    private static String cell(Part.Cell c) {
        return Json.obj(ordered("text", Json.str(c.text()), "colSpan", Json.num(c.colSpan()), "rowSpan", Json.num(c.rowSpan()),
                "badge", Json.str(c.badge()), "align", Json.str(c.align()), "emphasis", Json.str(c.emphasis())));
    }

    private static Map<String, String> ordered(String... kv) {
        var m = new LinkedHashMap<String, String>();
        for (int i = 0; i < kv.length; i += 2) m.put(kv[i], kv[i + 1]);
        return m;
    }

    /** Every part's key and type, in reading order: what the payload and the content route answer for. */
    public static List<String> keys(DocTree tree) { return tree.spots().stream().map(DocTree.Spot::key).toList(); }
}
