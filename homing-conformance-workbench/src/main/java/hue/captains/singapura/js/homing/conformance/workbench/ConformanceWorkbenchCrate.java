package hue.captains.singapura.js.homing.conformance.workbench;

import hue.captains.singapura.js.homing.catalogue.widgets.CatalogueWidgetsCrate;
import hue.captains.singapura.js.homing.core.Crate;
import hue.captains.singapura.js.homing.core.CrateEntry;
import hue.captains.singapura.js.homing.core.StandardJsModuleType;
import hue.captains.singapura.js.homing.core.js.CoreJsCrate;
import hue.captains.singapura.js.homing.design.DesignCrate;
import hue.captains.singapura.js.homing.docview.widgets.DocViewWidgetsCrate;
import hue.captains.singapura.js.homing.relgrid.protocol.RelGridProtocolCrate;
import hue.captains.singapura.js.homing.reltree.RelTreeCrate;
import hue.captains.singapura.js.homing.server.ServerCrate;
import hue.captains.singapura.js.homing.ui.panzoom.UiPanZoomCrate;
import hue.captains.singapura.js.homing.workspace.parties.WorkspacePartiesCrate;
import hue.captains.singapura.js.homing.workspace.site.WorkspaceSiteCrate;
import hue.captains.singapura.js.homing.workspace.widgets.WorkspaceWidgetsCrate;

import java.util.List;

/**
 * The conformance workbench, served: its party and its secretary, its feeds' routes and the
 * report's codecs, its widgets and what they share, the workbenches' manifests, group and first
 * states, and their page.
 */
public final class ConformanceWorkbenchCrate implements Crate {

    public static final ConformanceWorkbenchCrate INSTANCE = new ConformanceWorkbenchCrate();

    private ConformanceWorkbenchCrate() {}

    @Override public String name() { return "homing-conformance-workbench"; }

    @Override public List<Crate> requires() {
        return List.of(
                // the DomOpsParty a widget mints its root from; the focus party, the keys, the css manager; the design words
                CoreJsCrate.INSTANCE, ServerCrate.INSTANCE, DesignCrate.INSTANCE,
                // the navigators' tree and its questions, and the row a catalogue tree draws
                RelTreeCrate.INSTANCE, RelGridProtocolCrate.INSTANCE, CatalogueWidgetsCrate.INSTANCE,
                // the graph: DocView's mermaid engine and SVG reader, the pan-zoom view
                DocViewWidgetsCrate.INSTANCE, UiPanZoomCrate.INSTANCE,
                // the party's runtime, what a widget is, and the grouped page the workbenches are
                WorkspacePartiesCrate.INSTANCE, WorkspaceWidgetsCrate.INSTANCE, WorkspaceSiteCrate.INSTANCE);
    }

    @Override
    public List<CrateEntry> entries() {
        return List.of(
                CrateEntry.of(WorkbenchChoiceModule.INSTANCE, StandardJsModuleType.PURE_LOGIC),
                CrateEntry.of(WorkbenchChoiceSecretaryModule.INSTANCE, StandardJsModuleType.SECRETARY),
                CrateEntry.of(WorkbenchRoutesModule.INSTANCE, StandardJsModuleType.PURE_LOGIC),
                CrateEntry.of(WorkbenchFeedsModule.INSTANCE, StandardJsModuleType.PURE_LOGIC),
                CrateEntry.of(ReportCodecsModule.INSTANCE, StandardJsModuleType.PURE_LOGIC),
                CrateEntry.of(WorkbenchStyles.INSTANCE),
                CrateEntry.of(WorkbenchWidgetModule.INSTANCE),
                CrateEntry.of(WorkbenchNavigatorModule.INSTANCE),
                CrateEntry.of(WorkbenchPaneModule.INSTANCE),
                CrateEntry.of(CrateNavigatorModule.INSTANCE),
                CrateEntry.of(CrateGraphModule.INSTANCE),
                CrateEntry.of(NodeSummaryModule.INSTANCE),
                CrateEntry.of(ModuleSourceModule.INSTANCE),
                CrateEntry.of(CrateChecksModule.INSTANCE),
                CrateEntry.of(ModuleChecksModule.INSTANCE),
                CrateEntry.of(ConformanceReportPaneModule.INSTANCE),
                CrateEntry.of(ComponentNavigatorModule.INSTANCE),
                CrateEntry.of(ComponentSummaryModule.INSTANCE),
                CrateEntry.of(WorkbenchWorkspacesModule.INSTANCE, StandardJsModuleType.PURE_LOGIC),
                CrateEntry.of(WorkbenchGroupsModule.INSTANCE, StandardJsModuleType.PURE_LOGIC),
                CrateEntry.of(WorkbenchArrangementsModule.INSTANCE, StandardJsModuleType.PURE_LOGIC),
                CrateEntry.of(WorkbenchApp.INSTANCE, StandardJsModuleType.CONSUMER));
    }
}
