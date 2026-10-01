// =============================================================================
// PlanViewApp — a plan, viewed. The same app for every plan: it is told which by
// its params - the plan's authentic path - and nothing else. It is DocView's
// desk (DocDesk) with a plan's widgets offered beside a doc's, and their
// parties: the contents beside the plan, its pillars and its phases its
// sections, every part a widget asking its content party - a phase's details
// folded in its widget until they are asked for.
// =============================================================================

/** The widget types a plan's tree places: its head, its lists, its phases. */
var _PLAN_KINDS = Object.freeze({ "plan-head": PlanHead, "plan-list": PlanList, "plan-phase": PlanPhase });

function appMain(el, params) {
    DocDesk.open(el, params.plan, params.title, {
        kinds: _PLAN_KINDS,
        parties: function (given) {
            given[PLAN_HEAD.name] = new MessagingParty(PLAN_HEAD, ContentSecretary, PlanHeadSteward);
            given[PLAN_LIST.name] = new MessagingParty(PLAN_LIST, ContentSecretary, PlanListSteward);
            given[PLAN_PHASE.name] = new MessagingParty(PLAN_PHASE, ContentSecretary, PlanPhaseSteward);
        }
    });
}
