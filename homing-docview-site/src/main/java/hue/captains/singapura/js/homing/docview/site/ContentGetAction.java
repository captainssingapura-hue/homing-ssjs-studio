package hue.captains.singapura.js.homing.docview.site;

import hue.captains.singapura.js.homing.docview.tree.DocPayload;
import hue.captains.singapura.js.homing.server.EmptyParam;
import hue.captains.singapura.tao.http.action.GetAction;
import hue.captains.singapura.tao.http.action.Param;
import hue.captains.singapura.tao.http.action.ParamMarshaller;
import io.vertx.ext.web.RoutingContext;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * One part's content, by the doc's authentic path and the part's key:
 * {@code /doc-view/content?doc=/reference/rigid&key=a-picture:0} - {@code { type, params, content }},
 * what a heavy part - an image - is fetched by when it is wanted. 404 for a doc or a key that is
 * not there.
 */
public final class ContentGetAction implements GetAction<RoutingContext, ContentGetAction.Query, EmptyParam.NoHeaders, DocJson> {

    public static final String PATH = "/doc-view/content";

    /** Which part: the doc's authentic path, and the part's key. */
    public record Query(String doc, String key) implements Param._QueryString {}

    private final DocViews views;

    public ContentGetAction(DocViews views) { this.views = Objects.requireNonNull(views, "ContentGetAction.views"); }

    @Override
    public ParamMarshaller._QueryString<RoutingContext, Query> queryStrMarshaller() {
        return ctx -> new Query(ctx.request().getParam("doc"), ctx.request().getParam("key"));
    }

    @Override
    public ParamMarshaller._Header<RoutingContext, EmptyParam.NoHeaders> headerMarshaller() { return ctx -> new EmptyParam.NoHeaders(); }

    @Override
    public CompletableFuture<DocJson> execute(Query query, EmptyParam.NoHeaders headers) {
        return views.at(query.doc()).filter(b -> b.ok() && b.tree() != null)   // a plan has no part fetched apart
                .flatMap(b -> DocPayload.content(b.tree(), b.doc(), String.valueOf(query.key()), DocPayload.NO_RASTERS))
                .map(json -> CompletableFuture.completedFuture(new DocJson(json)))
                .orElseGet(() -> DocRoutes.missing("no part " + query.key() + " of a doc at " + query.doc()));
    }
}
