package hue.captains.singapura.js.homing.docview.tree;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Where a doc links within itself: every {@code [label](#path)} in its parts that is not a
 * citation ({@code #ref:}), with the section it is written in - read off the tree as citations are,
 * code quoting, not linking. And the law a doc keeps: a link within it is a section's full path,
 * as the page addresses a section - the path from the root, every heading above it named.
 */
public final class SectionLinks {

    private SectionLinks() {}

    /** A link within a doc, as markdown writes one: {@code #}, then not {@code ref:}. */
    static final Pattern LINK = Pattern.compile("\\]\\(#(?!ref:)([^)\\s]+)\\)");

    /**
     * A link within a doc.
     *
     * @param in the path of the section it is written in - the root's ""
     * @param to the path it links to, as written
     */
    public record Link(String in, String to) {}

    /**
     * A link to no section, and what it may mean: the one section whose path ends with what is
     * written - the full path to write instead - or none, when no section's does, or more than one.
     */
    public record Miss(String in, String to, Optional<String> meant) {}

    /** Every link within the doc, in reading order. */
    public static List<Link> of(DocTree tree) {
        var out = new ArrayList<Link>();
        for (DocTree.Spot s : tree.spots()) {
            for (String text : Citations.texts(s.part())) {
                Matcher m = LINK.matcher(Citations.withoutCode(text));
                while (m.find()) out.add(new Link(s.path(), m.group(1)));
            }
        }
        return List.copyOf(out);
    }

    /** The links within the doc that are no section's full path - each with the path it may mean. */
    public static List<Miss> unresolved(DocTree tree) {
        List<String> paths = tree.paths();
        var out = new ArrayList<Miss>();
        for (Link link : of(tree)) {
            if (paths.contains(link.to())) continue;
            List<String> ending = paths.stream().filter(p -> p.endsWith("/" + link.to())).toList();
            out.add(new Miss(link.in(), link.to(), ending.size() == 1 ? Optional.of(ending.get(0)) : Optional.empty()));
        }
        return List.copyOf(out);
    }
}
