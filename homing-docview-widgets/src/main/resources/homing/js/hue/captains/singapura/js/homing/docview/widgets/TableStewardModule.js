// =============================================================================
// TableSteward — the steward of a doc's table parts: a DocPartSteward of the type
// "table", hired by the root table party of the page, and by nothing linked below it.
//
//   new TableSteward(tell)
// =============================================================================

class TableSteward extends DocPartSteward {
    constructor(tell) { super(tell, "table"); }
}
