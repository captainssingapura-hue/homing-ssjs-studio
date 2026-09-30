package hue.captains.singapura.js.homing.catalogue.demo;

import hue.captains.singapura.js.homing.docview.reference.ReferenceDocs;
import hue.captains.singapura.js.homing.docview.site.DocLeaves;
import hue.captains.singapura.js.homing.site.catalogue.L0_Catalogue;
import hue.captains.singapura.js.homing.site.catalogue.Leaf;
import hue.captains.singapura.js.homing.site.mpa.Mpa;
import hue.captains.singapura.js.homing.tree.NodeName;

import java.util.List;

/**
 * DocView's reference docs, placed: one leaf for each - a markdown doc, a rigid doc, a rigid doc
 * by names, a composed doc - its page holding it, inspected: its tree, its arrangement, and the
 * payload a page loads, read by the doc's authentic path.
 */
public record ReferenceDocsCatalogue() implements L0_Catalogue<ReferenceDocsCatalogue> {

    public static final ReferenceDocsCatalogue INSTANCE = new ReferenceDocsCatalogue();

    @Override public String name() { return "Reference docs"; }
    @Override public NodeName slug() { return new NodeName("reference"); }
    @Override public String summary() { return "DocView's reference docs, each inspected: its tree, its arrangement, its payload"; }

    @Override
    public List<Leaf<ReferenceDocsCatalogue>> leaves(Mpa mpa) {
        return ReferenceDocs.ALL.entrySet().stream().map(e -> DocLeaves.inspected(this, mpa, new NodeName(e.getKey()), e.getValue())).toList();
    }
}
