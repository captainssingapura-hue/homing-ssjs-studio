package hue.captains.singapura.js.homing.catalogue.site;

import hue.captains.singapura.js.homing.catalogue.widgets.CatalogueWidgetsCrate;
import hue.captains.singapura.js.homing.core.Crate;
import hue.captains.singapura.js.homing.core.CrateEntry;
import hue.captains.singapura.js.homing.core.StandardJsModuleType;
import hue.captains.singapura.js.homing.core.js.CoreJsCrate;
import hue.captains.singapura.js.homing.server.ServerCrate;
import hue.captains.singapura.js.homing.ui.elements.UiElementsCrate;
import hue.captains.singapura.js.homing.workspace.parties.WorkspacePartiesCrate;

import java.util.List;

/**
 * The catalogue as a site shows it: a catalogue's page, the catalogue widgets' host,
 * and its sheet. A site that serves its catalogues with {@link AppListing} requires it.
 */
public final class CatalogueSiteCrate implements Crate {

    public static final CatalogueSiteCrate INSTANCE = new CatalogueSiteCrate();

    private CatalogueSiteCrate() {}

    @Override public String name() { return "homing-catalogue-site"; }

    @Override public List<Crate> requires() {
        return List.of(
                // the page's DomOps party
                CoreJsCrate.INSTANCE,
                // the page's focus party, the css manager
                ServerCrate.INSTANCE,
                // the widgets it hosts, and the party they meet in
                CatalogueWidgetsCrate.INSTANCE,
                WorkspacePartiesCrate.INSTANCE,
                // the view buttons
                UiElementsCrate.INSTANCE);
    }

    @Override
    public List<CrateEntry> entries() {
        return List.of(
                CrateEntry.of(ListingStyles.INSTANCE),
                CrateEntry.of(CatalogueListingApp.INSTANCE, StandardJsModuleType.CONSUMER));
    }
}
