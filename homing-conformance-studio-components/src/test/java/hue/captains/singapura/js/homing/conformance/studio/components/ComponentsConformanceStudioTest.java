package hue.captains.singapura.js.homing.conformance.studio.components;

import hue.captains.singapura.js.homing.components.conformance.ComponentsConformance;
import hue.captains.singapura.js.homing.conformance.export.ConformanceReportSource;
import hue.captains.singapura.js.homing.conformance.workbench.ConformanceStudio;
import hue.captains.singapura.js.homing.site.Path;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** The components' studio: the report their build exported, read from the check's jar; the studio made over their crates, the old stack nowhere. */
class ComponentsConformanceStudioTest {

    @Test
    void theReportIsTheChecks_aResultForEveryModuleOfTheirs() {
        ConformanceReportSource report = ConformanceStudio.exportedReport(ComponentsConformance.class);
        for (String fqcn : ComponentsConformance.ownModules())
            assertTrue(report.module(fqcn).isPresent(), fqcn + ": graded by the components' build");
        assertEquals(0, report.summary().errorCount(), "the components pass, with no debt: " + report.summary());
    }

    @Test
    void theStudioIsMadeOverTheirCrates_noOldStudioOnTheClasspath() {
        var studio = ConformanceStudio.of("Homing · components conformance", ComponentsConformance.TOP_LEVEL,
                ConformanceStudio.exportedReport(ComponentsConformance.class));
        assertTrue(studio.site().router().resolve(Path.parse("/conformance")).isPresent());
        assertThrows(ClassNotFoundException.class, () -> Class.forName("hue.captains.singapura.js.homing.studio.base.Bootstrap"),
                "the old studio stack is not on the classpath");
    }
}
