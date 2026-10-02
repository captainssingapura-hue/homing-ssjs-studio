package hue.captains.singapura.js.homing.catalogue.demo.notes;

import hue.captains.singapura.js.homing.site.catalogue.Catalogue;
import hue.captains.singapura.js.homing.site.catalogue.Leaf;
import hue.captains.singapura.js.homing.site.mpa.Mpa;

/**
 * How the notes app places a note: a leaf in a catalogue, its page the note app
 * made with the site's MPA - so a site, or another app's tree, can place a note
 * of its own the same way.
 */
public final class Notes {

    private Notes() {}

    /** A note, placed in {@code host}: its name the note's title, its page made with the site's {@code mpa}. */
    public static <C extends Catalogue<C>> Leaf<C> leaf(C host, Mpa mpa, String title, String when, String summary, String text) {
        return Leaf.of(host, title, summary, mpa.page(NoteApp.INSTANCE, new NoteApp.Params(title, when, text))).badge("NOTE").icon("🗒");
    }
}
