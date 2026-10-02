package hue.captains.singapura.js.homing.planview.widgets;

import hue.captains.singapura.js.homing.core.Crate;
import hue.captains.singapura.js.homing.core.CrateEntry;
import hue.captains.singapura.js.homing.core.StandardJsModuleType;
import hue.captains.singapura.js.homing.core.js.CoreJsCrate;
import hue.captains.singapura.js.homing.design.DesignCrate;
import hue.captains.singapura.js.homing.docview.widgets.DocViewWidgetsCrate;
import hue.captains.singapura.js.homing.server.ServerCrate;
import hue.captains.singapura.js.homing.workspace.content.WorkspaceContentCrate;

import java.util.List;

/**
 * A plan's widgets: its head, its lists, its phases - each made from its type and params alone,
 * asking its content party, as a doc's primitives are; their content types and stewards; the
 * marks they set; their sheet.
 */
public final class PlanViewWidgetsCrate implements Crate {

    public static final PlanViewWidgetsCrate INSTANCE = new PlanViewWidgetsCrate();

    private PlanViewWidgetsCrate() {}

    @Override public String name() { return "homing-planview-widgets"; }

    @Override public List<Crate> requires() {
        return List.of(
                // the base every primitive is, the steward of a payload's parts, markdown made into elements, the doc's sheet
                DocViewWidgetsCrate.INSTANCE,
                // the content parties' params and secretary
                WorkspaceContentCrate.INSTANCE,
                // the DomOpsParty, the css and href managers, the design words the sheet wears
                CoreJsCrate.INSTANCE, ServerCrate.INSTANCE, DesignCrate.INSTANCE);
    }

    @Override
    public List<CrateEntry> entries() {
        return List.of(
                CrateEntry.of(PlanStyles.INSTANCE),
                // the content types, as the page has them, and their stewards
                CrateEntry.of(PlanHeadContentModule.INSTANCE, StandardJsModuleType.PURE_LOGIC),
                CrateEntry.of(PlanListContentModule.INSTANCE, StandardJsModuleType.PURE_LOGIC),
                CrateEntry.of(PlanPhaseContentModule.INSTANCE, StandardJsModuleType.PURE_LOGIC),
                CrateEntry.of(PlanHeadStewardModule.INSTANCE, StandardJsModuleType.PURE_LOGIC),
                CrateEntry.of(PlanListStewardModule.INSTANCE, StandardJsModuleType.PURE_LOGIC),
                CrateEntry.of(PlanPhaseStewardModule.INSTANCE, StandardJsModuleType.PURE_LOGIC),
                // the marks they set, and the widgets
                CrateEntry.of(PlanMarksModule.INSTANCE, StandardJsModuleType.CONSUMER),
                CrateEntry.of(PlanHeadModule.INSTANCE, StandardJsModuleType.CONSUMER),
                CrateEntry.of(PlanListModule.INSTANCE, StandardJsModuleType.CONSUMER),
                CrateEntry.of(PlanPhaseModule.INSTANCE, StandardJsModuleType.CONSUMER));
    }
}
