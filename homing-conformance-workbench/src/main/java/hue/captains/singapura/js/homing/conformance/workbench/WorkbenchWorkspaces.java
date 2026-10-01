package hue.captains.singapura.js.homing.conformance.workbench;

import hue.captains.singapura.js.homing.workspace.groups.core.models.ArrangedWidget;
import hue.captains.singapura.js.homing.workspace.groups.core.models.Arrangement;
import hue.captains.singapura.js.homing.workspace.groups.core.models.GroupedWorkspace;
import hue.captains.singapura.js.homing.workspace.groups.core.models.SplitGrid;
import hue.captains.singapura.js.homing.workspace.groups.core.models.SplitGrid.Part;
import hue.captains.singapura.js.homing.workspace.groups.core.models.WorkspaceArrangements;
import hue.captains.singapura.js.homing.workspace.groups.core.models.WorkspaceGroup;
import hue.captains.singapura.js.homing.workspace.groups.core.models.WorkspaceGroups;
import hue.captains.singapura.js.homing.workspace.groups.core.models.WorkspaceKind;
import hue.captains.singapura.js.homing.workspace.site.GroupedWorkspaces;
import hue.captains.singapura.js.homing.workspace.widgets.WidgetDeclaration;
import hue.captains.singapura.js.homing.workspace.widgets.WorkspaceDeclaration;

import java.util.List;

/**
 * The studio's two workbenches, declared, grouped and arranged: the conformance workbench over
 * the crates' physical side, the components workbench over their logical one, both filed in one
 * group, {@code conformance} - its page {@code /conformance}, each workbench its anchor - and each
 * laid out the first time: the navigation on the left, the details beside it.
 */
public final class WorkbenchWorkspaces {

    private WorkbenchWorkspaces() {}

    /** {@code conformance}: the crates and their modules, the graph, a node's summary and source, its conformance, the report. */
    public record Conformance() implements WorkspaceDeclaration {
        public static final Conformance INSTANCE = new Conformance();
        @Override public String name() { return "conformance"; }
        @Override public List<WidgetDeclaration<?>> kinds() { return WorkbenchDeclarations.CONFORMANCE; }
    }

    /** {@code components}: the components the crates deliver, and the one picked. */
    public record Components() implements WorkspaceDeclaration {
        public static final Components INSTANCE = new Components();
        @Override public String name() { return "components"; }
        @Override public List<WidgetDeclaration<?>> kinds() { return WorkbenchDeclarations.COMPONENTS; }
    }

    /**
     * The conformance workbench, the first time: the crates and the graph on the left, a third of the
     * room; beside them the summary and the source above, the module's, the crate's and the whole
     * report's conformance below.
     */
    public static final Arrangement<Conformance, SplitGrid> CONFORMANCE_GRID = Arrangement.of(Conformance.INSTANCE,
            SplitGrid.of(SplitGrid.row(Part.of(SplitGrid.region("navigation", "crates", "graph"), 1),
                    Part.of(SplitGrid.column(SplitGrid.region("details", "summary", "source"),
                            SplitGrid.region("checks", "module-checks", "crate-checks", "report")), 2))),
            ArrangedWidget.of("crates", "crate-navigator"),
            ArrangedWidget.of("graph", "crate-graph"),
            ArrangedWidget.of("summary", "node-summary"),
            ArrangedWidget.of("source", "module-source"),
            ArrangedWidget.of("module-checks", "module-checks"),
            ArrangedWidget.of("crate-checks", "crate-checks"),
            ArrangedWidget.of("report", "conformance-report"));

    /** The components workbench, the first time: the components on the left, a third of the room; the one picked beside them. */
    public static final Arrangement<Components, SplitGrid> COMPONENTS_GRID = Arrangement.of(Components.INSTANCE,
            SplitGrid.of(SplitGrid.row(Part.of(SplitGrid.region("navigation", "components"), 1), Part.of(SplitGrid.region("details", "component"), 2))),
            ArrangedWidget.of("components", "component-navigator"),
            ArrangedWidget.of("component", "component-summary"));

    /** The one group: both workbenches, the conformance one its default. */
    public static final WorkspaceGroups GROUPS = WorkspaceGroups.of(
            WorkspaceGroup.of("conformance", "Conformance")
                    .section("Workbenches", GroupedWorkspace.of(Conformance.INSTANCE.name(), "Conformance"),
                                            GroupedWorkspace.of(Components.INSTANCE.name(), "Components"))
                    .defaultTo(WorkspaceKind.of(Conformance.INSTANCE.name()))
                    .build());

    /** What a studio serves of them: each declared, each filed, each arranged the first time. */
    public static final GroupedWorkspaces SITE = new GroupedWorkspaces(GROUPS, List.of(Conformance.INSTANCE, Components.INSTANCE))
            .arranged(WorkspaceArrangements.of(Conformance.INSTANCE, CONFORMANCE_GRID), WorkspaceArrangements.of(Components.INSTANCE, COMPONENTS_GRID));
}
