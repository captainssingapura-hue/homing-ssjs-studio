package hue.captains.singapura.js.homing.conformance.studio.workspace;

import hue.captains.singapura.js.homing.conformance.workbench.ConformanceStudio;
import hue.captains.singapura.js.homing.workspace.conformance.WorkspaceConformance;

/**
 * The workspace's conformance studio: the workbench over {@link WorkspaceConformance#TOP_LEVEL},
 * saying of their rules what their build exported - the report in the check's own jar.
 * {@code mvn -o -pl homing-conformance-studio-workspace exec:java}, on 8099 unless
 * {@code -Dconformance.port} says otherwise.
 */
public final class WorkspaceConformanceStudioServer {

    private WorkspaceConformanceStudioServer() {}

    public static void main(String[] args) {
        int port = Integer.getInteger("conformance.port", 8099);
        ConformanceStudio.of("Homing · workspace conformance", WorkspaceConformance.TOP_LEVEL,
                ConformanceStudio.exportedReport(WorkspaceConformance.class)).serve(port);
    }
}
