package hue.captains.singapura.js.homing.docview.site;

import hue.captains.singapura.js.homing.studio.base.Doc;

/**
 * A page that holds a doc: what a doc's routes resolve its authentic path to - the catalogue
 * leaf that places the doc, whose page holds it. No doc registry, no id.
 */
public interface HoldsDoc {

    /** The doc the page shows. */
    Doc doc();
}
