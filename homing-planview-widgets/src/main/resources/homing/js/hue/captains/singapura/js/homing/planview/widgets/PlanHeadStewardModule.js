// =============================================================================
// PlanHeadSteward — the steward of a plan's plan-head parts: a DocPartSteward of the
// type "plan-head", reading them from the plan's payload as a doc's parts are read
// from the doc's. Hired by the root plan-head party of the page.
//
//   new PlanHeadSteward(tell)
// =============================================================================

class PlanHeadSteward extends DocPartSteward {
    constructor(tell) { super(tell, "plan-head"); }
}
