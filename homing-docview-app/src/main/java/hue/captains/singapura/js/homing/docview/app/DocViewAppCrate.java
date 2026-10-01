package hue.captains.singapura.js.homing.docview.app;

import hue.captains.singapura.js.homing.core.Crate;
import hue.captains.singapura.js.homing.core.CrateEntry;
import hue.captains.singapura.js.homing.core.StandardJsModuleType;
import hue.captains.singapura.js.homing.core.js.CoreJsCrate;
import hue.captains.singapura.js.homing.design.DesignCrate;
import hue.captains.singapura.js.homing.docview.site.DocViewSiteCrate;
import hue.captains.singapura.js.homing.docview.widgets.DocViewWidgetsCrate;
import hue.captains.singapura.js.homing.server.ServerCrate;
import hue.captains.singapura.js.homing.ui.splitgrid.UiSplitGridCrate;
import hue.captains.singapura.js.homing.workspace.content.WorkspaceContentCrate;
import hue.captains.singapura.js.homing.workspace.parties.WorkspacePartiesCrate;
import hue.captains.singapura.js.homing.workspace.tree.WorkspaceTreeCrate;

import java.util.List;

/** DocView, the app: the page every doc is read on, and its sheet - over the primitives, the tree placement and the split grid. */
public final class DocViewAppCrate implements Crate {

    public static final DocViewAppCrate INSTANCE = new DocViewAppCrate();

    private DocViewAppCrate() {}

    @Override public String name() { return "homing-docview-app"; }

    @Override public List<Crate> requires() {
        return List.of(
                // the primitives, the doc they read from, their stewards
                DocViewWidgetsCrate.INSTANCE,
                // the doc's arrangement made from its tree
                DocViewSiteCrate.INSTANCE,
                // the tree placement's engine and its contents, and the split grid they are laid out in
                WorkspaceTreeCrate.INSTANCE, UiSplitGridCrate.INSTANCE,
                // the content parties: the runtime, their secretary
                WorkspacePartiesCrate.INSTANCE, WorkspaceContentCrate.INSTANCE,
                // the page's party, the css and href managers, the design words its sheet wears
                CoreJsCrate.INSTANCE, ServerCrate.INSTANCE, DesignCrate.INSTANCE);
    }

    @Override
    public List<CrateEntry> entries() {
        return List.of(
                CrateEntry.of(DocViewStyles.INSTANCE),
                CrateEntry.of(DocViewApp.INSTANCE, StandardJsModuleType.CONSUMER));
    }
}
