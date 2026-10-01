package hue.captains.singapura.js.homing.catalogue.demo;

import hue.captains.singapura.js.homing.catalogue.demo.notes.NoteApp;
import hue.captains.singapura.js.homing.catalogue.demo.notes.NotesCatalogue;
import hue.captains.singapura.js.homing.catalogue.demo.recipes.RecipeApp;
import hue.captains.singapura.js.homing.catalogue.demo.recipes.RecipesCatalogue;
import hue.captains.singapura.js.homing.catalogue.demo.recipes.TimerApp;
import hue.captains.singapura.js.homing.catalogue.site.CatalogueListingApp;
import hue.captains.singapura.js.homing.catalogue.site.EntryGetAction;
import hue.captains.singapura.js.homing.docview.app.DocViewApp;
import hue.captains.singapura.js.homing.docview.site.DocInspectorApp;
import hue.captains.singapura.js.homing.docview.site.DocViews;
import hue.captains.singapura.js.homing.docview.site.PayloadGetAction;
import hue.captains.singapura.js.homing.server.EmptyParam;
import hue.captains.singapura.js.homing.site.Path;
import hue.captains.singapura.js.homing.site.Query;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The demo site: two apps' trees grafted into one root, every page - the apps',
 * the site's own, and every catalogue's listing - a page of the site's one MPA,
 * reached at its authentic path, told the trail read off it.
 */
class DemoSiteTest {

    private static String page(String path) {
        return DemoSite.INSTANCE.router().resolve(Path.parse(path)).orElseThrow(() -> new AssertionError("nothing at " + path)).html(Query.NONE).body();
    }

    @Test
    void theAppsPagesAreAtTheirAuthenticPaths_madeWithTheSitesMpa() {
        String laksa = page("/recipes/soups/laksa");
        assertTrue(laksa.contains(RecipeApp.class.getCanonicalName()), "the recipe app, as a page");
        assertTrue(laksa.contains("<title>Laksa · Catalogue demo</title>"), "titled by its trail, under the site's brand");
        assertTrue(laksa.contains("label:\"Catalogue demo\""), "the site's brand on the chrome");
        assertTrue(laksa.contains("text:\"Kitchen\",to:\"\\/recipes\""), "the graft's restatement, in the trail read off the path");
        String monday = page("/notes/journal/monday");
        assertTrue(monday.contains(NoteApp.class.getCanonicalName()) && monday.contains("label:\"Catalogue demo\""));
        assertTrue(page("/welcome").contains(NoteApp.class.getCanonicalName()), "the site's own note, placed as the notes app places one");
    }

    @Test
    void everyCatalogueIsAListingPageOfTheSameMpa() {
        for (String at : List.of("/", "/notes", "/notes/ideas", "/recipes", "/recipes/noodles")) {
            String listing = page(at);
            assertTrue(listing.contains(CatalogueListingApp.class.getCanonicalName()), at + ": the listing app");
            assertTrue(listing.contains("label:\"Catalogue demo\""), at + ": the site's brand");
        }
        assertTrue(page("/recipes").contains("<title>Kitchen · Catalogue demo</title>"), "the kitchen, as the graft shows it");
    }

    @Test
    void eachAppsTreeKnowsNoSite_itsPositionsAreTheSites() {
        assertEquals(Path.of("notes"), DemoSite.ROUTER.tree().pathOf(NotesCatalogue.INSTANCE));
        assertEquals(Path.of("recipes", "noodles"), DemoSite.ROUTER.tree().pathOf(RecipesCatalogue.NoodlesCatalogue.INSTANCE));
        assertEquals("Kitchen", DemoSite.ROUTER.tree().shownAs(RecipesCatalogue.INSTANCE).name());
        for (String nowhere : List.of("/recipes/laksa", "/soups", "/notes/monday", "/welcome/more")) {
            assertFalse(DemoSite.INSTANCE.router().resolve(Path.parse(nowhere)).isPresent(), nowhere);
        }
    }

    private static JsonObject entry(String at) throws Exception {
        return new JsonObject(new EntryGetAction(DemoSite.ROUTER).execute(new EntryGetAction.Query(at), new EmptyParam.NoHeaders()).get().body());
    }

    private static List<String> children(JsonObject e, String field) {
        return e.getJsonArray("children").stream().map(o -> ((JsonObject) o).getString(field)).toList();
    }

    @Test
    void theRootAsTheWidgetsReadIt() throws Exception {
        JsonObject root = entry("/");
        assertEquals("Catalogue demo", root.getString("name"));
        assertEquals(List.of("Notes", "Kitchen", "Reference docs", "Welcome"), children(root, "name"));
        assertEquals(List.of("/notes", "/recipes", "/reference", "/welcome"), children(root, "to"));
        assertEquals(List.of("catalogue", "catalogue", "catalogue", "page"), children(root, "kind"));
        assertEquals(List.of("/recipes/soups/laksa", "/recipes/soups/tom-yum"), children(entry("/recipes/soups"), "to"));
    }

    @Test
    void theReferenceDocs_eachReadInDocView_inspectedBeside_itsPayloadByItsPath() throws Exception {
        assertEquals(List.of("/reference/inspector", "/reference/markdown", "/reference/rigid", "/reference/named-rigid", "/reference/composed"),
                children(entry("/reference"), "to"));
        assertTrue(page("/reference/markdown").contains(DocViewApp.class.getCanonicalName()), "DocView, as the page");
        assertEquals(List.of("/reference/inspector/markdown", "/reference/inspector/rigid", "/reference/inspector/named-rigid", "/reference/inspector/composed"),
                children(entry("/reference/inspector"), "to"));
        assertTrue(page("/reference/inspector/markdown").contains(DocInspectorApp.class.getCanonicalName()), "the inspector, beside");
        for (String at : List.of("/reference/composed", "/reference/inspector/composed")) {
            String payload = new PayloadGetAction(new DocViews(DemoSite.ROUTER)).execute(new PayloadGetAction.Query(at), new EmptyParam.NoHeaders()).get().body();
            assertEquals(at, new JsonObject(payload).getString("doc"), "a doc read by the path its page is at");
            assertEquals("Composed reference", new JsonObject(payload).getJsonObject("tree").getJsonObject("label").getString("text"));
        }
    }

    @Test
    void eachPageOpensAsItsAppSays_theTimerBeside() throws Exception {
        JsonObject kitchen = entry("/recipes");
        assertEquals(List.of("/recipes/soups", "/recipes/noodles", "/recipes/kitchen-timer"), children(kitchen, "to"));
        assertEquals(List.of("in-place", "in-place", "new-tab"), children(kitchen, "opens"));
        assertEquals("in-place", entry("/recipes/soups/laksa").getString("opens"));
        assertTrue(page("/recipes/kitchen-timer").contains(TimerApp.class.getCanonicalName()), "the timer app, as a page");
    }
}
