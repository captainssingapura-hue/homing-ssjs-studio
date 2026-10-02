package hue.captains.singapura.js.homing.docview.gallery;

import hue.captains.singapura.js.homing.catalogue.site.CatalogueSiteCrate;
import hue.captains.singapura.js.homing.core.Crate;
import hue.captains.singapura.js.homing.core.CrateEntry;
import hue.captains.singapura.js.homing.core.StandardJsModuleType;
import hue.captains.singapura.js.homing.core.js.CoreJsCrate;
import hue.captains.singapura.js.homing.design.DesignCrate;
import hue.captains.singapura.js.homing.docview.site.DocViewSiteCrate;
import hue.captains.singapura.js.homing.docview.widgets.DocViewWidgetsCrate;
import hue.captains.singapura.js.homing.server.ServerCrate;
import hue.captains.singapura.js.homing.workspace.content.WorkspaceContentCrate;
import hue.captains.singapura.js.homing.workspace.parties.WorkspacePartiesCrate;

import java.util.List;

/** The DocView widgets gallery: its page, its specimens, its sheet - over the primitives, the listing and the inspector. */
public final class DocViewGalleryCrate implements Crate {

    public static final DocViewGalleryCrate INSTANCE = new DocViewGalleryCrate();

    private DocViewGalleryCrate() {}

    @Override public String name() { return "homing-docview-gallery"; }

    @Override public List<Crate> requires() {
        return List.of(
                // the listing at each catalogue, and the inspector at each reference doc
                CatalogueSiteCrate.INSTANCE, DocViewSiteCrate.INSTANCE,
                // the primitives the gallery shows
                DocViewWidgetsCrate.INSTANCE,
                // the page's content parties: the runtime, their secretary
                WorkspacePartiesCrate.INSTANCE, WorkspaceContentCrate.INSTANCE,
                // the page's party, the css manager, the design words its sheet wears
                CoreJsCrate.INSTANCE, ServerCrate.INSTANCE, DesignCrate.INSTANCE);
    }

    @Override
    public List<CrateEntry> entries() {
        return List.of(
                CrateEntry.of(GalleryStyles.INSTANCE),
                CrateEntry.of(SpecimensModule.INSTANCE, StandardJsModuleType.PURE_LOGIC),
                CrateEntry.of(WidgetGalleryApp.INSTANCE, StandardJsModuleType.CONSUMER));
    }
}
