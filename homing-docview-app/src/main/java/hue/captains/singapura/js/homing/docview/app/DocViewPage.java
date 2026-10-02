package hue.captains.singapura.js.homing.docview.app;

import hue.captains.singapura.js.homing.docview.site.HoldsDoc;
import hue.captains.singapura.js.homing.server.HtmlPageContent;
import hue.captains.singapura.js.homing.site.Placed;
import hue.captains.singapura.js.homing.site.Query;
import hue.captains.singapura.js.homing.site.Trail;
import hue.captains.singapura.js.homing.site.mpa.Mpa;
import hue.captains.singapura.js.homing.studio.base.Doc;

import java.util.Objects;

/**
 * A doc, placed to be read: the page a catalogue leaf opens, holding its doc. Served, it is
 * DocView - the same app for every doc - told the doc's authentic path, the last crumb of the
 * trail it is reached by, and the doc's title. No doc registry, no id.
 *
 * @param mpa the site's MPA, which makes the page
 * @param doc the doc it holds
 */
public record DocViewPage(Mpa mpa, Doc doc) implements Placed, HoldsDoc {

    public DocViewPage {
        Objects.requireNonNull(mpa, "DocViewPage.mpa");
        Objects.requireNonNull(doc, "DocViewPage.doc");
    }

    @Override
    public HtmlPageContent html(Trail trail, Query query) {
        String at = trail.isEmpty() ? "" : trail.last().href();
        return mpa.page(DocViewApp.INSTANCE, new DocViewApp.Params(at, doc.title())).html(trail, query);
    }
}
