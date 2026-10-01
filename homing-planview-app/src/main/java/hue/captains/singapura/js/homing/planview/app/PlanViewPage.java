package hue.captains.singapura.js.homing.planview.app;

import hue.captains.singapura.js.homing.docview.site.HoldsPlan;
import hue.captains.singapura.js.homing.server.HtmlPageContent;
import hue.captains.singapura.js.homing.site.Placed;
import hue.captains.singapura.js.homing.site.Query;
import hue.captains.singapura.js.homing.site.Trail;
import hue.captains.singapura.js.homing.site.mpa.Mpa;
import hue.captains.singapura.js.homing.studio.base.tracker.Plan;

import java.util.Objects;

/**
 * A plan, placed to be read: the page a catalogue leaf opens, holding its plan. Served, it is
 * PlanView - the same app for every plan - told the plan's authentic path, the last crumb of the
 * trail it is reached by, and the plan's name. No plan registry, no id.
 *
 * @param mpa  the site's MPA, which makes the page
 * @param plan the plan it holds
 */
public record PlanViewPage(Mpa mpa, Plan plan) implements Placed, HoldsPlan {

    public PlanViewPage {
        Objects.requireNonNull(mpa, "PlanViewPage.mpa");
        Objects.requireNonNull(plan, "PlanViewPage.plan");
    }

    @Override
    public HtmlPageContent html(Trail trail, Query query) {
        String at = trail.isEmpty() ? "" : trail.last().href();
        return mpa.page(PlanViewApp.INSTANCE, new PlanViewApp.Params(at, plan.name())).html(trail, query);
    }
}
