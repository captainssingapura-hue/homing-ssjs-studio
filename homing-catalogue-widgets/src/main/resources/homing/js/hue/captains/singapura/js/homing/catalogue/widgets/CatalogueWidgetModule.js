// =============================================================================
// CatalogueWidget — what every catalogue widget is to its host, said once; a
// catalogue widget extends it and adds what it shows.
//
// A self-contained widget (the Workspace & Widgets doctrines): made with the
// container its host lends it and its params, and nothing else; its DomOps and
// focus parties its own, offered as roots for its host to graft; the catalogue
// party declared by type in Java, and joined after it is made. Not joined, it
// works alone - and opens an entry itself, as its app says it opens.
//
// THE KEYS. A catalogue view's keys are its lead's - the tree's - unless a
// widget has a designed use for them. One that has says so (static KEYS = true)
// and is a member of its own focus party: a press in it claims the keys, Escape
// gives them back. Any other is no member: joined, a press in it - not on a
// link or another control inside it, and not one that made a selection - tells
// the party where the reader is reading (Read, the entry it is about, from
// readingAt()), and the lead takes the reader there, keeping the keys.
//
// OPENING. Joined, a widget never opens anything: it tells the party Open, and
// the host - whose it is to say what opening means - acts on the party's
// Opening. A link a widget draws stays a real link (a middle press still opens
// it beside), and a plain press on it is taken by the widget and told.
//
//   class CatalogueTree extends CatalogueWidget {
//       static KEYS = true;                        keys of its own: a member, a press claims
//       constructor(container, params) { super(container, "catalogueTree", "Catalogue tree"); ... }
//       hears()  { return { Picked: m => ... }; }   the catalogue kinds it follows
//       joined() { this.tell({ kind: "CurrentRequested" }); }
//       disposed() { ...what it holds beside its branch... }
//   }
//   widget.root  widget.roots { dom, focus }  widget.focus (null with no keys of its own)  widget.branch  widget.entries
//   widget.readingAt()  the entry it is about, for a press to say where the reader is reading; null unless said
//   widget.join(given)  widget.leave()   given: { [type name]: party }
//   widget.member       its catalogue membership while joined, null otherwise
//   widget.tell(m)      told when joined, nothing when not
//   widget.pick(to)     a pick, told when joined
//   widget.open(entry)  Open told when joined; opened as its app says when not
//   widget.takes(a, entry)   a link it drew: a plain press on it opened through open()
//   widget.alive        true until it is disposed
//   widget.activate()   widget.keyDown(ev)   widget.dispose()
//   CatalogueWidget.go(entry)   what a host does on Opening: in place, or beside
// =============================================================================

var _catalogueWidgets = 0;
/** What a press on is the control's, not the reader's place: a link, a button, a field, anything focusable. */
var _CATALOGUE_CONTROLS = "a, button, input, select, textarea, summary, label, [tabindex], [contenteditable]";

class CatalogueWidget {
    constructor(container, name, label) {
        if (!container || typeof container.appendChild !== "function") throw new Error("[" + name + "] a container is required: the one its host lends it");
        var n = name + "-" + (++_catalogueWidgets);
        this._name = name;
        this._dom = domOpsParties.mobile(n);
        this._dom.activate(Object.freeze({ toString: function () { return name; } }));
        var root = this._dom.createElement("root", "div");
        css.addClass(root, wg_fill);
        root.setAttribute("role", "region");
        root.setAttribute("aria-label", label);
        this.root = root;
        this._focusParty = focusParties.mobile(n);
        var keyed = this.constructor.KEYS === true, self = this;
        this.focus = keyed ? this._focusParty.root.join(name, this) : null;
        this._off = keyed ? Keys.claimOn(root, this.focus) : null;
        if (!keyed) root.addEventListener("click", function (ev) { self._read(ev); });
        this.roots = Object.freeze({ dom: this._dom, focus: this._focusParty });
        this.entries = new CatalogueEntries();
        this.member = null;
        this.alive = true;
        this._joined = false;
        container.appendChild(root);
    }

    /** The widget's own branch: what it mints its elements and sub-branches on. */
    get branch() { return this._dom; }

    /** The catalogue kinds it follows: a reactor a kind. None, unless said. */
    hears() { return {}; }

    /** Called once it has joined what it was given. */
    joined() {}

    /** The entry it is about, for a press in it to say where the reader is reading: an authentic path, or null. None, unless said. */
    readingAt() { return null; }

    /** Called as it is disposed, before its branch goes. */
    disposed() {}

    join(given) {
        if (this._joined) throw new Error("[" + this._name + "] joined already: leave first");
        this._joined = true;
        var party = given && given[CATALOGUE.name];
        if (party) this.member = party.join(this._name, this.hears());
        this.joined();
    }

    leave() {
        if (this.member) { this.member.leave(); this.member = null; }
        this._joined = false;
    }

    tell(m) { if (this.member) this.member.tell(m); }

    pick(to) { this.tell({ kind: "Pick", to: to }); }

    /** Joined: Open told, and the host acts. Alone: opened here, as the entry's app says. */
    open(entry) {
        if (this.member) this.tell({ kind: "Open", to: entry.to, opens: entry.opens });
        else CatalogueWidget.go(entry);
    }

    /** A link the widget drew, to this entry: a plain press opened through open(); a middle or modified press left to the browser. */
    takes(a, entry) {
        var self = this;
        a.addEventListener("click", function (ev) {
            if (ev.button !== 0 || ev.ctrlKey || ev.metaKey || ev.shiftKey || ev.altKey) return;
            if (!self.member && entry.opens !== "new-tab") return;
            ev.preventDefault();
            self.open(entry);
        });
    }

    /** What opening an entry means on a page: in place, or beside - as its app says. */
    static go(entry) {
        if (entry.opens === "new-tab") HrefManagerInstance.openNew(entry.to);
        else HrefManagerInstance.navigate(entry.to);
    }

    /** Asked for the keys: claimed, with keys of its own; else the reader taken to its entry by the one that leads the keys. */
    activate() {
        if (this.focus) { Keys.claim(this.focus); return; }
        var to = this.readingAt();
        if (to) this.tell({ kind: "Read", to: to });
    }

    /** A press in a widget with no keys of its own: where the reader is reading, told - not a press on a control inside it, nor one that made a selection. */
    _read(ev) {
        if (!this.member) return;
        var t = ev.target, c = t && typeof t.closest === "function" ? t.closest(_CATALOGUE_CONTROLS) : null;
        if (c && this.root.contains(c)) return;
        var selection = document.getSelection ? document.getSelection() : null;
        if (selection && !selection.isCollapsed) return;
        var to = this.readingAt();
        if (to) this.tell({ kind: "Read", to: to });
    }

    /** Holding the keys, nothing natively focused: Escape gives them back. */
    keyDown(ev) {
        if (ev.key === "Escape") { Keys.yield(this.focus); return true; }
        return false;
    }

    dispose() {
        this.alive = false;
        this.leave();
        this.disposed();
        if (this._off) { this._off(); this._off = null; }
        this._focusParty.dissolve();
        this._dom.dissolve();
    }
}
