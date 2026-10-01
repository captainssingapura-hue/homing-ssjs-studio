package hue.captains.singapura.js.homing.catalogue.demo;

import hue.captains.singapura.js.homing.docview.reference.ReferencePlans;
import hue.captains.singapura.js.homing.planview.app.PlanViewLeaves;
import hue.captains.singapura.js.homing.site.catalogue.L0_Catalogue;
import hue.captains.singapura.js.homing.site.catalogue.Leaf;
import hue.captains.singapura.js.homing.site.mpa.Mpa;
import hue.captains.singapura.js.homing.tree.NodeName;

import java.util.List;

/** The reference plans, placed: one leaf each - DocView's own phases among them - its page PlanView, holding it. */
public record PlansCatalogue() implements L0_Catalogue<PlansCatalogue> {

    public static final PlansCatalogue INSTANCE = new PlansCatalogue();

    @Override public String name() { return "Plans"; }
    @Override public NodeName slug() { return new NodeName("plans"); }
    @Override public String summary() { return "Plans read on DocView's desk: their pillars and phases as sections, a phase's details unfolded on demand"; }

    @Override
    public List<Leaf<PlansCatalogue>> leaves(Mpa mpa) {
        return ReferencePlans.ALL.entrySet().stream().map(e -> PlanViewLeaves.viewed(this, mpa, new NodeName(e.getKey()), e.getValue())).toList();
    }
}
