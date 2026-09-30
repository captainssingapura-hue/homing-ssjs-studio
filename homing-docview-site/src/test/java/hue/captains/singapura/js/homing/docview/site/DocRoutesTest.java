package hue.captains.singapura.js.homing.docview.site;

import hue.captains.singapura.js.homing.core.AppModule;
import hue.captains.singapura.js.homing.core.EsModule;
import hue.captains.singapura.js.homing.docview.reference.ReferenceDocs;
import hue.captains.singapura.js.homing.server.EmptyParam;
import hue.captains.singapura.js.homing.server.HtmlPageContent;
import hue.captains.singapura.js.homing.server.ResourceNotFound;
import hue.captains.singapura.js.homing.server.ThemeRegistry;
import hue.captains.singapura.js.homing.site.Path;
import hue.captains.singapura.js.homing.site.Placed;
import hue.captains.singapura.js.homing.site.Query;
import hue.captains.singapura.js.homing.site.Site;
import hue.captains.singapura.js.homing.site.Trail;
import hue.captains.singapura.js.homing.site.catalogue.CatalogueRouter;
import hue.captains.singapura.js.homing.site.catalogue.L0_Catalogue;
import hue.captains.singapura.js.homing.site.catalogue.Leaf;
import hue.captains.singapura.js.homing.site.mpa.Brand;
import hue.captains.singapura.js.homing.site.mpa.Mpa;
import hue.captains.singapura.js.homing.tree.NodeName;
import hue.captains.singapura.tao.http.action.ActionRegistry;
import hue.captains.singapura.tao.http.action.GetAction;
import hue.captains.singapura.tao.http.action.PostAction;
import io.vertx.ext.web.RoutingContext;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The reference docs placed in a catalogue, read by their authentic paths: a doc's page holds
 * the doc and is told where it is; the payload and a part's content, by the path and the key;
 * a doc built once and kept; a path no doc is at, and a key no part has, 404; the routes before
 * the site's catch-all.
 */
class DocRoutesTest {

    /** An MPA that makes a page saying which app it was made for, and with what. */
    record SayingMpa() implements Mpa {
        @Override public Brand brand() { return new Brand("Test", "/"); }
        @Override public ThemeRegistry themes() { throw new UnsupportedOperationException(); }
        @Override public String moduleUrl(EsModule<?> module) { throw new UnsupportedOperationException(); }
        @Override public <P extends AppModule._Param, M extends AppModule<P, M>> Placed page(M app, P params) {
            return new Made(app.simpleName(), app.paramCodec().to(params).toString());
        }
        @Override public ActionRegistry<RoutingContext> registry(Site site) { throw new UnsupportedOperationException(); }
    }

    record Made(String app, String params) implements Placed {
        @Override public HtmlPageContent html(Trail trail, Query q) { return new HtmlPageContent(app + " " + params); }
    }

    /** The reference docs, each a leaf at its name. */
    record ReferenceCatalogue() implements L0_Catalogue<ReferenceCatalogue> {
        static final ReferenceCatalogue INSTANCE = new ReferenceCatalogue();
        @Override public String name() { return "Reference docs"; }
        @Override public List<Leaf<ReferenceCatalogue>> leaves(Mpa mpa) {
            return ReferenceDocs.ALL.entrySet().stream().map(e -> DocLeaves.inspected(this, mpa, new NodeName(e.getKey()), e.getValue())).toList();
        }
    }

    static final CatalogueRouter ROUTER = CatalogueRouter.at(Path.ROOT, ReferenceCatalogue.INSTANCE, new SayingMpa());

    private static String payload(String doc) throws Exception {
        return new PayloadGetAction(new DocViews(ROUTER)).execute(new PayloadGetAction.Query(doc), new EmptyParam.NoHeaders()).get().body();
    }

    @Test
    void aDocsPage_holdsTheDoc_andIsToldWhereItIs() {
        var leaf = ROUTER.tree().leavesOf(ReferenceCatalogue.INSTANCE).get(0);
        var page = assertInstanceOf(DocInspection.class, leaf.page());
        assertSame(ReferenceDocs.ALL.get("markdown"), page.doc());
        String at = ROUTER.hrefOf(page).orElseThrow();
        assertEquals("/markdown", at);
        String html = ROUTER.resolve(Path.parse(at)).orElseThrow().html(Query.NONE).body();
        assertTrue(html.startsWith("doc-inspector "), html);
        assertTrue(html.contains("doc=[/markdown]") && html.contains("title=[Markdown reference]"), html);
    }

    @Test
    void thePayload_byTheDocsAuthenticPath() throws Exception {
        for (String name : ReferenceDocs.ALL.keySet()) {
            String json = payload("/" + name);
            assertTrue(json.startsWith("{\"doc\":\"/" + name + "\",\"arrangement\":{\"engine\":\"tree\""), json.substring(0, 80));
        }
    }

    @Test
    void aDocIsBuiltOnce_andKept() {
        var views = new DocViews(ROUTER);
        assertSame(views.at("/rigid").orElseThrow(), views.at("/rigid").orElseThrow());
        assertTrue(views.at("/rigid").orElseThrow().ok());
    }

    @Test
    void aPartsContent_byThePathAndTheKey() throws Exception {
        String json = new ContentGetAction(new DocViews(ROUTER)).execute(new ContentGetAction.Query("/rigid", "a-picture:0"),
                new EmptyParam.NoHeaders()).get().body();
        assertTrue(json.startsWith("{\"type\":\"image\",\"params\":[{\"name\":\"doc\",\"value\":\"/rigid\"},{\"name\":\"key\",\"value\":\"a-picture:0\"}]"), json);
        assertTrue(json.contains("\"svg\":\"<svg"), json);
    }

    @Test
    void aPathNoDocIsAt_andAKeyNoPartHas_areNotFound() {
        var views = new DocViews(ROUTER);
        var missing = assertThrows(ExecutionException.class, () -> new PayloadGetAction(views)
                .execute(new PayloadGetAction.Query("/nowhere"), new EmptyParam.NoHeaders()).get());
        assertInstanceOf(ResourceNotFound.class, missing.getCause());
        assertThrows(ExecutionException.class, () -> new PayloadGetAction(views)
                .execute(new PayloadGetAction.Query("/"), new EmptyParam.NoHeaders()).get(), "a catalogue holds no doc");
        assertThrows(ExecutionException.class, () -> new ContentGetAction(views)
                .execute(new ContentGetAction.Query("/rigid", "nowhere:9"), new EmptyParam.NoHeaders()).get());
    }

    @Test
    void theRoutes_areMountedBeforeTheCatchAll() {
        ActionRegistry<RoutingContext> site = new ActionRegistry<>() {
            @Override public Map<String, GetAction<RoutingContext, ?, ?, ?>> getActions() {
                var m = new LinkedHashMap<String, GetAction<RoutingContext, ?, ?, ?>>();
                m.put("/module", null);
                m.put("/*", null);
                return m;
            }
            @Override public Map<String, PostAction<RoutingContext, ?, ?, ?>> postActions() { return Map.of(); }
        };
        assertEquals(List.of("/module", PayloadGetAction.PATH, ContentGetAction.PATH, "/*"), List.copyOf(DocRoutes.with(site, ROUTER).getActions().keySet()));
    }
}
