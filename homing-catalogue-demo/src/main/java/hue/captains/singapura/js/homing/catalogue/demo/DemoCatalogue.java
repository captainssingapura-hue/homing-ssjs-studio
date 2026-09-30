package hue.captains.singapura.js.homing.catalogue.demo;

import hue.captains.singapura.js.homing.catalogue.demo.notes.Notes;
import hue.captains.singapura.js.homing.catalogue.demo.notes.NotesCatalogue;
import hue.captains.singapura.js.homing.catalogue.demo.recipes.RecipesCatalogue;
import hue.captains.singapura.js.homing.site.catalogue.Graft;
import hue.captains.singapura.js.homing.site.catalogue.L0_Catalogue;
import hue.captains.singapura.js.homing.site.catalogue.Leaf;
import hue.captains.singapura.js.homing.site.mpa.Mpa;

import java.util.List;

/**
 * The demo site's root: two apps' trees grafted - the notes as they show
 * themselves, the recipes shown here as the kitchen - and a note of the site's
 * own, placed the way the notes app places one. Neither app knows this site;
 * the site knows only their roots.
 */
public record DemoCatalogue() implements L0_Catalogue<DemoCatalogue> {

    public static final DemoCatalogue INSTANCE = new DemoCatalogue();

    @Override public String name() { return "Catalogue demo"; }
    @Override public String summary() { return "Two apps' trees, grafted into one site: every page at its authentic path, in one chrome"; }

    @Override
    public List<Graft<DemoCatalogue>> grafts() {
        return List.of(Graft.of(this, NotesCatalogue.INSTANCE),
                       Graft.of(this, RecipesCatalogue.INSTANCE).shownAs("Kitchen", "What we cook - the recipes app's tree, shown here as the kitchen"),
                       Graft.of(this, ReferenceDocsCatalogue.INSTANCE));
    }

    @Override
    public List<Leaf<DemoCatalogue>> leaves(Mpa mpa) {
        return List.of(Notes.leaf(this, mpa, "Welcome", "", "What this site is",
                "This site knows two apps only by their trees: notes, and recipes - which it shows as the kitchen.\n\n"
                + "Each tree was written alone, knowing no site. Grafted here, every catalogue and every page has one "
                + "position, and its address is that position: the breadcrumb is read off it.\n\n"
                + "Every page - a note, a recipe, a catalogue's listing - is made with this site's MPA, so all wear one chrome."));
    }
}
