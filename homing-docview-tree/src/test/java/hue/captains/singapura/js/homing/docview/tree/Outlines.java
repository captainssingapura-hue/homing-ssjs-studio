package hue.captains.singapura.js.homing.docview.tree;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/** A tree as lines a test can read whole: each node's path, then its leaf's parts - code with its language. */
final class Outlines {

    private Outlines() {}

    static String of(DocTree tree) {
        var out = new ArrayList<String>();
        walk(tree.root(), "", out);
        return String.join("\n", out);
    }

    private static void walk(DocTree.Node node, String path, List<String> out) {
        String parts = node.leaf().stream().map(Outlines::part).collect(Collectors.joining(" "));
        out.add((path.isEmpty() ? "/" : path) + (parts.isEmpty() ? "" : "  " + parts));
        for (DocTree.Node child : node.children()) walk(child, DocTree.pathOf(path, child), out);
    }

    private static String part(Part p) {
        return switch (p) {
            case Part.Prose x -> "P";
            case Part.Code c -> "C" + (c.language().isEmpty() ? "" : "(" + c.language() + ")");
            case Part.Table t -> "T" + t.columns().size() + "x" + t.rows().size();
            case Part.Image i -> i.raster() ? "I(raster)" : "I(svg)";
        };
    }
}
