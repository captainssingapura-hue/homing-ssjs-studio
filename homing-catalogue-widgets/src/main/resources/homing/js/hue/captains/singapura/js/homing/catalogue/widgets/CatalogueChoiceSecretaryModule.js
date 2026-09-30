// =============================================================================
// CatalogueChoiceSecretary — the secretary of a catalogue party: which entry of
// the site's catalogue the widgets that meet in it are about, said to every
// member when it changes and to a member that asks; and the asking to open an
// entry, said to every member - the host among them, whose it is to act on.
// Diligent (Diligent Secretaries): its state answers an operator's questions -
// what is picked, who picked it, how often it changed, how often an entry was
// opened, what came that it does not handle.
//
//   state  { picked: an entry's authentic path, or "" - none; lastPickedBy: a member's id | null;
//            changes: n; opened: n; recentUnknown: [{ kind, from }] - the last few }
//
//   Pick { to }            picked := to, unless it is picked already; Picked to every member.
//                          The same again is nothing - so a member that shows what it hears
//                          and tells what it shows does not echo for ever
//   CurrentRequested       Picked to the member that asked, alone - when one is picked
//   Open { to, opens }     Opening { to, opens } to every member: the host acts on it, as the
//                          entry's app says it opens
//   anything else          kept in recentUnknown, nothing done: Picked and Opening are the
//                          party's own words, never a member's
//
// Pure: no DOM, no clock, no console; the state handed in is never changed.
// =============================================================================

var CatalogueChoiceSecretary = {

    initial: { picked: "", lastPickedBy: null, changes: 0, opened: 0, recentUnknown: [] },

    /** How many unknown messages are kept. */
    UNKNOWN_KEPT: 10,

    behavior: function (state, envelope) {
        var m = envelope.message;
        switch (m.kind) {

            case "Pick":
                if (m.to === state.picked) return { newState: state, actions: [] };
                return { newState: Object.assign({}, state, { picked: m.to, lastPickedBy: envelope.from, changes: state.changes + 1 }),
                         actions: [{ kind: "BroadcastToMembers", message: { kind: "Picked", to: m.to } }] };

            case "CurrentRequested":
                if (state.picked === "") return { newState: state, actions: [] };
                return { newState: state, actions: [{ kind: "SendToMember", to: envelope.from, message: { kind: "Picked", to: state.picked } }] };

            case "Open":
                return { newState: Object.assign({}, state, { opened: state.opened + 1 }),
                         actions: [{ kind: "BroadcastToMembers", message: { kind: "Opening", to: m.to, opens: m.opens } }] };

            default: {
                var unknown = state.recentUnknown.concat([{ kind: m.kind, from: envelope.from }]).slice(-CatalogueChoiceSecretary.UNKNOWN_KEPT);
                return { newState: Object.assign({}, state, { recentUnknown: unknown }), actions: [] };
            }
        }
    }
};
