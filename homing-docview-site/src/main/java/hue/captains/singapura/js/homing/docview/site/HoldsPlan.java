package hue.captains.singapura.js.homing.docview.site;

import hue.captains.singapura.js.homing.studio.base.tracker.Plan;

/**
 * A page that holds a plan: what the payload route resolves a plan's authentic path to - the
 * catalogue leaf that places the plan, whose page holds it - as it does a doc's ({@link HoldsDoc}).
 * No plan registry, no id: the plan's address is its page's.
 */
public interface HoldsPlan {

    /** The plan the page shows. */
    Plan plan();
}
