// =============================================================================
// PlanPhaseSteward — the steward of a plan's plan-phase parts: a DocPartSteward of the
// type "plan-phase", reading them from the plan's payload as a doc's parts are read
// from the doc's. Hired by the root plan-phase party of the page.
//
//   new PlanPhaseSteward(tell)
// =============================================================================

class PlanPhaseSteward extends DocPartSteward {
    constructor(tell) { super(tell, "plan-phase"); }
}
