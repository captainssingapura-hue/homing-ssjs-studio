package hue.captains.singapura.js.homing.planview.app;

import hue.captains.singapura.js.homing.site.catalogue.Catalogue;
import hue.captains.singapura.js.homing.site.catalogue.Leaf;
import hue.captains.singapura.js.homing.site.mpa.Mpa;
import hue.captains.singapura.js.homing.studio.base.tracker.Plan;
import hue.captains.singapura.js.homing.tree.NodeName;

/** A plan as a catalogue's leaf, to be read: named and summarised as the plan is, its page PlanView, holding it. */
public final class PlanViewLeaves {

    private PlanViewLeaves() {}

    /** A leaf at {@code slug} whose page is PlanView for the plan. */
    public static <C extends Catalogue<C>> Leaf<C> viewed(C host, Mpa mpa, NodeName slug, Plan plan) {
        return Leaf.of(host, slug, plan.name(), plan.summary(), new PlanViewPage(mpa, plan)).badge("PLAN").icon("🗺️");
    }
}
