package hue.captains.singapura.js.homing.catalogue.site;

import hue.captains.singapura.js.homing.site.Placed;
import hue.captains.singapura.js.homing.site.catalogue.Catalogue;
import hue.captains.singapura.js.homing.site.catalogue.CatalogueRouter;
import hue.captains.singapura.js.homing.site.catalogue.Listing;

/**
 * The listing a site shows at each catalogue: a page of the site's MPA - the
 * catalogue listing app ({@link CatalogueListingApp}) handed the catalogue's
 * address - so the listing wears the chrome, themes and preferences every
 * other page of the site does, and is told its trail as they are. It asks the
 * router's tree for the MPA the tree's pages were made with: a site that serves
 * its listings so reads its tree with its MPA.
 *
 * <pre>
 *   CatalogueRouter.at(Path.ROOT, SiteCatalogue.INSTANCE, MPA).listing(AppListing.INSTANCE)
 * </pre>
 */
public final class AppListing implements Listing {

    public static final AppListing INSTANCE = new AppListing();

    private AppListing() {}

    @Override
    public Placed pageFor(Catalogue<?> catalogue, CatalogueRouter router) {
        var mpa = router.tree().mpa().orElseThrow(() -> new IllegalStateException(
                "A catalogue's listing is a page of the site's MPA, and this tree was read without one:"
                + " read it with CatalogueRouter.at(mount, root, mpa)"));
        return mpa.page(CatalogueListingApp.INSTANCE, new CatalogueListingApp.Params(router.hrefOf(catalogue)));
    }
}
