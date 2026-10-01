// =============================================================================
// PlanListSteward — the steward of a plan's plan-list parts: a DocPartSteward of the
// type "plan-list", reading them from the plan's payload as a doc's parts are read
// from the doc's. Hired by the root plan-list party of the page.
//
//   new PlanListSteward(tell)
// =============================================================================

class PlanListSteward extends DocPartSteward {
    constructor(tell) { super(tell, "plan-list"); }
}
