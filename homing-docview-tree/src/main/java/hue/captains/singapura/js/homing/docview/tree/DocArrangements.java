package hue.captains.singapura.js.homing.docview.tree;

import hue.captains.singapura.js.homing.workspace.groups.core.models.ArrangedWidget;
import hue.captains.singapura.js.homing.workspace.groups.core.models.Arrangement;
import hue.captains.singapura.js.homing.workspace.groups.core.models.TreePlacement;
import hue.captains.singapura.js.homing.workspace.groups.core.models.WidgetRef;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * A doc's tree as a tree placement of widgets: each part of a node's leaf a widget of the
 * part's type, its params the doc's address and the part's key - {@code doc=/notes/monday},
 * {@code key=design/keys:1} - and nothing about where it sits. The widgets are named in reading
 * order, {@code w0}, {@code w1}, ….
 */
public final class DocArrangements {

    private DocArrangements() {}

    /** The param naming the doc: its authentic path. */
    public static final String DOC = "doc";

    /** The param naming the part: its node's path, and its position in the node's leaf. */
    public static final String KEY = "key";

    public static Arrangement<DocViewSpec, TreePlacement> of(DocTree tree, String doc) {
        Objects.requireNonNull(tree, "tree");
        Objects.requireNonNull(doc, "doc");
        var widgets = new ArrayList<ArrangedWidget>();
        TreePlacement.Node root = node(tree.root(), "", doc, widgets);
        return new Arrangement<>(DocViewSpec.INSTANCE, widgets, TreePlacement.of(root));
    }

    private static TreePlacement.Node node(DocTree.Node node, String path, String doc, List<ArrangedWidget> widgets) {
        var leaf = new ArrayList<WidgetRef>();
        for (int i = 0; i < node.leaf().size(); i++) {
            Part part = node.leaf().get(i);
            var w = ArrangedWidget.of("w" + widgets.size(), part.type(), Map.of(DOC, doc, KEY, path + ":" + i));
            widgets.add(w);
            leaf.add(w.ref());
        }
        var children = new ArrayList<TreePlacement.Node>();
        for (DocTree.Node child : node.children()) children.add(node(child, DocTree.pathOf(path, child), doc, widgets));
        return new TreePlacement.Node(node.name(), node.label(), leaf, children);
    }
}
