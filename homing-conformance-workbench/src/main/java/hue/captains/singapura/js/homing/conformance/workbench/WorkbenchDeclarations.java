package hue.captains.singapura.js.homing.conformance.workbench;

import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.workspace.parties.PartyType;
import hue.captains.singapura.js.homing.workspace.widgets.NoParams;
import hue.captains.singapura.js.homing.workspace.widgets.WidgetDeclaration;
import hue.captains.singapura.js.homing.workspace.widgets.WidgetQuery;

import java.util.List;

/**
 * The workbench's widgets, declared: each joins the workbench party - the navigators to tell
 * what is picked and opened, the panes to follow it; the graph and the report, which show the
 * whole, join it all the same. None takes params: what each shows is the studio's, by its feeds.
 */
public final class WorkbenchDeclarations {

    private WorkbenchDeclarations() {}

    private static final List<PartyType<?>> WORKBENCH = List.of(WorkbenchChoice.TYPE);

    /** A workbench widget's declaration, said once: its kind, its title, the class it is. */
    private interface Workbench extends WidgetDeclaration<NoParams> {
        @Override default Class<NoParams> paramsType() { return NoParams.class; }
        @Override default WidgetQuery<NoParams> query() { return new NoParams.Query(); }
        @Override default List<PartyType<?>> parties() { return WORKBENCH; }
    }

    /** {@code crate-navigator}: the studio's crates, their packages, their modules. */
    public record Crates() implements Workbench {
        public static final Crates INSTANCE = new Crates();
        @Override public String kind() { return "crate-navigator"; }
        @Override public String title() { return "Crates"; }
        @Override public ModuleImports<?> constructs() { return new ModuleImports<>(List.of(new CrateNavigatorModule.CrateNavigator()), CrateNavigatorModule.INSTANCE); }
    }

    /** {@code crate-graph}: the crates and what each requires. */
    public record Graph() implements Workbench {
        public static final Graph INSTANCE = new Graph();
        @Override public String kind() { return "crate-graph"; }
        @Override public String title() { return "Dependency graph"; }
        @Override public ModuleImports<?> constructs() { return new ModuleImports<>(List.of(new CrateGraphModule.CrateGraph()), CrateGraphModule.INSTANCE); }
    }

    /** {@code node-summary}: the node picked. */
    public record Summary() implements Workbench {
        public static final Summary INSTANCE = new Summary();
        @Override public String kind() { return "node-summary"; }
        @Override public String title() { return "Summary"; }
        @Override public ModuleImports<?> constructs() { return new ModuleImports<>(List.of(new NodeSummaryModule.NodeSummary()), NodeSummaryModule.INSTANCE); }
    }

    /** {@code module-source}: a module opened, as it is served. */
    public record Source() implements Workbench {
        public static final Source INSTANCE = new Source();
        @Override public String kind() { return "module-source"; }
        @Override public String title() { return "Full content"; }
        @Override public ModuleImports<?> constructs() { return new ModuleImports<>(List.of(new ModuleSourceModule.ModuleSource()), ModuleSourceModule.INSTANCE); }
    }

    /** {@code crate-checks}: the crate of the node picked, and how it stands. */
    public record CrateChecks() implements Workbench {
        public static final CrateChecks INSTANCE = new CrateChecks();
        @Override public String kind() { return "crate-checks"; }
        @Override public String title() { return "Crate conformance"; }
        @Override public ModuleImports<?> constructs() { return new ModuleImports<>(List.of(new CrateChecksModule.CrateChecks()), CrateChecksModule.INSTANCE); }
    }

    /** {@code module-checks}: the module picked, and its findings. */
    public record ModuleChecks() implements Workbench {
        public static final ModuleChecks INSTANCE = new ModuleChecks();
        @Override public String kind() { return "module-checks"; }
        @Override public String title() { return "Module conformance"; }
        @Override public ModuleImports<?> constructs() { return new ModuleImports<>(List.of(new ModuleChecksModule.ModuleChecks()), ModuleChecksModule.INSTANCE); }
    }

    /** {@code conformance-report}: the report the build exported. */
    public record Report() implements Workbench {
        public static final Report INSTANCE = new Report();
        @Override public String kind() { return "conformance-report"; }
        @Override public String title() { return "Conformance report"; }
        @Override public ModuleImports<?> constructs() { return new ModuleImports<>(List.of(new ConformanceReportPaneModule.ConformanceReportPane()), ConformanceReportPaneModule.INSTANCE); }
    }

    /** {@code component-navigator}: the components the crates deliver. */
    public record Components() implements Workbench {
        public static final Components INSTANCE = new Components();
        @Override public String kind() { return "component-navigator"; }
        @Override public String title() { return "Components"; }
        @Override public ModuleImports<?> constructs() { return new ModuleImports<>(List.of(new ComponentNavigatorModule.ComponentNavigator()), ComponentNavigatorModule.INSTANCE); }
    }

    /** {@code component-summary}: the component node picked. */
    public record Component() implements Workbench {
        public static final Component INSTANCE = new Component();
        @Override public String kind() { return "component-summary"; }
        @Override public String title() { return "Component"; }
        @Override public ModuleImports<?> constructs() { return new ModuleImports<>(List.of(new ComponentSummaryModule.ComponentSummary()), ComponentSummaryModule.INSTANCE); }
    }

    /** The conformance workbench's kinds: the crates' physical side. */
    public static final List<WidgetDeclaration<?>> CONFORMANCE = List.of(Crates.INSTANCE, Graph.INSTANCE, Summary.INSTANCE, Source.INSTANCE,
            CrateChecks.INSTANCE, ModuleChecks.INSTANCE, Report.INSTANCE);

    /** The components workbench's kinds: the crates' logical side. */
    public static final List<WidgetDeclaration<?>> COMPONENTS = List.of(Components.INSTANCE, Component.INSTANCE);
}
