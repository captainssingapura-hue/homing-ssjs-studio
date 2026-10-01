package hue.captains.singapura.js.homing.catalogue.demo;

import hue.captains.singapura.js.homing.docview.app.DocViewLeaves;
import hue.captains.singapura.js.homing.docview.reference.ReferenceDocs;
import hue.captains.singapura.js.homing.docview.site.DocLeaves;
import hue.captains.singapura.js.homing.site.catalogue.L0_Catalogue;
import hue.captains.singapura.js.homing.site.catalogue.L1_Catalogue;
import hue.captains.singapura.js.homing.site.catalogue.Leaf;
import hue.captains.singapura.js.homing.site.mpa.Mpa;
import hue.captains.singapura.js.homing.tree.NodeName;

import java.util.List;

/**
 * DocView's reference docs, placed: one leaf for each - a markdown doc, a rigid doc, a rigid doc
 * by names, a composed doc - its page DocView, holding it; and beside them the inspector, the same
 * docs seen as their first stage: the tree, the arrangement, the payload.
 */
public record ReferenceDocsCatalogue() implements L0_Catalogue<ReferenceDocsCatalogue> {

    public static final ReferenceDocsCatalogue INSTANCE = new ReferenceDocsCatalogue();

    @Override public String name() { return "Reference docs"; }
    @Override public NodeName slug() { return new NodeName("reference"); }
    @Override public String summary() { return "DocView's reference docs, each read in DocView - and inspected beside them"; }

    @Override
    public List<? extends L1_Catalogue<ReferenceDocsCatalogue, ?>> subCatalogues() { return List.of(Inspector.INSTANCE); }

    @Override
    public List<Leaf<ReferenceDocsCatalogue>> leaves(Mpa mpa) {
        return ReferenceDocs.ALL.entrySet().stream().map(e -> DocViewLeaves.viewed(this, mpa, new NodeName(e.getKey()), e.getValue())).toList();
    }

    /** The reference docs inspected: each its tree, its arrangement, its payload. */
    public record Inspector() implements L1_Catalogue<ReferenceDocsCatalogue, Inspector> {

        public static final Inspector INSTANCE = new Inspector();

        @Override public ReferenceDocsCatalogue parent() { return ReferenceDocsCatalogue.INSTANCE; }
        @Override public String name() { return "Inspector"; }
        @Override public NodeName slug() { return new NodeName("inspector"); }
        @Override public String summary() { return "The reference docs as their first stage: the tree, the arrangement, the payload"; }

        @Override
        public List<Leaf<Inspector>> leaves(Mpa mpa) {
            return ReferenceDocs.ALL.entrySet().stream().map(e -> DocLeaves.inspected(this, mpa, new NodeName(e.getKey()), e.getValue())).toList();
        }
    }
}
