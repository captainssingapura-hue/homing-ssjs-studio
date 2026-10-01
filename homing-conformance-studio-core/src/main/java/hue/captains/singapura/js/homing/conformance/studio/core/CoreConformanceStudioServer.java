package hue.captains.singapura.js.homing.conformance.studio.core;

import hue.captains.singapura.js.homing.conformance.self.CoreConformance;
import hue.captains.singapura.js.homing.conformance.workbench.ConformanceStudio;

/**
 * Core's conformance studio: the workbench over {@link CoreConformance#TOP_LEVEL}, saying of their
 * rules what core's build exported - the report in the check's own jar.
 * {@code mvn -o -pl homing-conformance-studio-core exec:java}, on 8090 unless
 * {@code -Dconformance.port} says otherwise.
 */
public final class CoreConformanceStudioServer {

    private CoreConformanceStudioServer() {}

    public static void main(String[] args) {
        int port = Integer.getInteger("conformance.port", 8090);
        ConformanceStudio.of("Homing · core conformance", CoreConformance.TOP_LEVEL,
                ConformanceStudio.exportedReport(CoreConformance.class)).serve(port);
    }
}
