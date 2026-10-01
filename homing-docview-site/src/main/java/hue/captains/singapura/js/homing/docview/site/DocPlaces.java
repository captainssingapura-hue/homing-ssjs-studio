package hue.captains.singapura.js.homing.docview.site;

import hue.captains.singapura.js.homing.site.catalogue.Catalogue;
import hue.captains.singapura.js.homing.site.catalogue.CatalogueRouter;
import hue.captains.singapura.js.homing.site.catalogue.Leaf;
import hue.captains.singapura.js.homing.studio.base.Doc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Where a site's docs are read: every doc a catalogue places on a reading page ({@link
 * HoldsDoc#reads()}), by the doc, with that page's authentic path - what a reference to the doc
 * links to. Read off the site's tree once, in its order: no registry, no id; a doc is found by
 * itself. A doc on more than one reading page is the site's to fix: it has no one path, so it is
 * said ({@link #twice()}) and its first place is its path meanwhile. A doc no catalogue places has
 * none.
 */
public final class DocPlaces {

    private final Map<Doc, List<String>> paths;

    private DocPlaces(Map<Doc, List<String>> paths) { this.paths = paths; }

    /** The reading pages of a site's tree, as its router serves them: each path as {@code hrefOf} writes it, the mount and all. */
    public static DocPlaces of(CatalogueRouter router) {
        Objects.requireNonNull(router, "DocPlaces.router");
        var found = new LinkedHashMap<Doc, List<String>>();
        for (Catalogue<?> c : router.tree().all()) {
            for (Leaf<?> leaf : router.tree().leavesOf(c)) {
                if (!(leaf.page() instanceof HoldsDoc h) || !h.reads()) continue;
                router.hrefOf(leaf.page()).ifPresent(at -> found.computeIfAbsent(h.doc(), d -> new ArrayList<>()).add(at));
            }
        }
        var frozen = new LinkedHashMap<Doc, List<String>>();
        found.forEach((doc, at) -> frozen.put(doc, List.copyOf(at)));
        return new DocPlaces(Collections.unmodifiableMap(frozen));
    }

    /** The doc's authentic path: its reading page's - empty when no catalogue places it to be read. */
    public Optional<String> pathOf(Doc doc) {
        List<String> at = paths.get(doc);
        return at == null || at.isEmpty() ? Optional.empty() : Optional.of(at.get(0));
    }

    /**
     * The doc read on the site whose id this is - as a plan names its execution doc and dossier,
     * by an id written as text - or empty: no doc read here has it, or it is no id. The doc is still
     * found by itself; the id is only how a plan, written before docs had places, says which.
     */
    public Optional<Doc> docOf(String id) {
        UUID uuid;
        try { uuid = UUID.fromString(Objects.requireNonNull(id, "DocPlaces.id").strip()); } catch (IllegalArgumentException e) { return Optional.empty(); }
        return paths.keySet().stream().filter(d -> uuid.equals(d.uuid())).findFirst();
    }

    /** The docs read at more than one page - each with its paths: what a site must fix, since a doc has one authentic path. */
    public Map<Doc, List<String>> twice() {
        var out = new LinkedHashMap<Doc, List<String>>();
        paths.forEach((doc, at) -> { if (at.size() > 1) out.put(doc, at); });
        return Collections.unmodifiableMap(out);
    }

    /** How many docs are read on the site. */
    public int size() { return paths.size(); }
}
