package hue.captains.singapura.js.homing.docview.site;

import hue.captains.singapura.js.homing.server.ResourceNotFound;
import hue.captains.singapura.js.homing.site.catalogue.CatalogueRouter;
import hue.captains.singapura.tao.http.action.ActionRegistry;
import hue.captains.singapura.tao.http.action.GetAction;
import hue.captains.singapura.tao.http.action.PostAction;
import io.vertx.ext.web.RoutingContext;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * DocView's routes on a site's server - the payload and one part's content, each by the doc's
 * authentic path - over the docs the site's catalogue places. A host mounts routes in the order
 * given and a site ends with its catch-all, so these come before it.
 *
 * <pre>
 *   new VertxActionHost(DocRoutes.with(CatalogueRoutes.with(MPA.registry(site), router), router), HostConfig.http(port)).start();
 * </pre>
 */
public final class DocRoutes {

    private DocRoutes() {}

    /** The site's routes, with DocView's beside them - before the catch-all. */
    public static ActionRegistry<RoutingContext> with(ActionRegistry<RoutingContext> site, CatalogueRouter router) {
        Objects.requireNonNull(site, "DocRoutes.site");
        var views = new DocViews(Objects.requireNonNull(router, "DocRoutes.router"));
        var ours = new LinkedHashMap<String, GetAction<RoutingContext, ?, ?, ?>>();
        ours.put(PayloadGetAction.PATH, new PayloadGetAction(views));
        ours.put(ContentGetAction.PATH, new ContentGetAction(views));
        var gets = new LinkedHashMap<String, GetAction<RoutingContext, ?, ?, ?>>();
        boolean placed = false;
        for (var e : site.getActions().entrySet()) {
            if (!placed && e.getKey().equals("/*")) { gets.putAll(ours); placed = true; }
            gets.put(e.getKey(), e.getValue());
        }
        if (!placed) gets.putAll(ours);
        var getsView = Collections.unmodifiableMap(gets);
        var postsView = Collections.unmodifiableMap(new LinkedHashMap<String, PostAction<RoutingContext, ?, ?, ?>>(site.postActions()));
        return new ActionRegistry<>() {
            @Override public Map<String, GetAction<RoutingContext, ?, ?, ?>> getActions() { return getsView; }
            @Override public Map<String, PostAction<RoutingContext, ?, ?, ?>> postActions() { return postsView; }
        };
    }

    static <T> CompletableFuture<T> missing(String why) {
        return CompletableFuture.failedFuture(new ResourceNotFound(new ResourceNotFound._InternalError(null, why),
                new ResourceNotFound._ExternalError("doc-view", why)));
    }

    static String quote(String s) {
        return "\"" + s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\n").replace("\r", "\r").replace("\t", "\t") + "\"";
    }
}
