package hue.captains.singapura.js.homing.conformance.workbench.feeds;

import hue.captains.singapura.js.homing.conformance.rules.CrateClosure;
import hue.captains.singapura.js.homing.core.Crate;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * {@code GET /conformance/crate-graph} - the crates and the crates they require, as a mermaid
 * flowchart: every crate of the closure a node, its module count under its name, an edge for
 * each requirement. The studio's own crates are drawn as the design draws a node; the crates
 * they require, with a dashed edge of their own - no colour is said here, so the diagram wears
 * the design's, as every diagram DocView draws does.
 */
public final class CrateGraphFeed extends WorkbenchFeed {

    public static final String ROUTE = "/conformance/crate-graph";

    private final List<Crate> topLevel;

    public CrateGraphFeed(List<Crate> topLevel) {
        super("conformance-crate-graph");
        this.topLevel = List.copyOf(Objects.requireNonNull(topLevel, "CrateGraphFeed.topLevel"));
    }

    @Override public String route() { return ROUTE; }

    @Override
    protected Body answer(Query query) {
        Set<String> owned = topLevel.stream().map(Crate::name).collect(Collectors.toSet());
        return Body.text(mermaid(CrateClosure.of(topLevel), owned));
    }

    /** The flowchart: the crates, their requirements, the ones not owned dashed. */
    public static String mermaid(List<Crate> crates, Set<String> owned) {
        var sb = new StringBuilder("flowchart TD\n");
        for (Crate c : crates) {
            int n = c.entries().size();
            sb.append("  ").append(id(c.name())).append("[\"").append(c.name()).append("<br/>")
              .append(n).append(n == 1 ? " module" : " modules").append("\"]\n");
        }
        for (Crate c : crates)
            for (Crate r : c.requires()) sb.append("  ").append(id(c.name())).append(" --> ").append(id(r.name())).append('\n');
        String external = crates.stream().filter(c -> !owned.contains(c.name())).map(c -> id(c.name())).collect(Collectors.joining(","));
        if (!external.isEmpty()) sb.append("  classDef external stroke-dasharray:4 3;\n  class ").append(external).append(" external;\n");
        return sb.toString();
    }

    /** A mermaid node id is identifier-safe; a crate's name carries dashes. */
    private static String id(String crateName) { return crateName.replaceAll("[^A-Za-z0-9]", "_"); }
}
