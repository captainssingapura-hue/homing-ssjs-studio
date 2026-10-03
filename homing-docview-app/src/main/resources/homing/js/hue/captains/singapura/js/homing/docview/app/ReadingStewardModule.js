// =============================================================================
// ReadingSteward — the steward of a doc reader's reading party: where the
// keyboard goes when the reader presses a widget that has no keys of its own.
// Told ReadHere, it asks the placement which section the widget sits in, tells
// the placement the reader is there - its mark on that section, the doc not
// moved - and takes the reader there in the contents: the cursor on its entry,
// the sections holding it unfolded, telling no one - so the doc does not move
// under the reader - and the contents given the keys there, into their tree.
// The contents never lose the keys to such a widget: they keep them, or have
// them back from a widget that took them. The widget never learns where it
// sits, nor what the contents are.
//
//   ReadingSteward.over({ placement, contents }) → the class a reading party hires:
//     placement   function () → the placement: pathOf(name) → the path of the section a widget sits in, or null;
//                 readAt(path) - the reader is there: marked, the doc not moved
//     contents    function () → the contents: follow(path) - the cursor there, quietly; activate() - the keys, into it
//   steward.reactors { ReadHere }
// =============================================================================

class ReadingSteward {

    static over(at) {
        if (!at || typeof at.placement !== "function" || typeof at.contents !== "function") {
            throw new Error("[ReadingSteward] over a placement and the contents: ReadingSteward.over({ placement: () => engine, contents: () => toc })");
        }
        return class extends ReadingSteward { constructor(tell) { super(tell, at); } };
    }

    constructor(tell, at) {
        if (typeof tell !== "function") throw new Error("[ReadingSteward] hired with the means to tell its party");
        if (!at) throw new Error("[ReadingSteward] hired over a placement and the contents: a party hires ReadingSteward.over({ placement, contents })");
        var self = this;
        this._tell = tell;
        this._at = at;
        this.reactors = Object.freeze({ ReadHere: function (m) { self._read(m.widget); } });
    }

    /** A widget read: its section's entry in the contents, and the keys there. Nothing for a widget the placement did not make. */
    _read(widget) {
        var placement = this._at.placement(), contents = this._at.contents();
        var path = placement && typeof placement.pathOf === "function" ? placement.pathOf(widget) : null;
        if (path === null || !contents) return;
        if (typeof placement.readAt === "function") placement.readAt(path);   // the doc's mark where the reader is, as the contents' cursor
        contents.follow(path);
        contents.activate();
    }
}
