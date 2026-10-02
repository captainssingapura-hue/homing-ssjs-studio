package hue.captains.singapura.js.homing.catalogue.site;

import hue.captains.singapura.js.homing.core.AppModule;
import hue.captains.singapura.js.homing.core.EsModule;
import hue.captains.singapura.js.homing.server.EmptyParam;
import hue.captains.singapura.js.homing.server.HtmlPageContent;
import hue.captains.singapura.js.homing.server.ThemeRegistry;
import hue.captains.singapura.js.homing.site.Path;
import hue.captains.singapura.js.homing.site.Placed;
import hue.captains.singapura.js.homing.site.Query;
import hue.captains.singapura.js.homing.site.Site;
import hue.captains.singapura.js.homing.site.Trail;
import hue.captains.singapura.js.homing.site.catalogue.CatalogueRouter;
import hue.captains.singapura.js.homing.site.catalogue.Graft;
import hue.captains.singapura.js.homing.site.catalogue.L0_Catalogue;
import hue.captains.singapura.js.homing.site.catalogue.L1_Catalogue;
import hue.captains.singapura.js.homing.site.catalogue.Leaf;
import hue.captains.singapura.js.homing.site.mpa.Brand;
import hue.captains.singapura.js.homing.site.mpa.Mpa;
import hue.captains.singapura.tao.http.action.ActionRegistry;
import hue.captains.singapura.tao.http.action.GetAction;
import hue.captains.singapura.tao.http.action.PostAction;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The listing, knowing no app: at each catalogue, a page of the site's MPA - the
 * listing app, handed the catalogue's address - so it wears the site's chrome
 * and is told its trail; an entry as the widgets read it, from the route -
 * its grafted trees among its catalogues, each entry with its authentic path
 * and how its app opens;
 * the route mounted before the site's catch-all.
 */
class ListingTest {

    /** An MPA that makes a page saying which app it was made for, and with what, and the trail it is told. */
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
        @Override public HtmlPageContent html(Trail trail, Query q) {
            return new HtmlPageContent(app + " " + params + " " + trail.crumbs().stream().map(Trail.Crumb::text).toList());
        }
    }

    record Note(String title) implements Placed {
        @Override public HtmlPageContent html(Trail trail, Query q) { return new HtmlPageContent(title); }
    }

    record PantryCatalogue() implements L0_Catalogue<PantryCatalogue> {
        static final PantryCatalogue INSTANCE = new PantryCatalogue();
        @Override public String name() { return "Pantry"; }
        @Override public String summary() { return "What is in"; }
        @Override public List<Leaf<PantryCatalogue>> leaves() { return List.of(Leaf.of(this, "Rice", "Jasmine", new Note("rice"))); }
    }
    record HomeCatalogue() implements L0_Catalogue<HomeCatalogue> {
        static final HomeCatalogue INSTANCE = new HomeCatalogue();
        @Override public String name() { return "Home"; }
        @Override public List<? extends L1_Catalogue<HomeCatalogue, ?>> subCatalogues() { return List.of(ShedCatalogue.INSTANCE); }
        @Override public List<Graft<HomeCatalogue>> grafts() { return List.of(Graft.of(this, PantryCatalogue.INSTANCE).icon("🥫")); }
        @Override public List<Leaf<HomeCatalogue>> leaves() { return List.of(Leaf.of(this, "About", "", new Note("about")).badge("NOTE")); }
    }
    record ShedCatalogue() implements L1_Catalogue<HomeCatalogue, ShedCatalogue> {
        static final ShedCatalogue INSTANCE = new ShedCatalogue();
        @Override public HomeCatalogue parent() { return HomeCatalogue.INSTANCE; }
        @Override public String name() { return "Shed"; }
        @Override public List<Leaf<ShedCatalogue>> leaves() { return List.of(Leaf.of(this, "Clock", "", new Note("clock")).opens(Leaf.Opening.NEW_TAB)); }
    }

    static final CatalogueRouter ROUTER = CatalogueRouter.at(Path.of("cat"), HomeCatalogue.INSTANCE, new SayingMpa()).listing(AppListing.INSTANCE);

    @Test
    void eachCatalogueIsAPageOfTheSitesMpa_theListingAppHandedItsAddress_andToldItsTrail() {
        assertEquals("catalogue-listing {path=[/cat/pantry]} [Home, Pantry]", ROUTER.resolve(Path.of("cat", "pantry")).orElseThrow().html(Query.NONE).body());
        assertEquals("catalogue-listing {path=[/cat]} [Home]", ROUTER.resolve(Path.of("cat")).orElseThrow().html(Query.NONE).body());
    }

    @Test
    void aTreeReadWithoutAnMpaCannotListByApp() {
        var bare = CatalogueRouter.at(Path.ROOT, PantryCatalogue.INSTANCE).listing(AppListing.INSTANCE);
        var e = assertThrows(IllegalStateException.class, () -> bare.resolve(Path.ROOT));
        assertTrue(e.getMessage().contains("CatalogueRouter.at(mount, root, mpa)"), e.getMessage());
    }

    private static JsonObject read(String address) throws Exception {
        return new JsonObject(new EntryGetAction(ROUTER).execute(new EntryGetAction.Query(address), new EmptyParam.NoHeaders()).get().body());
    }

    @Test
    void theRouteReadsACatalogue_itsGraftsAmongItsCatalogues_eachEntryAtItsAuthenticPath() throws Exception {
        JsonObject home = read("/cat");
        assertEquals("/cat", home.getString("to"));
        assertEquals("catalogue", home.getString("kind"));
        assertEquals("Home", home.getString("name"));
        assertEquals(List.of("/cat/shed catalogue", "/cat/pantry catalogue", "/cat/about page"),
                home.getJsonArray("children").stream().map(o -> ((JsonObject) o).getString("to") + " " + ((JsonObject) o).getString("kind")).toList());
        JsonObject pantry = home.getJsonArray("children").getJsonObject(1);
        assertEquals("🥫", pantry.getString("icon"));
        assertEquals("What is in", pantry.getString("summary"));
        JsonObject about = home.getJsonArray("children").getJsonObject(2);
        assertEquals(Map.of("to", "/cat/about", "kind", "page", "name", "About", "summary", "", "badge", "NOTE", "icon", "", "opens", "in-place"), about.getMap());
        assertEquals("/cat/pantry/rice", read("/cat/pantry").getJsonArray("children").getJsonObject(0).getString("to"));
    }

    @Test
    void theRouteReadsAPage_asItsAppSaysItOpens() throws Exception {
        JsonObject about = read("/cat/about");
        assertEquals("page", about.getString("kind"));
        assertEquals(0, about.getJsonArray("children").size());
        assertEquals("new-tab", read("/cat/shed/clock").getString("opens"));
    }

    @Test
    void anAddressThatNamesNoEntryIsAMiss() {
        for (String nowhere : List.of("/cat/about/more", "/cat/nope", "/elsewhere")) {
            var failed = new EntryGetAction(ROUTER).execute(new EntryGetAction.Query(nowhere), new EmptyParam.NoHeaders());
            assertThrows(ExecutionException.class, failed::get, nowhere);
        }
    }

    @Test
    void theRouteIsMountedBeforeTheSitesCatchAll() {
        var site = new ActionRegistry<RoutingContext>() {
            @Override public Map<String, GetAction<RoutingContext, ?, ?, ?>> getActions() {
                return Map.of("/*", new EntryGetAction(ROUTER));
            }
            @Override public Map<String, PostAction<RoutingContext, ?, ?, ?>> postActions() { return Map.of(); }
        };
        assertEquals(List.of(EntryGetAction.PATH, "/*"), List.copyOf(CatalogueRoutes.with(site, ROUTER).getActions().keySet()));
    }
}
