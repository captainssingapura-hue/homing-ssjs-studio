package hue.captains.singapura.js.homing.catalogue.site;

import hue.captains.singapura.js.homing.site.catalogue.CatalogueRouter;
import hue.captains.singapura.tao.http.action.ActionRegistry;
import hue.captains.singapura.tao.http.action.GetAction;
import hue.captains.singapura.tao.http.action.PostAction;
import io.vertx.ext.web.RoutingContext;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * The catalogue's own route on a site's server: {@link VertexGetAction#PATH},
 * what each catalogue's listing reads it from. A host mounts routes in the
 * order given and a site ends with its catch-all, so this comes before it.
 *
 * <pre>
 *   new VertxActionHost(CatalogueRoutes.with(MPA.registry(site), router), HostConfig.http(port)).start();
 * </pre>
 */
public final class CatalogueRoutes {

    private CatalogueRoutes() {}

    /** The site's routes, with the catalogue's beside them - before the catch-all. */
    public static ActionRegistry<RoutingContext> with(ActionRegistry<RoutingContext> site, CatalogueRouter router) {
        Objects.requireNonNull(site, "CatalogueRoutes.site");
        var ours = new VertexGetAction(Objects.requireNonNull(router, "CatalogueRoutes.router"));
        var gets = new LinkedHashMap<String, GetAction<RoutingContext, ?, ?, ?>>();
        boolean placed = false;
        for (var e : site.getActions().entrySet()) {
            if (!placed && e.getKey().equals("/*")) { gets.put(VertexGetAction.PATH, ours); placed = true; }
            gets.put(e.getKey(), e.getValue());
        }
        if (!placed) gets.put(VertexGetAction.PATH, ours);
        var getsView = Collections.unmodifiableMap(gets);
        var postsView = Collections.unmodifiableMap(new LinkedHashMap<String, PostAction<RoutingContext, ?, ?, ?>>(site.postActions()));
        return new ActionRegistry<>() {
            @Override public Map<String, GetAction<RoutingContext, ?, ?, ?>> getActions() { return getsView; }
            @Override public Map<String, PostAction<RoutingContext, ?, ?, ?>> postActions() { return postsView; }
        };
    }
}
