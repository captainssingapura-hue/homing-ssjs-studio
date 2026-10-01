// =============================================================================
// ProseSteward — the steward of a doc's prose parts: a DocPartSteward of the type
// "prose", hired by the root prose party of the page, and by nothing linked below it.
//
//   new ProseSteward(tell)
// =============================================================================

class ProseSteward extends DocPartSteward {
    constructor(tell) { super(tell, "prose"); }
}
