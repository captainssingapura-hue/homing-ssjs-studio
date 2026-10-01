package hue.captains.singapura.js.homing.conformance.studio.core;

import hue.captains.singapura.js.homing.conformance.export.ConformanceReportSource;
import hue.captains.singapura.js.homing.conformance.workbench.ConformanceStudio;
import hue.captains.singapura.js.homing.core.Crate;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Core's studio: the report its build exported beside it, over every module of the closure; the studio made over its crates. */
class CoreConformanceStudioTest {

    @Test
    void theBuildExportedItsReport_everyModuleOfTheClosure() {
        ConformanceReportSource report = ConformanceStudio.exportedReport(CoreConformanceStudioServer.class);
        int modules = CoreConformance.closure().stream().mapToInt(c -> c.entries().size()).sum();
        assertEquals(modules, report.summary().moduleCount(), "a result for every module of the closure");
        assertEquals(0, report.summary().errorCount(), "core's crates pass, their debt baselined: " + report.summary());
    }

    @Test
    void theStudioIsMadeOverCoresCrates_noOldStudioAmongThem() {
        var studio = ConformanceStudio.of("Homing · core conformance", CoreConformance.TOP_LEVEL, ConformanceStudio.exportedReport(CoreConformanceStudioServer.class));
        assertTrue(studio.site().router().resolve(hue.captains.singapura.js.homing.site.Path.parse("/conformance")).isPresent());
        for (Crate c : CoreConformance.closure())
            assertTrue(!c.name().contains("studio-base") && !c.name().startsWith("homing-workspace"), c.name() + ": the old stack is not core's to browse here");
    }
}
