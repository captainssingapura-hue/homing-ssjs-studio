package hue.captains.singapura.js.homing.docview.tree;

import java.util.List;
import java.util.Objects;

/**
 * One of a doc's references, resolved for the page: its name, as the doc cites it; what it is;
 * the title and summary of what it names; where it goes - an authentic path, an address - and
 * the sections that cite it. Plain data: resolving it is the site's, which knows where docs are
 * placed; the tree knows only what the doc declares and cites.
 *
 * @param kind     {@link #DOC} - a doc placed on the site, {@code to} its authentic path; {@link #UNPLACED} - a doc no
 *                 catalogue places, {@code to} empty; {@link #EXTERNAL} - an address off the site; {@link #IMAGE} - an
 *                 image the doc ships, not shown yet
 * @param to       where it goes: an authentic path, an address, or empty
 * @param citedIn  the sections citing it, by path - the root's "" - each once, in reading order; empty when it is declared
 *                 and never cited
 */
public record DocRef(String name, String kind, String title, String summary, String to, List<String> citedIn) {

    public static final String DOC = "doc";
    public static final String UNPLACED = "unplaced";
    public static final String EXTERNAL = "external";
    public static final String IMAGE = "image";

    public DocRef {
        Objects.requireNonNull(name, "DocRef.name");
        if (!List.of(DOC, UNPLACED, EXTERNAL, IMAGE).contains(kind)) throw new IllegalArgumentException("a reference is a doc, unplaced, external or an image - not " + kind);
        title = title == null ? "" : title;
        summary = summary == null ? "" : summary;
        to = to == null ? "" : to;
        citedIn = List.copyOf(citedIn);
    }
}
