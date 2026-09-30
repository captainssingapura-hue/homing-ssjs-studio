package hue.captains.singapura.js.homing.catalogue.site;

import hue.captains.singapura.js.homing.core.Crate;
import hue.captains.singapura.js.homing.core.CrateEntry;
import hue.captains.singapura.js.homing.core.StandardJsModuleType;
import hue.captains.singapura.js.homing.core.js.CoreJsCrate;
import hue.captains.singapura.js.homing.design.DesignCrate;
import hue.captains.singapura.js.homing.server.ServerCrate;

import java.util.List;

/**
 * The catalogue as a site shows it: the listing's sheet, the listing, and the
 * page it is. A site that serves its catalogues with {@link AppListing} requires it.
 */
public final class CatalogueSiteCrate implements Crate {

    public static final CatalogueSiteCrate INSTANCE = new CatalogueSiteCrate();

    private CatalogueSiteCrate() {}

    @Override public String name() { return "homing-catalogue-site"; }

    @Override public List<Crate> requires() {
        return List.of(
                // the DomOpsParty the listing mints from
                CoreJsCrate.INSTANCE,
                // the css manager and the href manager
                ServerCrate.INSTANCE,
                // the design words the sheet wears
                DesignCrate.INSTANCE);
    }

    @Override
    public List<CrateEntry> entries() {
        return List.of(
                CrateEntry.of(ListingStyles.INSTANCE),
                CrateEntry.of(CatalogueListingModule.INSTANCE, StandardJsModuleType.CONSUMER),
                CrateEntry.of(CatalogueListingApp.INSTANCE, StandardJsModuleType.CONSUMER));
    }
}
