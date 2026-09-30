package hue.captains.singapura.js.homing.docview.site;

import hue.captains.singapura.js.homing.core.Crate;
import hue.captains.singapura.js.homing.core.CrateEntry;
import hue.captains.singapura.js.homing.core.StandardJsModuleType;
import hue.captains.singapura.js.homing.core.js.CoreJsCrate;
import hue.captains.singapura.js.homing.design.DesignCrate;
import hue.captains.singapura.js.homing.server.ServerCrate;

import java.util.List;

/** DocView on a site, as a page serves it: the inspector and its sheet. The routes are the server's. */
public final class DocViewSiteCrate implements Crate {

    public static final DocViewSiteCrate INSTANCE = new DocViewSiteCrate();

    private DocViewSiteCrate() {}

    @Override public String name() { return "homing-docview-site"; }

    @Override public List<Crate> requires() {
        return List.of(
                // the page's party the inspector mints its elements from
                CoreJsCrate.INSTANCE,
                // the css manager
                ServerCrate.INSTANCE,
                // the design words the sheet wears
                DesignCrate.INSTANCE);
    }

    @Override
    public List<CrateEntry> entries() {
        return List.of(
                CrateEntry.of(InspectorStyles.INSTANCE),
                CrateEntry.of(DocInspectorApp.INSTANCE, StandardJsModuleType.CONSUMER));
    }
}
