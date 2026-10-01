// =============================================================================
// CatalogueDetails — the entry that is picked, as it reads on the sheet it lies
// on - flat, nothing raised off it: its badge as a kicker, its name, its
// summary, and the way to it - Browse for a catalogue, Open for a page - a real
// link to its authentic path - but for the entry the page it lies on is: there
// is no way to where one is. Until something is picked, a hint.
//
// It follows the catalogue party it joins: what the party says is picked, it
// reads from the site and shows. A press on its link asks the party for the
// entry to open, and the host opens it as its app says. Not joined, it shows
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
        this._hint = this.branch.createElement("hint", "p");
        css.addClass(this._hint, cw_hint);
        this._hint.textContent = "Pick an entry to see it here.";
        this._box.appendChild(this._hint);
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

    _show(to) {
        var self = this;
        this._asked = to;
        this.entries.get(to).then(function (e) {
            if (!self.alive || self._asked !== to) return;
            self._put(e);
        }, function (err) {
            if (!self.alive || self._asked !== to) return;
            self._hint.textContent = "The entry at " + to + " could not be read: " + err.message;
            css.setClass(self._hint, cw_failed);
            css.toggleClass(self._hint, cw_hidden, false);
        });
    }

    /** The entry, in place of the one before, on a branch of its own: its link a real one, a plain press on it told - none to the page's own. */
    _put(e) {
        if (this._entry) this._entry.dissolve();
        var branch = this.branch.createBranch("entry" + (++this._entries));
        branch.activate(this);
        var entry = branch.createElement("entry", "article");
        css.addClass(entry, cw_entry);
        if (e.badge) CatalogueDetails._text(branch, "kicker", "p", cw_kicker, e.badge, entry);
        CatalogueDetails._text(branch, "title", "h2", cw_title, (e.icon ? e.icon + " " : "") + e.name, entry);
        if (e.summary) CatalogueDetails._text(branch, "summary", "p", cw_summary, e.summary, entry);
        if (e.to !== this._here) {   // the page's own entry: one is there already
            var way = CatalogueDetails._text(branch, "way", "a", cw_way,
                    e.kind === "catalogue" ? "Browse →" : (e.opens === "new-tab" ? "Open beside ↗" : "Open →"), entry);
            HrefManagerInstance.set(way, e.to);
            this.takes(way, e);
        }
        this._box.appendChild(entry);
        this._entry = branch;
        this._shown = Object.freeze(e);
        css.toggleClass(this._hint, cw_hidden, true);
    }

    static _text(branch, name, tag, cls, text, into) {
        var el = branch.createElement(name, tag);
        css.addClass(el, cls);
        el.textContent = text;
        into.appendChild(el);
        return el;
    }

    disposed() { if (this._entry) { this._entry.dissolve(); this._entry = null; } }
}
