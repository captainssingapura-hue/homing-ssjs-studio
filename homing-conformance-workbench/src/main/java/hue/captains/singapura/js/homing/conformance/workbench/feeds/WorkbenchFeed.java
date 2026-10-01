package hue.captains.singapura.js.homing.conformance.workbench.feeds;

import hue.captains.singapura.js.homing.server.EmptyParam;
import hue.captains.singapura.js.homing.server.ResourceNotFound;
import hue.captains.singapura.tao.http.action.GetAction;
import hue.captains.singapura.tao.http.action.Param;
import hue.captains.singapura.tao.http.action.ParamMarshaller;
import hue.captains.singapura.tao.http.action.TypedContent;
import io.vertx.ext.web.RoutingContext;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * What every workbench feed is: a GET at its own route, of one optional query parameter
 * ({@code path}), answered with a body computed from the crates a studio was given and the
 * report its build exported - or a 404 that says why, when it cannot be.
 */
public abstract class WorkbenchFeed implements GetAction<RoutingContext, WorkbenchFeed.Query, EmptyParam.NoHeaders, WorkbenchFeed.Body> {

    /** The one parameter a feed may read: which of what it serves. */
    public record Query(String path) implements Param._QueryString {
        public boolean asks() { return path != null && !path.isBlank(); }
    }

    /** What a feed answers with: its body, and what it is. */
    public record Body(String body, String contentType) implements TypedContent {
        public static Body json(String body) { return new Body(body, "application/json; charset=utf-8"); }
        public static Body text(String body) { return new Body(body, "text/plain; charset=utf-8"); }
    }

    private final String name;

    protected WorkbenchFeed(String name) { this.name = Objects.requireNonNull(name, "WorkbenchFeed.name"); }

    /** The route this feed answers at. */
    public abstract String route();

    /** The answer; a {@link NoSuchElementException} for a query that names nothing. */
    protected abstract Body answer(Query query) throws Exception;

    @Override
    public ParamMarshaller._QueryString<RoutingContext, Query> queryStrMarshaller() {
        return ctx -> new Query(ctx.request().getParam("path"));
    }

    @Override
    public ParamMarshaller._Header<RoutingContext, EmptyParam.NoHeaders> headerMarshaller() {
        return ctx -> new EmptyParam.NoHeaders();
    }

    @Override
    public CompletableFuture<Body> execute(Query query, EmptyParam.NoHeaders headers) {
        try {
            return CompletableFuture.completedFuture(answer(query));
        } catch (NoSuchElementException e) {
            return CompletableFuture.failedFuture(notFound(e.getMessage()));
        } catch (Exception e) {
            return CompletableFuture.failedFuture(notFound("could not be read: " + e.getMessage()));
        }
    }

    private ResourceNotFound notFound(String why) {
        return new ResourceNotFound(new ResourceNotFound._InternalError(null, name + ": " + why), new ResourceNotFound._ExternalError(name, why));
    }
}
