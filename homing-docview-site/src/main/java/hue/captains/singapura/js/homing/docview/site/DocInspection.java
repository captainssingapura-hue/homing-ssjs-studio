package hue.captains.singapura.js.homing.docview.site;

import hue.captains.singapura.js.homing.server.HtmlPageContent;
import hue.captains.singapura.js.homing.site.Placed;
import hue.captains.singapura.js.homing.site.Query;
import hue.captains.singapura.js.homing.site.Trail;
import hue.captains.singapura.js.homing.site.mpa.Mpa;
import hue.captains.singapura.js.homing.studio.base.Doc;

import java.util.Objects;

/**
 * A doc, placed to be inspected: the page a catalogue leaf opens, holding its doc. Served, it
 * is the inspector for that doc, told the doc's authentic path - the last crumb of the trail it
 * is reached by - so it asks for the doc by the address it is at.
 *
 * @param mpa the site's MPA, which makes the page
 * @param doc the doc it holds
 */
public record DocInspection(Mpa mpa, Doc doc) implements Placed, HoldsDoc {

    public DocInspection {
        Objects.requireNonNull(mpa, "DocInspection.mpa");
        Objects.requireNonNull(doc, "DocInspection.doc");
    }

    /** Inspected here, not read: a reference to the doc goes to its reading page. */
    @Override public boolean reads() { return false; }

    @Override
    public HtmlPageContent html(Trail trail, Query query) {
        String at = trail.isEmpty() ? "" : trail.last().href();
        return mpa.page(DocInspectorApp.INSTANCE, new DocInspectorApp.Params(at, doc.title())).html(trail, query);
    }
}
