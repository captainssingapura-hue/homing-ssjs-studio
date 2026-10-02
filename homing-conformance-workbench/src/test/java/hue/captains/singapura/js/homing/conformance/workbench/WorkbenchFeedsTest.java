package hue.captains.singapura.js.homing.conformance.workbench;

import hue.captains.singapura.js.homing.catalogue.widgets.CatalogueWidgetsCrate;
import hue.captains.singapura.js.homing.conformance.engine.ConformanceEngine;
import hue.captains.singapura.js.homing.conformance.export.ConformanceReportSource;
import hue.captains.singapura.js.homing.conformance.export.ConformanceReportWriter;
import hue.captains.singapura.js.homing.conformance.rules.CrateClosure;
import hue.captains.singapura.js.homing.conformance.rules.FindingGrader;
import hue.captains.singapura.js.homing.conformance.workbench.feeds.ComponentsFeed;
import hue.captains.singapura.js.homing.conformance.workbench.feeds.CrateConformanceFeed;
import hue.captains.singapura.js.homing.conformance.workbench.feeds.CrateGraphFeed;
import hue.captains.singapura.js.homing.conformance.workbench.feeds.CratesFeed;
import hue.captains.singapura.js.homing.conformance.workbench.feeds.ReportFeed;
import hue.captains.singapura.js.homing.conformance.workbench.feeds.WorkbenchFeed;
import hue.captains.singapura.js.homing.core.Crate;
import hue.captains.singapura.js.homing.server.EmptyParam;
import hue.captains.singapura.js.homing.server.ResourceNotFound;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The feeds the workbenches read, over the workbench's own crate and the catalogue widgets' -
 * the report they say of the rules exported here first, as a build exports it.
 */
class WorkbenchFeedsTest {

    private static final List<Crate> CRATES = List.of(ConformanceWorkbenchCrate.INSTANCE, CatalogueWidgetsCrate.INSTANCE);

    @TempDir static Path reportDir;
    private static ConformanceReportSource report;

    @BeforeAll
    static void export() throws Exception {
        var run = new ConformanceEngine().assemble(CrateClosure.of(CRATES), FindingGrader.STRICT);
        new ConformanceReportWriter().write(reportDir, run);
        report = new ConformanceReportSource(reportDir);
    }

    private static String body(WorkbenchFeed feed, String path) throws Exception {
        return feed.execute(new WorkbenchFeed.Query(path), new EmptyParam.NoHeaders()).get().body();
    }

    @Test
    void theCrates_eachCratesModulesByTheirPackages_aModuleALeaf() throws Exception {
        var tree = new JsonObject(body(new CratesFeed(CRATES), null));
        assertEquals(2, tree.getInteger("crates"));
        JsonArray crates = tree.getJsonArray("children");
        JsonObject workbench = crates.getJsonObject(0);
        assertEquals("crate:homing-conformance-workbench", workbench.getString("key"));
        assertEquals("crate", workbench.getString("kind"));
        assertEquals(ConformanceWorkbenchCrate.INSTANCE.entries().size(), workbench.getInteger("modules"));
        assertEquals("hue.captains.singapura.js.homing.conformance.workbench", workbench.getString("prefix"), "the common package stripped");
        var leaves = new ArrayList<JsonObject>();
        collect(workbench, leaves);
        assertEquals(ConformanceWorkbenchCrate.INSTANCE.entries().size(), leaves.size(), "every module a leaf");
        JsonObject app = leaves.stream().filter(l -> l.getString("label").equals("WorkbenchApp")).findFirst().orElseThrow();
        assertEquals("module:" + WorkbenchApp.class.getName(), app.getString("key"));
        assertEquals("homing-conformance-workbench", app.getString("crate"));
        assertEquals(WorkbenchApp.class.getName(), app.getString("fqcn"));
    }

    private static void collect(JsonObject node, List<JsonObject> leaves) {
        if ("module".equals(node.getString("kind"))) { leaves.add(node); return; }
        node.getJsonArray("children").forEach(c -> collect((JsonObject) c, leaves));
    }

    @Test
    void crateConformance_byCrateAndByModule_theRulesFromTheReport() throws Exception {
        var c = new JsonObject(body(new CrateConformanceFeed(CRATES, report), null));
        JsonObject crate = c.getJsonObject("crates").getJsonObject("homing-conformance-workbench");
        assertEquals(ConformanceWorkbenchCrate.INSTANCE.entries().size(), crate.getInteger("modules"));
        assertTrue(crate.containsKey("orphans") && crate.containsKey("illegalImports") && crate.containsKey("ruleFindings"));
        JsonObject app = c.getJsonObject("modules").getJsonObject(WorkbenchApp.class.getName());
        assertEquals("homing-conformance-workbench", app.getString("crate"));
        assertTrue(app.getBoolean("ok"), "the workbench's own app is conformant: " + app);
        assertTrue(c.getJsonObject("crates").containsKey("homing-catalogue-widgets"), "the closure's crates too, the ones it browses among them");
    }

    @Test
    void theGraph_everyCrateOfTheClosure_theOnesNotOwnedDashed_noColourSaid() throws Exception {
        String graph = body(new CrateGraphFeed(CRATES), null);
        assertTrue(graph.startsWith("flowchart TD\n"), graph);
        assertTrue(graph.contains("homing_conformance_workbench --> homing_catalogue_widgets"), graph);
        assertTrue(graph.contains("classDef external stroke-dasharray:4 3;"), graph);
        assertFalse(graph.contains("fill:") || graph.contains("#"), "the design's colours, never the feed's: " + graph);
    }

    @Test
    void theReport_itsSummaryAndEveryModule_throughTheCodecs() throws Exception {
        var r = new JsonObject(body(new ReportFeed(report), null));
        JsonObject summary = r.getJsonObject("summary");
        assertEquals(report.summary().moduleCount(), summary.getInteger("moduleCount"));
        assertEquals(report.summary().moduleCount(), r.getJsonArray("modules").size(), "every module's result");
    }

    @Test
    void theComponents_aTree_andANodeByItsNamePath() throws Exception {
        var feed = new ComponentsFeed(CRATES);
        var tree = new JsonObject(body(feed, null));
        assertEquals("", tree.getString("key"));
        assertEquals("composition", tree.getString("kind"));
        JsonObject vehicle = tree.getJsonArray("children").getJsonObject(0);
        assertEquals("vehicle", vehicle.getString("kind"));
        var asked = new JsonObject(body(feed, vehicle.getString("key")));
        assertEquals("vehicle", asked.getString("kind"));
        assertEquals(vehicle.getString("label"), asked.getString("label"));
        assertEquals("composition", new JsonObject(body(feed, "/")).getString("kind"), "the root, by \"/\"");
        var nowhere = assertThrows(ExecutionException.class, () -> body(feed, "no/such/node"));
        var notFound = assertInstanceOf(ResourceNotFound.class, nowhere.getCause(), "a 404");
        assertTrue(notFound.externalError().toString().contains("no component at no/such/node"), notFound.externalError().toString());
    }
}
