package hue.captains.singapura.js.homing.catalogue.demo.notes;

import hue.captains.singapura.js.homing.site.catalogue.L0_Catalogue;
import hue.captains.singapura.js.homing.site.catalogue.L1_Catalogue;
import hue.captains.singapura.js.homing.site.catalogue.Leaf;
import hue.captains.singapura.js.homing.site.mpa.Mpa;

import java.util.List;

/**
 * The notes app's tree, written alone: a root, a journal and a shelf of ideas.
 * It knows no site - a site grafts it wherever it wants it, and hands in its
 * MPA, and the notes are made pages of that site with it.
 */
public record NotesCatalogue() implements L0_Catalogue<NotesCatalogue> {

    public static final NotesCatalogue INSTANCE = new NotesCatalogue();

    @Override public String name() { return "Notes"; }
    @Override public String summary() { return "A journal, and ideas worth keeping"; }
    @Override public String icon() { return "🗒"; }

    @Override public List<? extends L1_Catalogue<NotesCatalogue, ?>> subCatalogues() {
        return List.of(JournalCatalogue.INSTANCE, IdeasCatalogue.INSTANCE);
    }

    @Override public List<Leaf<NotesCatalogue>> leaves(Mpa mpa) {
        return List.of(Notes.leaf(this, mpa, "About these notes", "", "What they are, and where they came from",
                "Notes are an app of their own: a page and a tree, written knowing no site.\n\n"
                + "A site grafts the tree where it wants it, and every note becomes a page of that site - its chrome, its themes, its trail."));
    }

    /** The journal: a note a day. */
    public record JournalCatalogue() implements L1_Catalogue<NotesCatalogue, JournalCatalogue> {
        public static final JournalCatalogue INSTANCE = new JournalCatalogue();
        @Override public NotesCatalogue parent() { return NotesCatalogue.INSTANCE; }
        @Override public String name() { return "Journal"; }
        @Override public String summary() { return "A note a day"; }
        @Override public List<Leaf<JournalCatalogue>> leaves(Mpa mpa) {
            return List.of(
                    Notes.leaf(this, mpa, "Monday", "29 Sep", "The workspace stood up on its own",
                            "Groups, a switcher the page summons, and the authentic path back on the new stack.\n\nThe platformer played and watched."),
                    Notes.leaf(this, mpa, "Tuesday", "30 Sep", "The desk moved, and the catalogue grew grafts",
                            "The trader's workspace on the new stack, as a pilot.\n\nThen the studio: catalogues first, knowing no app."));
        }
    }

    /** Ideas worth keeping. */
    public record IdeasCatalogue() implements L1_Catalogue<NotesCatalogue, IdeasCatalogue> {
        public static final IdeasCatalogue INSTANCE = new IdeasCatalogue();
        @Override public NotesCatalogue parent() { return NotesCatalogue.INSTANCE; }
        @Override public String name() { return "Ideas"; }
        @Override public String icon() { return "💡"; }
        @Override public List<Leaf<IdeasCatalogue>> leaves(Mpa mpa) {
            return List.of(
                    Notes.leaf(this, mpa, "The address is the position", "", "Authentic paths",
                            "A page is reached at its position in the catalogue, and the breadcrumb is read off the address - never looked up."),
                    Notes.leaf(this, mpa, "Trees compose by grafting", "", "One position per site",
                            "A tree is written alone and grafted where a site wants it. Within a site, every vertex and page has one position."));
        }
    }
}
