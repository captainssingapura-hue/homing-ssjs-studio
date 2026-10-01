package hue.captains.singapura.js.homing.conformance.studio.workspace;

import hue.captains.singapura.js.homing.conformance.export.ConformanceReportSource;
import hue.captains.singapura.js.homing.conformance.workbench.ConformanceStudio;
import hue.captains.singapura.js.homing.core.Crate;
import hue.captains.singapura.js.homing.site.Path;
import hue.captains.singapura.js.homing.workspace.conformance.WorkspaceConformance;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The workspace's studio: the report their build exported, read from the check's jar; the studio
 * made over the workspace repo's crates - on the classpath at last, with no old copy of the
 * workspace under the same names beside them.
 */
class WorkspaceConformanceStudioTest {

    @Test
    void theReportIsTheChecks_aResultForEveryModuleOfTheirs() {
        ConformanceReportSource report = ConformanceStudio.exportedReport(WorkspaceConformance.class);
        for (Crate c : WorkspaceConformance.TOP_LEVEL)
            c.entries().forEach(e -> assertTrue(report.module(e.moduleClass()).isPresent(), e.moduleClass() + ": graded by the workspace's build"));
        assertEquals(0, report.summary().errorCount(), "the workspace passes, strictly: " + report.summary());
    }

    @Test
    void theStudioIsMadeOverTheirCrates_noOldStudioOnTheClasspath() {
        var studio = ConformanceStudio.of("Homing · workspace conformance", WorkspaceConformance.TOP_LEVEL,
                ConformanceStudio.exportedReport(WorkspaceConformance.class));
        assertTrue(studio.site().router().resolve(Path.parse("/conformance")).isPresent());
        assertThrows(ClassNotFoundException.class, () -> Class.forName("hue.captains.singapura.js.homing.studio.base.Bootstrap"),
                "the old studio stack is not on the classpath");
    }
}
