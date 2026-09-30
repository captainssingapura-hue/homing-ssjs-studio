// =============================================================================
// CatalogueCards — one catalogue as cards: its badge, its name, its summary,
// then what is under it - its catalogues (its own and the trees it grafts,
// alike), then its pages - each a raised tile that is a real link to its
// authentic path.
//
// Joined to a catalogue party, the cards follow a picked catalogue - they show
// it, so a tree picking beside them browses through them; a press on any tile
// asks for its entry to open, and the host opens it as its app says (a
// catalogue in place: its own listing). Not joined, every tile is an ordinary
// link - but a page its app opens beside is opened beside.
//
//   new CatalogueCards(container, params)   params: { at } - the catalogue shown first, "/" by default
//   cards.shown()   the catalogue shown, or null
// =============================================================================

class CatalogueCards extends CatalogueWidget {
    constructor(container, params) {
        super(container, "catalogueCards", "Catalogue cards");
        var box = this._box = this.branch.createElement("box", "div");
        css.addClass(box, cw_cards);
        css.addClass(box, wg_scroll);
        this._kicker = this._el(this.branch, "kicker", "p", cw_kicker, box);
        this._title = this._el(this.branch, "title", "h1", cw_title, box);
        this._summary = this._el(this.branch, "summary", "p", cw_summary, box);
        this._note = this._el(this.branch, "note", "p", cw_hint, box);
        this.root.appendChild(box);
        this._sections = null;
        this._shown = null;
        this._asked = null;
        this._show((params && params.at) || "/");
    }

    hears() {
        var self = this;
        return { Picked: function (m) { self._show(m.to, true); } };
    }

    shown() { return this._shown; }

    /** The catalogue at `to`, shown - a page picked is not a catalogue, and is passed over. */
    _show(to, followed) {
        var self = this;
        this._asked = to;
        this.entries.get(to).then(function (e) {
            if (!self.alive || self._asked !== to) return;
            if (e.kind !== "catalogue") return;
            self._put(e);
        }, function (err) {
            if (!self.alive || self._asked !== to || followed) return;
            self._note.textContent = "The catalogue at " + to + " could not be read: " + err.message;
            css.setClass(self._note, cw_failed);
        });
    }

    _put(e) {
        this._shown = Object.freeze(e);
        this._kicker.textContent = e.badge;
        this._title.textContent = (e.icon ? e.icon + " " : "") + e.name;
        this._summary.textContent = e.summary;
        css.toggleClass(this._summary, cw_hidden, !e.summary);
        if (this._sections) this._sections.dissolve();
        var b = this._sections = this.branch.createBranch("sections");
        b.activate(this);
        var catalogues = e.children.filter(function (c) { return c.kind === "catalogue"; });
        var pages = e.children.filter(function (c) { return c.kind !== "catalogue"; });
        if (catalogues.length) this._box.appendChild(this._section(b, "catalogues", "Catalogues", catalogues));
        if (pages.length) this._box.appendChild(this._section(b, "pages", "Pages", pages));
        this._note.textContent = e.children.length ? "" : "Nothing here yet.";
        css.toggleClass(this._note, cw_hidden, e.children.length > 0);
    }

    _section(b, name, heading, entries) {
        var box = this._el(b, name, "section", null, null), self = this;
        box.setAttribute("aria-label", heading);
        this._el(b, name + "-title", "h2", cw_section_title, box).textContent = heading;
        var grid = this._el(b, name + "-grid", "div", cw_grid, box);
        entries.forEach(function (e, i) { grid.appendChild(self._tile(b, name + i, e)); });
        return box;
    }

    /** A tile: a real link to the entry's authentic path; a plain press on it opened as its app says - through the party, joined. */
    _tile(b, key, e) {
        var tile = this._el(b, key, "a", cw_tile, null);
        HrefManagerInstance.set(tile, e.to);
        this._el(b, key + "-name", "span", cw_tile_name, tile).textContent = (e.icon ? e.icon + " " : "") + e.name;
        this._el(b, key + "-badge", "span", cw_tile_badge, tile).textContent = e.badge;
        if (e.summary) this._el(b, key + "-summary", "span", cw_tile_summary, tile).textContent = e.summary;
        this.takes(tile, e);
        return tile;
    }

    _el(branch, name, tag, cls, parent) {
        var el = branch.createElement(name, tag);
        if (cls) css.addClass(el, cls);
        if (parent) parent.appendChild(el);
        return el;
    }
}
