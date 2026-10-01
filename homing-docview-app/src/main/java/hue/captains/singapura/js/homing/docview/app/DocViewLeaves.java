package hue.captains.singapura.js.homing.docview.app;

import hue.captains.singapura.js.homing.site.catalogue.Catalogue;
import hue.captains.singapura.js.homing.site.catalogue.Leaf;
import hue.captains.singapura.js.homing.site.mpa.Mpa;
import hue.captains.singapura.js.homing.studio.base.Doc;
import hue.captains.singapura.js.homing.tree.NodeName;

/** A doc as a catalogue's leaf, to be read: named and summarised as the doc is, its page DocView, holding it. */
public final class DocViewLeaves {

    private DocViewLeaves() {}

    /** A leaf at {@code slug} whose page is DocView for the doc. */
    public static <C extends Catalogue<C>> Leaf<C> viewed(C host, Mpa mpa, NodeName slug, Doc doc) {
        return Leaf.of(host, slug, doc.title(), doc.summary(), new DocViewPage(mpa, doc)).badge("DOC").icon("📖");
    }
}
