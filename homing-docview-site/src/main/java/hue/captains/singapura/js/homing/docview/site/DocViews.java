package hue.captains.singapura.js.homing.docview.site;

import hue.captains.singapura.js.homing.docview.tree.DocPayload;
import hue.captains.singapura.js.homing.docview.tree.DocRef;
import hue.captains.singapura.js.homing.docview.tree.DocTree;
import hue.captains.singapura.js.homing.docview.tree.DocTrees;
import hue.captains.singapura.js.homing.planview.tree.PlanPayload;
import hue.captains.singapura.js.homing.planview.tree.PlanTree;
import hue.captains.singapura.js.homing.site.Navigable;
import hue.captains.singapura.js.homing.site.Path;
import hue.captains.singapura.js.homing.site.catalogue.CatalogueRouter;
import hue.captains.singapura.js.homing.site.catalogue.Resolution;
import hue.captains.singapura.js.homing.studio.base.Doc;
import hue.captains.singapura.js.homing.studio.base.tracker.Plan;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * The docs and plans a site places, as their routes read them: one found by its authentic path -
 * the leaf that places it, whose page holds it - and its tree and payload, built on the first
 * request and kept, since neither can change while the server runs. A doc's references, and the
 * docs a plan names, are resolved against where the site's docs are read ({@link DocPlaces}), read
 * off the tree once. One whose tree cannot be built is kept as that, with the reason, and the
 * others are untouched.
 */
public final class DocViews {

    private final CatalogueRouter router;
    private final ConcurrentHashMap<String, Built> built = new ConcurrentHashMap<>();
    private volatile DocPlaces places;

    public DocViews(CatalogueRouter router) { this.router = Objects.requireNonNull(router, "DocViews.router"); }

    /**
     * A doc or a plan, built: at its authentic path, its payload - or why it has none - and a
     * doc's tree, what its heavy parts are fetched from.
     *
     * @param tree   the doc's tree; null for a plan, which has no part fetched apart, and when it failed
     * @param failed empty when it was built; else why not, and the tree and payload are null
     */
    public record Built(String doc, DocTree tree, String payload, String failed) {
        public boolean ok() { return failed.isEmpty(); }
    }

    /** What is held at a path, built: empty when no leaf there holds a doc or a plan. */
    public Optional<Built> at(String path) {
        if (path == null || path.isBlank()) return Optional.empty();
        Path parsed;
        try { parsed = Path.parse(path); } catch (RuntimeException e) { return Optional.empty(); }
        return router.resolution(parsed).flatMap(r -> r instanceof Resolution.AtLeaf leaf ? held(leaf.leaf().page(), path) : Optional.empty());
    }

    private Optional<Built> held(Navigable page, String path) {
        String at = router.hrefOf(page).orElse(path);
        if (page instanceof HoldsDoc h) return Optional.of(built.computeIfAbsent(at, a -> build(a, h.doc())));
        if (page instanceof HoldsPlan h) return Optional.of(built.computeIfAbsent(at, a -> build(a, h.plan())));
        return Optional.empty();
    }

    /** Where the site's docs are read: read off its tree the first time a doc is built, and kept. */
    public DocPlaces places() {
        DocPlaces p = places;
        if (p == null) { p = DocPlaces.of(router); places = p; }
        return p;
    }

    private Built build(String at, Doc doc) {
        return attempt(at, () -> {
            DocTree tree = DocTrees.of(doc);
            return new Built(at, tree, DocPayload.json(tree, at, DocReferences.of(doc, tree, places())), "");
        });
    }

    private Built build(String at, Plan plan) {
        return attempt(at, () -> new Built(at, null, PlanPayload.json(PlanTree.of(plan), at, this::named), ""));
    }

    /** A doc a plan names, by its id: where the site reads it - empty when no catalogue places it to be read. */
    private Optional<DocRef> named(String id) {
        return places().docOf(id).flatMap(d -> places().pathOf(d).map(to -> new DocRef(id, DocRef.DOC, d.title(), d.summary(), to, List.of())));
    }

    private static Built attempt(String at, Supplier<Built> build) {
        try {
            return build.get();
        } catch (RuntimeException e) {
            return new Built(at, null, null, e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }
}
