package hue.captains.singapura.js.homing.docview.gallery;

import hue.captains.singapura.js.homing.catalogue.site.AppListing;
import hue.captains.singapura.js.homing.designs.HomingDesigns;
import hue.captains.singapura.js.homing.site.Path;
import hue.captains.singapura.js.homing.site.Router;
import hue.captains.singapura.js.homing.site.Site;
import hue.captains.singapura.js.homing.site.catalogue.CatalogueRouter;
import hue.captains.singapura.js.homing.site.mpa.Brand;
import hue.captains.singapura.js.homing.site.mpa.StandardMpa;

/** The DocView widgets gallery, as a site: its catalogue at the root, every page in the site's one chrome. */
public record GallerySite() implements Site {

    public static final GallerySite INSTANCE = new GallerySite();

    public static final StandardMpa MPA = StandardMpa.of(Brand.of("DocView widgets"), HomingDesigns.REGISTRY, DocViewGalleryCrate.INSTANCE);

    public static final CatalogueRouter ROUTER = CatalogueRouter.at(Path.ROOT, GalleryCatalogue.INSTANCE, MPA).listing(AppListing.INSTANCE);

    @Override public String name() { return "docview-gallery"; }

    @Override public Router router() { return ROUTER; }
}
