package hue.captains.singapura.js.homing.planview.app;

import hue.captains.singapura.js.homing.site.catalogue.Catalogue;
import hue.captains.singapura.js.homing.site.catalogue.Leaf;
import hue.captains.singapura.js.homing.site.mpa.Mpa;
import hue.captains.singapura.js.homing.studio.base.tracker.Plan;
import hue.captains.singapura.js.homing.tree.NodeName;

/**
 * A plan as a catalogue's leaf, to be read: named and summarised as the plan is, badged with its
 * kicker - or {@code PLAN} - its page PlanView, holding it.
 */
public final class PlanViewLeaves {

    private PlanViewLeaves() {}

    /** A leaf at {@code slug} whose page is PlanView for the plan. */
    public static <C extends Catalogue<C>> Leaf<C> viewed(C host, Mpa mpa, NodeName slug, Plan plan) {
        String kicker = plan.kicker();
        return Leaf.of(host, slug, plan.name(), plan.summary(), new PlanViewPage(mpa, plan))
                .badge(kicker == null || kicker.isBlank() ? "PLAN" : kicker).icon("🗺️");
    }

    /** A leaf at the plan's own slug ({@link #slugOf}) whose page is PlanView for the plan. */
    public static <C extends Catalogue<C>> Leaf<C> viewed(C host, Mpa mpa, Plan plan) { return viewed(host, mpa, slugOf(plan), plan); }

    /** A plan's own slug, as studios have always placed it: its class's name, less {@code Plan}. */
    public static NodeName slugOf(Plan plan) { return NodeName.ofType(plan.getClass(), "Plan"); }
}
