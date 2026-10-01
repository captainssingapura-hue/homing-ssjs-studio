package hue.captains.singapura.js.homing.docview.site;

import hue.captains.singapura.js.homing.studio.base.Doc;

/**
 * A page that holds a doc: what a doc's routes resolve its authentic path to - the catalogue
 * leaf that places the doc, whose page holds it. No doc registry, no id.
 *
 * <p>A doc may be held by more than one page - read on one, inspected on another - but it is read
 * at one: its <b>reading page</b>, whose authentic path is the doc's, and where a reference to the
 * doc goes ({@link DocPlaces}). A page says whether it is one.</p>
 */
public interface HoldsDoc {

    /** The doc the page shows. */
    Doc doc();

    /** Whether the doc is read here - the place a reference to it goes. A page that only views it, as the inspector does, is not. */
    default boolean reads() { return true; }
}
