package hue.captains.singapura.js.homing.catalogue.site;

import hue.captains.singapura.js.homing.catalogue.widgets.CatalogueEntriesModule;
import hue.captains.singapura.js.homing.server.EmptyParam;
import hue.captains.singapura.js.homing.server.ResourceNotFound;
import hue.captains.singapura.js.homing.site.Path;
import hue.captains.singapura.js.homing.site.catalogue.Catalogue;
import hue.captains.singapura.js.homing.site.catalogue.CatalogueRouter;
import hue.captains.singapura.js.homing.site.catalogue.CatalogueTree;
import hue.captains.singapura.js.homing.site.catalogue.Leaf;
import hue.captains.singapura.js.homing.site.catalogue.Resolution;
import hue.captains.singapura.tao.http.action.GetAction;
import hue.captains.singapura.tao.http.action.Param;
import hue.captains.singapura.tao.http.action.ParamMarshaller;
import hue.captains.singapura.tao.http.action.TypedContent;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * {@code GET /catalogue/entry?path=<address>} - an entry of the site's catalogue, by its
 * authentic path, as the catalogue widgets read it: a catalogue with what is under it - its
 * catalogues, its own and the trees it grafts alike, then its pages - or a page. Each entry
 * carries the address the router minted for it, and a page how its app says it opens. An
 * address that names no entry is a 404.
 *
 * <pre>
 *   { to, kind: "catalogue" | "page", name, summary, badge, icon, opens: "in-place" | "new-tab",
 *     children: [{ to, kind, name, summary, badge, icon, opens }] }   - a page's are none
 * </pre>
 */
public final class EntryGetAction implements GetAction<RoutingContext, EntryGetAction.Query, EmptyParam.NoHeaders, EntryGetAction.Json> {

    /** Where the widgets read an entry from: their own route, the one they know. */
    public static final String PATH = CatalogueEntriesModule.ROUTE;

    /** The entry's address, as a page of the site has it. */
    public record Query(String path) implements Param._QueryString {}

    /** The answer: JSON. */
    public record Json(String body) implements TypedContent {
        @Override public String contentType() { return "application/json; charset=utf-8"; }
    }

    private final CatalogueRouter router;

    public EntryGetAction(CatalogueRouter router) {
        this.router = Objects.requireNonNull(router, "EntryGetAction.router");
    }

    @Override
    public ParamMarshaller._QueryString<RoutingContext, Query> queryStrMarshaller() {
        return ctx -> new Query(ctx.request().getParam("path"));
    }

    @Override
    public ParamMarshaller._Header<RoutingContext, EmptyParam.NoHeaders> headerMarshaller() {
        return ctx -> new EmptyParam.NoHeaders();
    }

    @Override
    public CompletableFuture<Json> execute(Query query, EmptyParam.NoHeaders headers) {
        String address = query.path() == null || query.path().isBlank() ? "/" : query.path();
        var found = router.resolution(Path.parse(address)).filter(Resolution::isHit);
        if (found.isPresent()) return CompletableFuture.completedFuture(new Json(entry(found.get()).encode()));
        String why = "no entry at " + address;
        return CompletableFuture.failedFuture(new ResourceNotFound(
                new ResourceNotFound._InternalError(null, why), new ResourceNotFound._ExternalError("catalogue-entry", why)));
    }

    /** The entry a hit found: a catalogue with what is under it, or a page. */
    public JsonObject entry(Resolution hit) {
        return switch (hit) {
            case Resolution.AtCatalogue(var path, var c) -> catalogue(c);
            case Resolution.AtLeaf(var path, var parent, var leaf) -> page(leaf).put("children", new JsonArray());
            case Resolution.Miss miss -> throw new IllegalArgumentException("a miss is no entry: " + miss.path());
        };
    }

    /** A catalogue, with what is under it: its catalogues, then its pages, each without children. */
    public JsonObject catalogue(Catalogue<?> c) {
        CatalogueTree tree = router.tree();
        var children = new JsonArray();
        for (Catalogue<?> sub : tree.childrenOf(c)) children.add(head(sub));
        for (Leaf<?> leaf : tree.leavesOf(c)) children.add(page(leaf));
        return head(c).put("children", children);
    }

    private JsonObject head(Catalogue<?> c) {
        var s = router.tree().shownAs(c);
        return entry(router.hrefOf(c), "catalogue", s.name(), s.summary(), s.badge(), s.icon(), Leaf.Opening.IN_PLACE);
    }

    private JsonObject page(Leaf<?> leaf) {
        return entry(router.hrefOf(leaf.page()).orElseThrow(), "page", leaf.name(), leaf.summary(), leaf.badge(), leaf.icon(), leaf.opens());
    }

    private static JsonObject entry(String to, String kind, String name, String summary, String badge, String icon, Leaf.Opening opens) {
        return new JsonObject().put("to", to).put("kind", kind).put("name", name).put("summary", summary)
                .put("badge", badge).put("icon", icon).put("opens", opens == Leaf.Opening.NEW_TAB ? "new-tab" : "in-place");
    }
}
