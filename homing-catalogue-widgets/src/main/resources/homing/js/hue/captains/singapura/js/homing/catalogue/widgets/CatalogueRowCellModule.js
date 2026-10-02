// =============================================================================
// CatalogueRowCell — a catalogue tree's row, as the domain draws it: the
// entry's icon and name, then its badge. A cell of the relation tree's
// contract (as its stock text cell is): minted once on the branch it was
// handed, told its selection mode, disposed by its owner. The tree never reads
// what it shows.
//
//   new CatalogueRowCell({ branch, entry })
//   cellElement()   onSelect(mode)   entry()   dispose()
// =============================================================================

class CatalogueRowCell {
    constructor(opts) {
        if (!opts || !opts.branch || !opts.entry) throw new Error("[CatalogueRowCell] opts.branch and opts.entry are required");
        this._entry = opts.entry;   // first: activating names the owner, and the owner's name is its entry's
        this._el = null;
        this._branch = opts.branch;
        this._branch.activate(this);
    }

    toString() { return "CatalogueRowCell " + this._entry.to; }

    entry() { return this._entry; }

    cellElement() {
        if (this._el) return this._el;
        var e = this._entry, el = this._branch.createElement("cell", "span");
        css.addClass(el, cw_row);
        var name = this._branch.createElement("name", "span");
        css.addClass(name, cw_row_name);
        name.textContent = (e.icon ? e.icon + " " : "") + e.name;
        var badge = this._branch.createElement("badge", "span");
        css.addClass(badge, cw_row_badge);
        badge.textContent = e.badge;
        el.appendChild(name);
        el.appendChild(badge);
        this._el = el;
        return el;
    }

    /** The mode is the tree's to draw on its row; the cell has nothing of its own to change. */
    onSelect(mode) {}

    dispose() { this._el = null; this._branch.dissolve(); }
}
