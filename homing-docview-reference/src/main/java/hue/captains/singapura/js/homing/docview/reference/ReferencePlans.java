package hue.captains.singapura.js.homing.docview.reference;

import hue.captains.singapura.js.homing.studio.base.tracker.Plan;

import java.util.Map;

/** DocView's reference plans: what the plan view is demonstrated with and validated against. */
public final class ReferencePlans {

    private ReferencePlans() {}

    /** DocView's own phases. */
    public static final DocViewPlan DOCVIEW = new DocViewPlan();

    /** Every reference plan, by the slug it is placed at. */
    public static final Map<String, Plan> ALL = Map.of("docview", DOCVIEW);
}
