package hue.captains.singapura.js.homing.docview.site;

import hue.captains.singapura.js.homing.server.EmptyParam;
import hue.captains.singapura.tao.http.action.GetAction;
import hue.captains.singapura.tao.http.action.Param;
import hue.captains.singapura.tao.http.action.ParamMarshaller;
import io.vertx.ext.web.RoutingContext;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * A doc's payload, by its authentic path: {@code /doc-view/payload?doc=/reference/markdown} - its
 * arrangement and every plain part's content, in one (DocPayload). A doc whose tree cannot be
 * built answers {@code { doc, failed }}, the reason; a path no leaf holding a doc is at, 404.
 */
public final class PayloadGetAction implements GetAction<RoutingContext, PayloadGetAction.Query, EmptyParam.NoHeaders, DocJson> {

    public static final String PATH = "/doc-view/payload";

    /** Which doc: its authentic path. */
    public record Query(String doc) implements Param._QueryString {}

    private final DocViews views;

    public PayloadGetAction(DocViews views) { this.views = Objects.requireNonNull(views, "PayloadGetAction.views"); }

    @Override
    public ParamMarshaller._QueryString<RoutingContext, Query> queryStrMarshaller() { return ctx -> new Query(ctx.request().getParam("doc")); }

    @Override
    public ParamMarshaller._Header<RoutingContext, EmptyParam.NoHeaders> headerMarshaller() { return ctx -> new EmptyParam.NoHeaders(); }

    @Override
    public CompletableFuture<DocJson> execute(Query query, EmptyParam.NoHeaders headers) {
        return views.at(query.doc())
                .map(b -> CompletableFuture.completedFuture(new DocJson(b.ok() ? b.payload()
                        : "{\"doc\":" + DocRoutes.quote(b.doc()) + ",\"failed\":" + DocRoutes.quote(b.failed()) + "}")))
                .orElseGet(() -> DocRoutes.missing("no doc is placed at " + query.doc()));
    }
}
