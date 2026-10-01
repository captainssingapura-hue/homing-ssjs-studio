package hue.captains.singapura.js.homing.conformance.studio.components;

import hue.captains.singapura.js.homing.components.conformance.ComponentsConformance;
import hue.captains.singapura.js.homing.conformance.workbench.ConformanceStudio;

/**
 * The components' conformance studio: the workbench over {@link ComponentsConformance#TOP_LEVEL},
 * saying of their rules what their build exported - the report in the check's own jar.
 * {@code mvn -o -pl homing-conformance-studio-components exec:java}, on 8097 unless
 * {@code -Dconformance.port} says otherwise.
 */
public final class ComponentsConformanceStudioServer {

    private ComponentsConformanceStudioServer() {}

    public static void main(String[] args) {
        int port = Integer.getInteger("conformance.port", 8097);
        ConformanceStudio.of("Homing · components conformance", ComponentsConformance.TOP_LEVEL,
                ConformanceStudio.exportedReport(ComponentsConformance.class)).serve(port);
    }
}
