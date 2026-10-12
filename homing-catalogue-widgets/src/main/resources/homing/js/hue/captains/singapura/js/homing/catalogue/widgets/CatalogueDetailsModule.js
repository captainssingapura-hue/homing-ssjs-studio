// =============================================================================
// CatalogueDetails — the entry that is picked, as it reads on the sheet it lies
// on - flat, nothing raised off it: its badge as a kicker, its name, its
// summary, and the way to it - Browse for a catalogue, Open for a page - a real
// link to its authentic path - but for the entry the page it lies on is: there
// is no way to where one is. Until something is picked, a hint. Each is the
// house's text, or its link, placed by the details and looking as it is.
//
// It follows the catalogue party it joins: what the party says is picked, it
// reads from the site and shows. A press on its link asks the party for the
// entry to open, and the host opens it as its app says. It has no keys of its
// own: a press anywhere else in it says the reader is reading the entry shown,
// and the view's lead takes the reader there. Not joined, it shows
// nothing - there is nothing to follow - and its link is an ordinary one.
//
//   new CatalogueDetails(container, params)   params: { here? } - the address of the page it lies
//                                             on, when that is an entry's: that entry shows no way
//   details.shown()   the entry shown, or null
// =============================================================================

class CatalogueDetails extends CatalogueWidget {
    constructor(container, params) {
        super(container, "catalogueDetails", "Catalogue entry");
        this._here = (params && params.here) || null;
        this._box = this.branch.createElement("box", "div");
        css.addClass(this._box, cw_details);
        css.addClass(this._box, wg_scroll);
        this._hint = new Caption(CaptionFactory.create(this.branch, "hint"), { text: "Pick an entry to see it here." }, ROLE.STANDALONE);
        css.addClass(this._hint.el, cw_hint);
        this._box.appendChild(this._hint.el);
        this.root.appendChild(this._box);
        this._entry = null;
        this._shown = null;
        this._asked = null;
        this._entries = 0;
    }

    hears() {
        var self = this;
        return { Picked: function (m) { self._show(m.to); } };
    }

    joined() { this.tell({ kind: "CurrentRequested" }); }

    shown() { return this._shown; }

    /** The entry it is about: the one shown. */
    readingAt() { return this._shown ? this._shown.to : null; }

    _show(to) {
        var self = this;
        this._asked = to;
        this.entries.get(to).then(function (e) {
            if (!self.alive || self._asked !== to) return;
            self._put(e);
        }, function (err) {
            if (!self.alive || self._asked !== to) return;
            self._hint.text = "The entry at " + to + " could not be read: " + err.message;
            css.toggleClass(self._hint.el, cw_hidden, false);
        });
    }

    /** The entry, in place of the one before, on a branch of its own: its link a real one, a plain press on it told - none to the page's own. */
    _put(e) {
        if (this._entry) this._entry.dissolve();
        var branch = this.branch.createBranch("entry" + (++this._entries));
        branch.activate(this);
        var entry = branch.createElement("entry", "article");
        css.addClass(entry, cw_entry);
        if (e.badge) CatalogueDetails._placed(new Kicker(KickerFactory.create(branch, "kicker"), { text: e.badge }, ROLE.STANDALONE), cw_kicker, entry);
        CatalogueDetails._placed(new Heading(HeadingFactory.create(branch, "title"), { text: (e.icon ? e.icon + " " : "") + e.name, level: 2 }, ROLE.TITLE), cw_title, entry);
        if (e.summary) CatalogueDetails._placed(new Lede(LedeFactory.create(branch, "summary"), { text: e.summary }, ROLE.STANDALONE), cw_summary, entry);
        if (e.to !== this._here) {   // the page's own entry: one is there already
            var way = new Link(LinkFactory.create(branch, "way"),
                    { text: e.kind === "catalogue" ? "Browse →" : (e.opens === "new-tab" ? "Open beside ↗" : "Open →"), to: e.to }, ROLE.OPEN);
            CatalogueDetails._placed(way, cw_way, entry);
            this.takesLink(way, e);
        }
        this._box.appendChild(entry);
        this._entry = branch;
        this._shown = Object.freeze(e);
        css.toggleClass(this._hint.el, cw_hidden, true);
    }

    /** A text or a link of the house's, placed in the entry: where it sits is the details', how it looks its own. */
    static _placed(component, cls, into) {
        css.addClass(component.el, cls);
        into.appendChild(component.el);
        return component;
    }

    disposed() { if (this._entry) { this._entry.dissolve(); this._entry = null; } }
}
