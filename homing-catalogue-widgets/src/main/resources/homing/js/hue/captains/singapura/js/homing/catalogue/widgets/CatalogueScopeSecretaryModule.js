// =============================================================================
// CatalogueScopeSecretary — the secretary of a composed widget's own catalogue
// party: the scope where its subordinates meet (the catalogue browser's tree
// and details). Within the scope it is the catalogue secretary
// (CatalogueChoiceSecretary), whose state it keeps whole; what it adds is the
// scope's edge - what goes up to the party the composed widget joined, and what
// is taken from it.
//
// What goes up is declared, kind by kind (BUBBLES): a pick made in the scope,
// when it changed what is picked; an asking to open, always - opening is the
// host's, and the host is above. A question is answered here. From above, a
// pick is taken as the scope's own: told to every member of the scope when it
// changes, never sent back up. Anything else from above - the host's opening
// among it - is the scope's to pass over. Diligent: how often it sent up, kept
// to itself, and took from above, beside the scope's state.
//
//   state  { choice: CatalogueChoiceSecretary's state, bubbled: n, kept: n, adopted: n }
//
// Pure: no DOM, no clock, no console; the state handed in is never changed.
// =============================================================================

var CatalogueScopeSecretary = {

    initial: { choice: CatalogueChoiceSecretary.initial, bubbled: 0, kept: 0, adopted: 0 },

    /** What a member's word does at the scope's edge: up when it says so - a pick only when it changed what is picked. */
    BUBBLES: Object.freeze({ Pick: "changed", Open: "always", CurrentRequested: "never" }),

    behavior: function (state, envelope) {
        if (envelope.from === "upstream") return CatalogueScopeSecretary.fromAbove(state, envelope);
        var m = envelope.message, step = CatalogueChoiceSecretary.behavior(state.choice, envelope);
        var rule = CatalogueScopeSecretary.BUBBLES[m.kind];
        var up = rule === "always" || (rule === "changed" && step.newState.changes !== state.choice.changes);
        return {
            newState: { choice: step.newState, bubbled: state.bubbled + (up ? 1 : 0), kept: state.kept + (up ? 0 : 1), adopted: state.adopted },
            actions: up ? step.actions.concat([{ kind: "SendToParent", message: m }]) : step.actions
        };
    },

    /** What the party above says: a pick taken as the scope's own, told to the scope, never sent back up; the rest passed over. */
    fromAbove: function (state, envelope) {
        var m = envelope.message;
        if (m.kind !== "Picked") return { newState: state, actions: [] };
        var step = CatalogueChoiceSecretary.behavior(state.choice, { from: "upstream", message: { kind: "Pick", to: m.to } });
        return { newState: { choice: step.newState, bubbled: state.bubbled, kept: state.kept, adopted: state.adopted + (step.actions.length ? 1 : 0) },
                 actions: step.actions };
    }
};
