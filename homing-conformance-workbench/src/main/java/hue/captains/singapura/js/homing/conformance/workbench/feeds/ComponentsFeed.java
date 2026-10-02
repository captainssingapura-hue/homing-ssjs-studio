package hue.captains.singapura.js.homing.conformance.workbench.feeds;

import hue.captains.singapura.js.homing.component.ComponentDetails;
import hue.captains.singapura.js.homing.component.ComponentTrees;
import hue.captains.singapura.js.homing.core.Crate;
import hue.captains.singapura.js.homing.tree.NormalizedNode;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * {@code GET /conformance/components} - the components the studio's crates deliver, composed
 * from their closure (every vehicle's catalogue grafted under one root), as the components
 * navigator shows them: root, vehicle, family, component. With {@code ?path=} the one node at
 * that name path - below the root, its segments joined by "/" - and what it is, for the summary:
 * a composition, a vehicle, a catalogue, or a component with its shape, its tag, its module and
 * the crate that ships it.
 *
 * <pre>
 *   tree   { key: "", label, kind, children: [{ key: "&lt;path&gt;", label, kind, children }] }
 *   ?path  { segment, level, kind, label, summary?, shape?, tag?, module?, crate?, path?, components?, vehicles? }
 * </pre>
 */
public final class ComponentsFeed extends WorkbenchFeed {

    public static final String ROUTE = "/conformance/components";

    private final List<Crate> topLevel;

    public ComponentsFeed(List<Crate> topLevel) {
        super("conformance-components");
        this.topLevel = List.copyOf(Objects.requireNonNull(topLevel, "ComponentsFeed.topLevel"));
    }

    @Override public String route() { return ROUTE; }

    @Override
    protected Body answer(Query query) {
        var composed = ComponentTrees.compose("components", topLevel);
        if (!query.asks()) return Body.json(tree(composed, composed.root(), "").encode());
        NormalizedNode node = find(composed.root(), query.path().split("/"), 0);
        if (node == null) throw new NoSuchElementException("no component at " + query.path());
        return Body.json(details(node, composed.detailsOf(node.identity())).encode());
    }

    private static JsonObject tree(ComponentTrees.Composition composed, NormalizedNode node, String path) {
        ComponentDetails d = composed.detailsOf(node.identity());
        var children = new JsonArray();
        for (NormalizedNode k : node.children()) {
            String sub = path.isEmpty() ? k.segment().value() : path + "/" + k.segment().value();
            children.add(tree(composed, k, sub));
        }
        return new JsonObject().put("key", path).put("label", label(node, d)).put("kind", kind(d)).put("children", children);
    }

    /** The node at a name path below the root - the root's own segment is not part of it. */
    private static NormalizedNode find(NormalizedNode node, String[] segments, int i) {
        if (i == segments.length) return node;
        for (NormalizedNode k : node.children()) if (k.segment().value().equals(segments[i])) return find(k, segments, i + 1);
        return null;
    }

    private static String kind(ComponentDetails d) {
        return switch (d) {
            case ComponentDetails.OfComponent c -> "component";
            case ComponentDetails.OfVehicle v -> "vehicle";
            case ComponentDetails.OfCatalogue c -> "catalogue";
            case ComponentDetails.OfComposition r -> "composition";
            case null -> "unknown";
        };
    }

    private static String label(NormalizedNode node, ComponentDetails d) {
        return switch (d) {
            case ComponentDetails.OfComponent c -> c.label();
            case ComponentDetails.OfVehicle v -> v.name();
            case ComponentDetails.OfCatalogue c -> c.name();
            case ComponentDetails.OfComposition r -> r.name();
            case null -> node.segment().value();
        };
    }

    /** One node, typed by what it is. */
    static JsonObject details(NormalizedNode node, ComponentDetails d) {
        var out = new JsonObject().put("segment", node.segment().value()).put("level", node.level().tag()).put("kind", kind(d)).put("label", label(node, d));
        switch (d) {
            case ComponentDetails.OfComponent c -> out.put("shape", c.shape().tag()).put("tag", c.tag()).put("summary", c.summary())
                    .put("module", c.module()).put("crate", c.crate()).put("path", String.join("/", c.path()));
            case ComponentDetails.OfVehicle v -> out.put("summary", v.summary()).put("crate", v.crate()).put("components", v.componentCount());
            case ComponentDetails.OfCatalogue c -> out.put("summary", c.summary()).put("components", c.componentCount());
            case ComponentDetails.OfComposition r -> out.put("vehicles", r.vehicleCount()).put("components", r.componentCount());
            case null -> { }
        }
        return out;
    }
}
