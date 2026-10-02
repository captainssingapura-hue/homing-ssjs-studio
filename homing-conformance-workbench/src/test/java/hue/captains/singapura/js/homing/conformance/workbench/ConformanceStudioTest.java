package hue.captains.singapura.js.homing.conformance.workbench;

import hue.captains.singapura.js.homing.catalogue.widgets.CatalogueWidgetsCrate;
import hue.captains.singapura.js.homing.conformance.engine.ConformanceEngine;
import hue.captains.singapura.js.homing.conformance.export.ConformanceReportSource;
import hue.captains.singapura.js.homing.conformance.export.ConformanceReportWriter;
import hue.captains.singapura.js.homing.conformance.rules.CrateClosure;
import hue.captains.singapura.js.homing.conformance.rules.FindingGrader;
import hue.captains.singapura.js.homing.conformance.workbench.feeds.CratesFeed;
import hue.captains.singapura.js.homing.conformance.workbench.feeds.ReportFeed;
import hue.captains.singapura.js.homing.core.Crate;
import hue.captains.singapura.js.homing.site.Path;
import hue.captains.singapura.js.homing.site.Query;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A studio put together: the group's page at {@code /conformance}, the root sending to it, both
 * workbenches filed in it; the feeds mounted before the site's catch-all.
 */
class ConformanceStudioTest {

    private static final List<Crate> CRATES = List.of(CatalogueWidgetsCrate.INSTANCE);

    @TempDir static java.nio.file.Path reportDir;
    private static ConformanceStudio studio;

    @BeforeAll
    static void studio() throws Exception {
        new ConformanceReportWriter().write(reportDir, new ConformanceEngine().assemble(CrateClosure.of(CRATES), FindingGrader.STRICT));
        studio = ConformanceStudio.of("Test conformance", CRATES, new ConformanceReportSource(reportDir));
    }

    private static String page(String path) {
        return studio.site().router().resolve(Path.parse(path)).orElseThrow(() -> new AssertionError("nothing at " + path)).html(Query.NONE).body();
    }

    @Test
    void theGroupIsThePage_theRootSendsToIt() {
        String html = page("/conformance");
        assertTrue(html.contains(WorkbenchApp.class.getCanonicalName()), "the workbenches' app, as the page");
        assertTrue(html.contains("\"ws_group\":\"conformance\""), "the route's group");
        assertTrue(html.contains("label:\"Test conformance\""), "under the studio's brand");
        assertTrue(page("/").contains("window.location.replace(\"\\/conformance\" + window.location.hash)"), "the root sends to the group");
    }

    @Test
    void bothWorkbenchesAreFiled_conformanceTheDefault() {
        String groups = String.join("\n", WorkbenchGroupsModule.INSTANCE.selfContent(null));
        assertTrue(groups.contains("defaultKind: \"conformance\""), groups);
        assertTrue(groups.contains("kind: \"conformance\", title: \"Conformance\""), groups);
        assertTrue(groups.contains("kind: \"components\", title: \"Components\""), groups);
    }

    @Test
    void theFeedsAreMountedBeforeTheCatchAll_andTheBrowsedCratesAreServed() {
        var routes = new ArrayList<>(studio.routes().getActions().keySet());
        assertTrue(routes.indexOf(CratesFeed.ROUTE) >= 0 && routes.indexOf(CratesFeed.ROUTE) < routes.indexOf("/*"), routes.toString());
        assertTrue(routes.indexOf(ReportFeed.ROUTE) < routes.indexOf("/*"), routes.toString());
        assertEquals("/*", routes.get(routes.size() - 1), routes.toString());
        assertTrue(studio.mpa().registry(studio.site()).getActions().containsKey("/module"), "modules served, the browsed ones among them");
    }
}
