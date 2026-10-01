package hue.captains.singapura.js.homing.docview.site;

import hue.captains.singapura.js.homing.docview.tree.DocPayload;
import hue.captains.singapura.js.homing.docview.tree.DocTree;
import hue.captains.singapura.js.homing.docview.tree.DocTrees;
import hue.captains.singapura.js.homing.site.Path;
import hue.captains.singapura.js.homing.site.catalogue.CatalogueRouter;
import hue.captains.singapura.js.homing.site.catalogue.Resolution;
import hue.captains.singapura.js.homing.studio.base.Doc;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The docs a site places, as their routes read them: a doc found by its authentic path - the
 * leaf that places it, whose page holds it - and its tree and payload, built on the first
 * request and kept, since a doc cannot change while the server runs. Its references are resolved
 * against where the site's docs are read ({@link DocPlaces}), read off the tree once. A doc whose
 * tree cannot be built is kept as that, with the reason, and the other docs are untouched.
 */
public final class DocViews {

    private final CatalogueRouter router;
    private final ConcurrentHashMap<String, Built> built = new ConcurrentHashMap<>();
    private volatile DocPlaces places;

    public DocViews(CatalogueRouter router) { this.router = Objects.requireNonNull(router, "DocViews.router"); }

    /**
     * A doc, built: at its authentic path, its tree and its payload - or why it has none.
     *
     * @param failed empty when it was built; else why not, and the tree and payload are null
     */
    public record Built(String doc, DocTree tree, String payload, String failed) {
        public boolean ok() { return failed.isEmpty(); }
    }

    /** The doc at a path, built: empty when no leaf there holds a doc. */
    public Optional<Built> at(String path) {
        if (path == null || path.isBlank()) return Optional.empty();
        Path parsed;
        try { parsed = Path.parse(path); } catch (RuntimeException e) { return Optional.empty(); }
        return router.resolution(parsed).flatMap(r -> r instanceof Resolution.AtLeaf leaf && leaf.leaf().page() instanceof HoldsDoc h
                ? Optional.of(built.computeIfAbsent(router.hrefOf(leaf.leaf().page()).orElse(path), at -> build(at, h.doc())))
                : Optional.empty());
    }

    /** Where the site's docs are read: read off its tree the first time a doc is built, and kept. */
    public DocPlaces places() {
        DocPlaces p = places;
        if (p == null) { p = DocPlaces.of(router); places = p; }
        return p;
    }

    private Built build(String at, Doc doc) {
        try {
            DocTree tree = DocTrees.of(doc);
            return new Built(at, tree, DocPayload.json(tree, at, DocReferences.of(doc, tree, places())), "");
        } catch (RuntimeException e) {
            return new Built(at, null, null, e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }
}
