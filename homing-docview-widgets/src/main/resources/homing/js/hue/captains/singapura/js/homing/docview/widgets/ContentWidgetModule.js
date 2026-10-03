// =============================================================================
// ContentWidget — what every primitive of a doc is: a widget made from its type
// and its params alone - the doc's address and the part's key - which never
// learns where it sits. Joined, it asks its type's content party for its
// content with those params, and draws it when it comes; until then, and when
// the party says the content is unavailable, it says so. Given no party of its
// type, it stands alone, and says that. A primitive that shows more than its
// own type asks another party with the same params (_ask). One whose class says
// STAGEABLE offers itself to the stage, when the page gave it one (_offerStage):
// a button that asks for it by its name. It never learns where it sits, nor
// that it moved: it fills the box it is in - its content's height in the flow
// of a doc, all of a box its host sizes.
//
// Self-contained: its DomOps party and its focus party are its own, offered as
// roots for its host to graft. It flows: as tall as its content.
//
// The keys: in a doc reader they are the contents', unless a widget has a
// designed use for them. A primitive with one - a drawing that zooms and pans -
// says which control the keys go into (_keysInto), and is from then a member of
// its own focus party, never focused itself (RFC 0066 E3, keyboard §15.1, §17):
// a press in it claims the keys, the browser's focus in a control inside it
// makes it the holder, its Escape yields; a control inside it letting go of the
// keys is a yield from the control - the widget, asked first, says nothing and
// is passed by, so the keys go up to the root's default, the contents. Any
// other primitive takes no keys: a press in it - not on a link or a button
// inside it, and not one that made a selection - says where the reader is
// reading to the reading party (ReadHere, by its own name), whose steward takes
// the reader to its section in the contents, which keep the keys.
//
//   class X extends ContentWidget { constructor(container, params) { super(container, params, TYPE, "x"); }
//                                   _draw(content, branch, into) { … } }
//   w.root  w.roots { dom, focus }   w.join(given)  w.leave()   w.dispose()
//   w._ask(type, { Content(content), Unavailable(why) })   - for a primitive's own use, once joined
//   w._offerStage(branch) → the button's element, or null: no stage given
//   w._keysInto(control)  - control: the element the keys go into when given, or a function answering it now
//   w.focus  its membership, once it has keys of its own, else null   w.activate()
//   w.state() → "alone" | "waiting" | "shown" | "unavailable" - on the root as data-state too
// =============================================================================

var _contentWidgets = 0;
/** What a press on is the control's, not the reader's place: a link, a button, a field, anything focusable. */
var _CONTROLS = "a, button, input, select, textarea, summary, label, [tabindex], [contenteditable]";

class ContentWidget {

    /** A doc's primitive is as tall as its content. */
    static SIZING = "flow";

    constructor(container, params, type, name) {
        if (!container || typeof container.appendChild !== "function") throw new Error("[" + name + "] a container is required: the one its host lends it");
        if (!type || !type.name) throw new Error("[" + name + "] the content type it asks of is required");
        this._name = name + "-" + (++_contentWidgets);
        this._type = type;
        this._params = ContentParams.of(params);
        this._dom = domOpsParties.mobile(this._name);
        this._dom.activate(this);
        this._focusParty = focusParties.mobile(this._name);
        this._control = null;
        var root = this._dom.createElement("root", "div"), self = this;
        css.addClass(root, dw_widget);
        this._note = this._dom.createElement("note", "p");
        css.addClass(this._note, dw_note);
        root.appendChild(this._note);
        container.appendChild(root);
        this.root = root;
        this.roots = Object.freeze({ dom: this._dom, focus: this._focusParty });
        this.focus = null;        // its membership of its own focus party: only once it has keys of its own
        this._offKeys = null;
        this._reader = null;      // its membership of the reading party, when the page gave one
        root.addEventListener("click", function (ev) { self._read(ev); });
        this._member = null;
        this._given = null;
        this._also = [];
        this._at("alone", "Not asked for: no " + type.name + " party here.");
    }

    toString() { return this._name; }

    /** Joined: its content asked for, with its params. */
    join(given) {
        if (this._member) throw new Error("[" + this._name + "] joined already: leave first");
        var party = given && given[this._type.name], reading = given && given[READING.name], self = this;
        if (reading && !this._reader) this._reader = reading.join(this._name, {});
        if (!party) return;
        this._given = given;
        this._member = party.join(this._name, {
            Content: function (m) { if (ContentParams.same(m.params, self._params)) self._show(m.content); },
            Unavailable: function (m) { if (ContentParams.same(m.params, self._params)) self._at("unavailable", "Unavailable: " + m.why); }
        });
        if (this._state === "alone") this._at("waiting", "Waiting for its content.");
        this._member.tell({ kind: "Wanted", params: this._params });
    }

    leave() {
        if (this._reader) { this._reader.leave(); this._reader = null; }
        this._also.forEach(function (m) { m.leave(); });
        this._also = [];
        this._given = null;
        if (this._member) { this._member.leave(); this._member = null; }
    }

    /**
     * Another type's content, asked with the same params - for a primitive that shows more than
     * its own type: a code part, its diagram. Told unavailable at once when no party of the type is
     * where the widget joined.
     */
    _ask(type, on) {
        var party = this._given && this._given[type.name], self = this;
        if (!party) { on.Unavailable("no " + type.name + " party here"); return; }
        var member = party.join(this._name, {
            Content: function (m) { if (ContentParams.same(m.params, self._params)) on.Content(m.content); },
            Unavailable: function (m) { if (ContentParams.same(m.params, self._params)) on.Unavailable(m.why); }
        });
        this._also.push(member);
        member.tell({ kind: "Wanted", params: this._params });
    }

    /** The stage offered, when the page gave one: a button that asks for this widget by its name - the one its placement keeps it by. */
    _offerStage(branch) {
        var stage = this._given && this._given[STAGE.name];
        if (!stage) return null;
        return new StageButton(branch.createBranch("toStage"), { party: stage, widget: this._name }).root;
    }

    /**
     * Keys of its own: the control they go into when the widget is given them by a press or a call - an element, or
     * a function answering it now. Said once, the widget joins its focus party and a press in it claims; said again,
     * the control changes.
     */
    _keysInto(control) {
        this._control = control || null;
        if (this.focus) return;
        this.focus = this._focusParty.root.join("widget", this);
        this._offKeys = Keys.claimOn(this.root, this.focus);
    }

    /** Asked for the keys: a claim, what a press in it does. Nothing, for a primitive with no keys of its own. */
    activate() { if (this.focus) Keys.claim(this.focus); }

    /**
     * A press in a primitive with no keys of its own: the reader is reading here, said to the reading party by its own
     * name. Not a press on a link, a button or another control inside it - that is the control's - and not one that
     * made a selection: the reader is taking text.
     */
    _read(ev) {
        if (this.focus || !this._reader) return;
        var t = ev.target, c = t && typeof t.closest === "function" ? t.closest(_CONTROLS) : null;
        if (c && this.root.contains(c)) return;
        var selection = document.getSelection ? document.getSelection() : null;
        if (selection && !selection.isCollapsed) return;
        this._reader.tell({ kind: "ReadHere", widget: this._name });
    }

    /** Given the keys by a press or a call: into its control, when it has one - not when the browser's focus arriving is what gave them. */
    granted(by) {
        if (by === "native") return;
        var c = typeof this._control === "function" ? this._control() : this._control;
        if (c && typeof c.focus === "function") c.focus({ preventScroll: true });
    }

    /** Held with nothing focused: Escape yields, up the tree. */
    keyDown(ev) {
        if (ev.key === "Escape") { Keys.yield(this.focus); return true; }
        return false;
    }

    state() { return this._state; }

    dispose() {
        this.leave();
        if (this._offKeys) { this._offKeys(); this._offKeys = null; }
        this._focusParty.dissolve();
        this._dom.dissolve();
    }

    /** Its content, drawn once: a part does not change. */
    _show(content) {
        if (this._state === "shown") return;
        var branch = this._dom.createBranch("content");
        branch.activate(this);
        try {
            this._draw(content, branch, this.root);
            this._at("shown", "");
        } catch (e) {
            this._at("unavailable", "It could not be drawn: " + String(e && e.message || e));
        }
    }

    _draw() { throw new Error("[" + this._name + "] a primitive draws its own content"); }

    _at(state, text) {
        this._state = state;
        this.root.setAttribute("data-state", state);
        this._note.textContent = text;
        css.toggleClass(this._note, dw_hidden, !text);
    }
}
