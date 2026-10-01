package hue.captains.singapura.js.homing.conformance.studio.core;

import hue.captains.singapura.js.homing.conformance.export.ConformanceReportSource;
import hue.captains.singapura.js.homing.conformance.self.CoreConformance;
import hue.captains.singapura.js.homing.conformance.workbench.ConformanceStudio;
import hue.captains.singapura.js.homing.site.Path;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Core's studio: the report core's build exported, read from the check's jar; the studio made over core's crates, the old stack nowhere. */
class CoreConformanceStudioTest {

    @Test
    void theReportIsTheChecks_aResultForEveryModuleOfTheClosure() {
        ConformanceReportSource report = ConformanceStudio.exportedReport(CoreConformance.class);
        int modules = CoreConformance.closure().stream().mapToInt(c -> c.entries().size()).sum();
        assertEquals(modules, report.summary().moduleCount(), "a result for every module of the closure");
        assertEquals(0, report.summary().errorCount(), "core's crates pass, their debt baselined: " + report.summary());
    }

    @Test
    void theStudioIsMadeOverCoresCrates_noOldStudioOnTheClasspath() {
        var studio = ConformanceStudio.of("Homing · core conformance", CoreConformance.TOP_LEVEL, ConformanceStudio.exportedReport(CoreConformance.class));
        assertTrue(studio.site().router().resolve(Path.parse("/conformance")).isPresent());
        assertThrows(ClassNotFoundException.class, () -> Class.forName("hue.captains.singapura.js.homing.studio.base.Bootstrap"),
                "the old studio stack is not on the classpath");
    }
}
