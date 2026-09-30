// =============================================================================
// CatalogueDetails — the entry that is picked, as a card: its name, its badge,
// its summary, and the way to it - Browse for a catalogue, Open for a page - a
// real link to its authentic path. Until something is picked, a hint.
//
// It follows the catalogue party it joins: what the party says is picked, it
// reads from the site and shows. A press on its link asks the party for the
// entry to open, and the host opens it as its app says. Not joined, it shows
// nothing - there is nothing to follow - and its link is an ordinary one.
//
//   new CatalogueDetails(container, params)   params: none
//   details.shown()   the entry shown, or null
// =============================================================================

class CatalogueDetails extends CatalogueWidget {
    constructor(container, params) {
        super(container, "catalogueDetails", "Catalogue entry");
        this._box = this.branch.createElement("box", "div");
        css.addClass(this._box, cw_details);
        css.addClass(this._box, wg_scroll);
        this._hint = this.branch.createElement("hint", "p");
        css.addClass(this._hint, cw_hint);
        this._hint.textContent = "Pick an entry to see it here.";
        this._box.appendChild(this._hint);
        this.root.appendChild(this._box);
        this._card = null;
        this._shown = null;
        this._asked = null;
        this._cards = 0;
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

    /** The entry's card, in place of the one before: its link a real one, a plain press on it told. */
    _put(e) {
        if (this._card) this._card.dispose();
        var card = new CardBuilder()
            .title((e.icon ? e.icon + " " : "") + e.name)
            .badge(e.badge)
            .text(e.summary)
            .link(e.to, e.kind === "catalogue" ? "Browse →" : (e.opens === "new-tab" ? "Open beside ↗" : "Open →"))
            .build(this.branch.createBranch("card" + (++this._cards)));
        var link = card.branch.getElement("link");   // the card's way, by the name it minted it under
        if (link) this.takes(link, e);
        this._box.appendChild(card.root);
        this._card = card;
        this._shown = Object.freeze(e);
        css.toggleClass(this._hint, cw_hidden, true);
    }

    disposed() { if (this._card) { this._card.dispose(); this._card = null; } }
}
