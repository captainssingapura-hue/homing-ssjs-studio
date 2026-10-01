package hue.captains.singapura.js.homing.docview.app;

import hue.captains.singapura.js.homing.site.catalogue.Catalogue;
import hue.captains.singapura.js.homing.site.catalogue.Leaf;
import hue.captains.singapura.js.homing.site.mpa.Mpa;
import hue.captains.singapura.js.homing.studio.base.AuthoredName;
import hue.captains.singapura.js.homing.studio.base.Doc;
import hue.captains.singapura.js.homing.studio.base.rigid.RigidDoc;
import hue.captains.singapura.js.homing.studio.base.rigid.RigidDocV2;
import hue.captains.singapura.js.homing.tree.NodeName;

/**
 * A doc as a catalogue's leaf, to be read: named and summarised as the doc is, badged with its
 * category, its page DocView, holding it.
 */
public final class DocViewLeaves {

    private DocViewLeaves() {}

    /** A leaf at {@code slug} whose page is DocView for the doc. */
    public static <C extends Catalogue<C>> Leaf<C> viewed(C host, Mpa mpa, NodeName slug, Doc doc) {
        String category = doc.category();
        return Leaf.of(host, slug, doc.title(), doc.summary(), new DocViewPage(mpa, doc))
                .badge(category == null || category.isBlank() ? "DOC" : category).icon("📖");
    }

    /** A leaf at the doc's own slug ({@link #slugOf}) whose page is DocView for the doc. */
    public static <C extends Catalogue<C>> Leaf<C> viewed(C host, Mpa mpa, Doc doc) { return viewed(host, mpa, slugOf(doc), doc); }

    /**
     * A doc's own slug, as studios have always placed it - so a doc keeps its address wherever it
     * is read: the name its author gave it; else, for a rigid doc, its title made concise; else
     * its class's name, less {@code Doc}.
     */
    public static NodeName slugOf(Doc doc) {
        if (doc instanceof AuthoredName named && named.authoredSlug() != null) return named.authoredSlug();
        if (doc instanceof RigidDoc || doc instanceof RigidDocV2) return NodeName.conciseSlug(doc.title());
        return NodeName.ofType(doc.getClass(), "Doc");
    }
}
