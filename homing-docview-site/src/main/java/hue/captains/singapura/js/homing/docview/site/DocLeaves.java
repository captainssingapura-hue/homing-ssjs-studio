package hue.captains.singapura.js.homing.docview.site;

import hue.captains.singapura.js.homing.site.catalogue.Catalogue;
import hue.captains.singapura.js.homing.site.catalogue.Leaf;
import hue.captains.singapura.js.homing.site.mpa.Mpa;
import hue.captains.singapura.js.homing.studio.base.Doc;
import hue.captains.singapura.js.homing.tree.NodeName;

/** A doc as a catalogue's leaf: named and summarised as the doc is, its page holding it. */
public final class DocLeaves {

    private DocLeaves() {}

    /** A leaf at {@code slug} whose page inspects the doc: its tree, its arrangement, its payload. */
    public static <C extends Catalogue<C>> Leaf<C> inspected(C host, Mpa mpa, NodeName slug, Doc doc) {
        return Leaf.of(host, slug, doc.title(), doc.summary(), new DocInspection(mpa, doc)).badge("DOC").icon("📄");
    }
}
