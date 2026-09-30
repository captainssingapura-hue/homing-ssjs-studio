package hue.captains.singapura.js.homing.docview.tree;

import hue.captains.singapura.js.homing.workspace.groups.core.models.TreePlacement.Label;
import hue.captains.singapura.js.homing.workspace.groups.core.models.TreePlacement.Name;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * A doc as a tree of named headings - the first stage of viewing it, built headlessly, once.
 * Each node has at most one leaf: its own content, a list of {@link Part}s in the order they
 * are read, before its named children. The root is the doc itself; its leaf is the doc's
 * introduction.
 *
 * <p>A part is where it is by its node's path and its position in the node's leaf:
 * {@link Spot}, {@code design/keys:1} - the key its content is asked by.</p>
 *
 * @param root the doc: no name, its label the doc's title
 */
public record DocTree(Node root) {

    public DocTree { Objects.requireNonNull(root, "DocTree.root"); }

    /** A heading: its name in the path - none for the root - its label, its leaf, its named children. */
    public record Node(Optional<Name> name, Label label, List<Part> leaf, List<Node> children) {
        public Node {
            Objects.requireNonNull(name, "Node.name");
            Objects.requireNonNull(label, "Node.label");
            leaf = List.copyOf(Objects.requireNonNull(leaf, "Node.leaf"));
            children = List.copyOf(Objects.requireNonNull(children, "Node.children"));
        }
    }

    /** A part of a leaf, where it is: its node's path, its position in the leaf. */
    public record Spot(String path, int position, Part part) {
        /** Its key: {@code design/keys:1}; in the root's leaf, {@code :0}. */
        public String key() { return path + ":" + position; }
    }

    /** Every part, in reading order, where it is. */
    public List<Spot> spots() {
        var out = new ArrayList<Spot>();
        walk(root, "", out);
        return List.copyOf(out);
    }

    private static void walk(Node node, String path, List<Spot> out) {
        for (int i = 0; i < node.leaf().size(); i++) out.add(new Spot(path, i, node.leaf().get(i)));
        for (Node child : node.children()) walk(child, pathOf(path, child), out);
    }

    /** The part a key names, if the tree has it. */
    public Optional<Part> part(String key) {
        for (Spot s : spots()) if (s.key().equals(key)) return Optional.of(s.part());
        return Optional.empty();
    }

    /** Every node's path, in reading order, the root's first. */
    public List<String> paths() {
        var out = new ArrayList<String>();
        paths(root, "", out);
        return List.copyOf(out);
    }

    private static void paths(Node node, String path, List<String> out) {
        out.add(path);
        for (Node child : node.children()) paths(child, pathOf(path, child), out);
    }

    /** The node at a path. */
    public Optional<Node> node(String path) {
        if (path.isEmpty()) return Optional.of(root);
        Node at = root;
        for (String segment : path.split("/", -1)) {
            Optional<Node> next = at.children().stream().filter(c -> c.name().orElseThrow().value().equals(segment)).findFirst();
            if (next.isEmpty()) return Optional.empty();
            at = next.get();
        }
        return Optional.of(at);
    }

    static String pathOf(String parent, Node child) {
        String name = child.name().orElseThrow().value();
        return parent.isEmpty() ? name : parent + "/" + name;
    }
}
