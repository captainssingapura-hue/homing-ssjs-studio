// =============================================================================
// ReadingSecretary — the secretary of a doc reader's reading party. A widget
// with no keys of its own, pressed, says where the reader is reading
// (ReadHere {widget}); the steward is sent it, and does what it does. The last
// widget read is kept for whoever looks.
//
//   state  { last: widget | null }
//
//   ReadHere { widget }   from a member: to the steward
//   anything else         nothing
//
// Pure: no DOM, no clock, no console; the state handed in is never changed.
// =============================================================================

var ReadingSecretary = {

    initial: { last: null },

    behavior: function (state, envelope) {
        var m = envelope.message || {};
        if (envelope.name === "steward" || envelope.from === "unrouted") return { newState: state, actions: [] };
        if (m.kind === "ReadHere" && typeof m.widget === "string") return { newState: { last: m.widget }, actions: [{ kind: "SendToSteward", message: m }] };
        return { newState: state, actions: [] };
    }
};
