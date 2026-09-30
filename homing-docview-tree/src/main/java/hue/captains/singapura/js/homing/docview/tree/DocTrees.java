package hue.captains.singapura.js.homing.docview.tree;

import hue.captains.singapura.js.homing.studio.base.Doc;
import hue.captains.singapura.js.homing.studio.base.MarkdownSource;
import hue.captains.singapura.js.homing.studio.base.ProxyDoc;
import hue.captains.singapura.js.homing.studio.base.composed.ComposedDoc;
import hue.captains.singapura.js.homing.studio.base.composed.ComposedLeaf;
import hue.captains.singapura.js.homing.studio.base.composed.DocTreeV2;
import hue.captains.singapura.js.homing.studio.base.composed.LeafContent;
import hue.captains.singapura.js.homing.studio.base.composed.Segment;
import hue.captains.singapura.js.homing.studio.base.composed.text.Line;
import hue.captains.singapura.js.homing.studio.base.rigid.DocNode;
import hue.captains.singapura.js.homing.studio.base.rigid.RigidDoc;
import hue.captains.singapura.js.homing.studio.base.rigid.RigidDocV2;
import hue.captains.singapura.js.homing.tree.DisplayLabel;
import hue.captains.singapura.js.homing.tree.NormalizedNode;
import hue.captains.singapura.js.homing.tree.dims.NameValue;
import hue.captains.singapura.js.homing.workspace.groups.core.models.TreePlacement.Label;
import hue.captains.singapura.js.homing.workspace.groups.core.models.TreePlacement.Name;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * A doc made into its tree - one entry point per kind the view takes, so a doc of another kind is
 * a compile error where it is made, and {@link #of(Doc)} for a doc known only as a doc.
 *
 * <ul>
 *   <li><b>Markdown</b> ({@link MarkdownSource}): its headings, by {@link MarkdownTrees}.</li>
 *   <li><b>A rigid doc</b> ({@link RigidDoc}): each node a node - its title the label, named by the
 *       naming rules, since its nodes carry no name of their own; its caption and segments its
 *       leaf.</li>
 *   <li><b>A rigid doc by names</b> ({@link RigidDocV2}): each node a node - its authored name used
 *       as written, its label the node's, its caption and segments its leaf.</li>
 *   <li><b>A composed doc</b> ({@link ComposedDoc}): one level deep - each titled segment starts a
 *       node; an untitled one belongs to the node before it, or to the root.</li>
 *   <li><b>A proxy</b>: the doc it stands for, under the proxy's title.</li>
 * </ul>
 */
public final class DocTrees {

    private DocTrees() {}

    public static DocTree of(MarkdownSource doc) { return MarkdownTrees.of(doc.title(), doc.contents()); }

    public static DocTree of(RigidDoc doc) { return new DocTree(rigid(Optional.empty(), doc.root())); }

    public static DocTree of(RigidDocV2 doc) {
        DocTreeV2 tree = doc.toDocTreeV2();
        return new DocTree(named(tree, tree.structure(), "", Optional.empty(), doc.title()));
    }

    public static DocTree of(ComposedDoc doc) {
        var root = new ArrayList<Part>();
        var children = new ArrayList<DocTree.Node>();
        var siblings = new NodeNames.Siblings();
        Optional<Name> name = Optional.empty();
        Label label = null;
        var leaf = root;
        for (Segment s : doc.segments()) {
            Optional<String> title = SegmentParts.title(s);
            if (title.isPresent()) {
                if (label != null) children.add(new DocTree.Node(name, label, leaf, List.of()));
                label = HeadingLabels.of(title.get());
                name = Optional.of(siblings.take(NodeNames.of(label.text())));
                leaf = new ArrayList<>();
            }
            leaf.addAll(SegmentParts.of(s));
        }
        if (label != null) children.add(new DocTree.Node(name, label, leaf, List.of()));
        return new DocTree(new DocTree.Node(Optional.empty(), HeadingLabels.of(doc.title()), root, children));
    }

    /** A doc known only as a doc: its kind's tree; refused, saying so, for a kind the view does not take. */
    public static DocTree of(Doc doc) {
        return switch (doc) {
            case MarkdownSource m -> of(m);
            case RigidDoc r -> of(r);
            case RigidDocV2 r -> of(r);
            case ComposedDoc c -> of(c);
            case ProxyDoc p -> retitled(of(p.target()), p.title());
            default -> throw new IllegalArgumentException("DocView takes markdown, rigid and composed docs - not " + doc.getClass().getName()
                    + " (" + doc.kind() + ")");
        };
    }

    private static DocTree retitled(DocTree tree, String title) {
        var r = tree.root();
        return new DocTree(new DocTree.Node(r.name(), HeadingLabels.of(title), r.leaf(), r.children()));
    }

    /** A rigid node: its caption, then its segments, as its leaf; its children named by their titles. */
    private static DocTree.Node rigid(Optional<Name> name, DocNode node) {
        var leaf = new ArrayList<Part>();
        node.caption().map(Line::raw).filter(c -> !c.isBlank()).ifPresent(c -> leaf.add(new Part.Prose(c)));
        leaf.addAll(SegmentParts.of(node.content()));
        var siblings = new NodeNames.Siblings();
        var children = new ArrayList<DocTree.Node>();
        for (DocNode child : node.children()) {
            children.add(rigid(Optional.of(siblings.take(NodeNames.of(child.title().text()))), child));
        }
        return new DocTree.Node(name, HeadingLabels.of(node.title().text()), leaf, children);
    }

    /** A node of a tree by names: its authored name, its label, its content by its path. */
    private static DocTree.Node named(DocTreeV2 tree, NormalizedNode node, String path, Optional<Name> name, String rootTitle) {
        String text = node.dimensions().get(DisplayLabel.INSTANCE) instanceof NameValue v ? v.text() : node.segment().value();
        Label label = HeadingLabels.of(path.isEmpty() ? rootTitle : text);
        var leaf = new ArrayList<Part>();
        tree.providerAt(path).map(p -> p.content()).ifPresent(c -> leaf.addAll(content(c)));
        var siblings = new NodeNames.Siblings();
        var children = new ArrayList<DocTree.Node>();
        for (NormalizedNode child : node.children()) {
            String childPath = path.isEmpty() ? child.segment().value() : path + "/" + child.segment().value();
            children.add(named(tree, child, childPath, Optional.of(siblings.take(child.segment().value())), rootTitle));
        }
        return new DocTree.Node(name, label, leaf, children);
    }

    private static List<Part> content(LeafContent content) {
        var out = new ArrayList<Part>();
        if (content instanceof ComposedLeaf leaf) {
            leaf.caption().map(Line::raw).filter(c -> !c.isBlank()).ifPresent(c -> out.add(new Part.Prose(c)));
            out.addAll(SegmentParts.of(leaf.contents()));
        }
        return out;
    }
}
