package hue.captains.singapura.js.homing.planview.app;

import hue.captains.singapura.js.homing.core.Crate;
import hue.captains.singapura.js.homing.core.CrateEntry;
import hue.captains.singapura.js.homing.core.StandardJsModuleType;
import hue.captains.singapura.js.homing.docview.app.DocViewAppCrate;
import hue.captains.singapura.js.homing.planview.widgets.PlanViewWidgetsCrate;
import hue.captains.singapura.js.homing.workspace.content.WorkspaceContentCrate;
import hue.captains.singapura.js.homing.workspace.parties.WorkspacePartiesCrate;

import java.util.List;

/** PlanView, the app: the page every plan is read on - DocView's desk, with a plan's widgets offered beside a doc's. */
public final class PlanViewAppCrate implements Crate {

    public static final PlanViewAppCrate INSTANCE = new PlanViewAppCrate();

    private PlanViewAppCrate() {}

    @Override public String name() { return "homing-planview-app"; }

    @Override public List<Crate> requires() {
        return List.of(
                // the desk, and everything it lays a tree out with
                DocViewAppCrate.INSTANCE,
                // a plan's widgets, their content types and stewards
                PlanViewWidgetsCrate.INSTANCE,
                // the content parties: the runtime, their secretary
                WorkspacePartiesCrate.INSTANCE, WorkspaceContentCrate.INSTANCE);
    }

    @Override
    public List<CrateEntry> entries() { return List.of(CrateEntry.of(PlanViewApp.INSTANCE, StandardJsModuleType.CONSUMER)); }
}
