package hue.captains.singapura.js.homing.catalogue.site;

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
 * {@code GET /catalogue/vertex?path=<address>} - a catalogue as its listing reads
 * it: what it is shown as, and what is under it - the catalogues, sub-catalogues
 * and grafted trees alike, then the pages - each with the address the router
 * minted for it. The address is the catalogue's own, mount and all; one that
 * names no catalogue is a 404.
 *
 * <pre>
 *   { path, name, summary, badge, icon,
 *     catalogues: [{ to, name, summary, badge, icon }],
 *     pages:      [{ to, name, summary, badge, icon }] }
 * </pre>
 */
public final class VertexGetAction implements GetAction<RoutingContext, VertexGetAction.Query, EmptyParam.NoHeaders, VertexGetAction.Json> {

    /** Where the listing reads a catalogue from. */
    public static final String PATH = "/catalogue/vertex";

    /** The catalogue's address, as a page of the site has it. */
    public record Query(String path) implements Param._QueryString {}

    /** The answer: JSON. */
    public record Json(String body) implements TypedContent {
        @Override public String contentType() { return "application/json; charset=utf-8"; }
    }

    private final CatalogueRouter router;

    public VertexGetAction(CatalogueRouter router) {
        this.router = Objects.requireNonNull(router, "VertexGetAction.router");
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
        var found = router.resolution(Path.parse(address));
        if (found.isPresent() && found.get() instanceof Resolution.AtCatalogue at) {
            return CompletableFuture.completedFuture(new Json(vertex(at.catalogue()).encode()));
        }
        String why = "no catalogue at " + address;
        return CompletableFuture.failedFuture(new ResourceNotFound(
                new ResourceNotFound._InternalError(null, why), new ResourceNotFound._ExternalError("catalogue-vertex", why)));
    }

    /** The catalogue, as its listing reads it. */
    public JsonObject vertex(Catalogue<?> c) {
        CatalogueTree tree = router.tree();
        var shown = tree.shownAs(c);
        var catalogues = new JsonArray();
        for (Catalogue<?> sub : tree.childrenOf(c)) {
            var s = tree.shownAs(sub);
            catalogues.add(entry(router.hrefOf(sub), s.name(), s.summary(), s.badge(), s.icon()));
        }
        var pages = new JsonArray();
        for (Leaf<?> leaf : tree.leavesOf(c)) {
            pages.add(entry(router.hrefOf(leaf.page()).orElseThrow(), leaf.name(), leaf.summary(), leaf.badge(), leaf.icon()));
        }
        return new JsonObject()
                .put("path", router.hrefOf(c))
                .put("name", shown.name()).put("summary", shown.summary()).put("badge", shown.badge()).put("icon", shown.icon())
                .put("catalogues", catalogues)
                .put("pages", pages);
    }

    private static JsonObject entry(String href, String name, String summary, String badge, String icon) {
        return new JsonObject().put("to", href).put("name", name).put("summary", summary).put("badge", badge).put("icon", icon);
    }
}
