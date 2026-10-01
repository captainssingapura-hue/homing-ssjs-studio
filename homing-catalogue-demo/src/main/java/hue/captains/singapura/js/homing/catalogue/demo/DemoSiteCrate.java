package hue.captains.singapura.js.homing.catalogue.demo;

import hue.captains.singapura.js.homing.catalogue.demo.notes.NotesCrate;
import hue.captains.singapura.js.homing.catalogue.demo.recipes.RecipesCrate;
import hue.captains.singapura.js.homing.catalogue.site.CatalogueSiteCrate;
import hue.captains.singapura.js.homing.core.Crate;
import hue.captains.singapura.js.homing.core.CrateEntry;

import hue.captains.singapura.js.homing.docview.app.DocViewAppCrate;
import hue.captains.singapura.js.homing.docview.site.DocViewSiteCrate;
import java.util.List;

/**
 * What the demo site serves: the listing, and the two apps' pages. It has no
 * module of its own - its tree is Java, served as the pages it places.
 */
public final class DemoSiteCrate implements Crate {

    public static final DemoSiteCrate INSTANCE = new DemoSiteCrate();

    private DemoSiteCrate() {}

    @Override public String name() { return "homing-catalogue-demo"; }

    @Override public List<Crate> requires() {
        return List.of(CatalogueSiteCrate.INSTANCE, NotesCrate.INSTANCE, RecipesCrate.INSTANCE, DocViewSiteCrate.INSTANCE, DocViewAppCrate.INSTANCE);
    }

    @Override public List<CrateEntry> entries() { return List.of(); }
}
