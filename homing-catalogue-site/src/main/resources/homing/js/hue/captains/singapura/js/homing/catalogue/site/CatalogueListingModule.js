// =============================================================================
// CatalogueListing — a catalogue as a site shows it: its badge, its name, its
// summary, then what is under it - its catalogues, the ones it has and the
// trees it grafts, alike; then its pages - each a tile that is a link to the
// address the router minted for it, its authentic path. What it shows it reads
// from the site (/catalogue/vertex), by the catalogue's own address.
//
// A branch component: it takes the sub-branch its caller made for it. It knows
// no app: a page is a name, a summary, a badge, an icon and an address.
//
//   new CatalogueListing(branch, path)   path: the catalogue's address
//   listing.root      what the caller appends
//   listing.shown()   the catalogue as read, or null until it is
//   listing.dispose()
// =============================================================================

var _VERTEX = "/catalogue/vertex";

class CatalogueListing {
    constructor(branch, path) {
        if (!branch || typeof branch.createElement !== "function") throw new Error("[CatalogueListing] a branch is required: the one its caller made for it");
        this._branch = branch;
        branch.activate(this);
        this._gone = false;
        this._vertex = null;
        var root = this._el("root", "div", cl_root);
        root.setAttribute("role", "region");
        root.setAttribute("aria-label", "Catalogue");
        this._kicker = this._el("kicker", "p", cl_kicker);
        this._title = this._el("title", "h1", cl_title);
        this._summary = this._el("summary", "p", cl_summary);
        this._catalogues = this._section("catalogues", "Catalogues");
        this._pages = this._section("pages", "Pages");
        this._note = this._el("note", "p", cl_empty);
        [this._kicker, this._title, this._summary, this._catalogues.box, this._pages.box, this._note].forEach(function (e) { root.appendChild(e); });
        this.root = root;
        var self = this;
        fetch(_VERTEX + "?path=" + encodeURIComponent(path))
            .then(function (r) { if (!r.ok) throw new Error("HTTP " + r.status); return r.json(); })
            .then(function (v) { if (!self._gone) self._show(v); })
            .catch(function (e) { if (!self._gone) self._failed(path, e); });
    }

    toString() { return "CatalogueListing"; }

    shown() { return this._vertex; }

    _show(v) {
        this._vertex = Object.freeze(v);
        this._kicker.textContent = v.badge;
        this._title.textContent = (v.icon ? v.icon + " " : "") + v.name;
        this._summary.textContent = v.summary;
        css.toggleClass(this._summary, cl_hidden, !v.summary);
        this._fill(this._catalogues, v.catalogues, "c");
        this._fill(this._pages, v.pages, "p");
        this._note.textContent = v.catalogues.length || v.pages.length ? "" : "Nothing here yet.";
        css.toggleClass(this._note, cl_hidden, v.catalogues.length > 0 || v.pages.length > 0);
    }

    _failed(path, e) {
        css.setClass(this._note, cl_failed);
        this._note.textContent = "The catalogue at " + path + " could not be read: " + (e && e.message ? e.message : e);
        css.toggleClass(this._catalogues.box, cl_hidden, true);
        css.toggleClass(this._pages.box, cl_hidden, true);
    }

    _section(name, heading) {
        var box = this._el(name, "section", null);
        box.setAttribute("aria-label", heading);
        var title = this._el(name + "-title", "h2", cl_section_title);
        title.textContent = heading;
        var grid = this._el(name + "-grid", "div", cl_grid);
        box.appendChild(title);
        box.appendChild(grid);
        css.toggleClass(box, cl_hidden, true);
        return { box: box, grid: grid };
    }

    /** A section's tiles: a link each, to the address the router minted. */
    _fill(section, entries, key) {
        var self = this;
        css.toggleClass(section.box, cl_hidden, entries.length === 0);
        entries.forEach(function (e, i) {
            var tile = self._el(key + i, "a", cl_tile);
            HrefManagerInstance.set(tile, e.to);
            var name = self._el(key + i + "-name", "span", cl_tile_name);
            name.textContent = (e.icon ? e.icon + " " : "") + e.name;
            var badge = self._el(key + i + "-badge", "span", cl_tile_badge);
            badge.textContent = e.badge;
            tile.appendChild(name);
            tile.appendChild(badge);
            if (e.summary) {
                var summary = self._el(key + i + "-summary", "span", cl_tile_summary);
                summary.textContent = e.summary;
                tile.appendChild(summary);
            }
            section.grid.appendChild(tile);
        });
    }

    _el(name, tag, cls) {
        var el = this._branch.createElement(name, tag);
        if (cls) css.addClass(el, cls);
        return el;
    }

    dispose() {
        this._gone = true;
        this._branch.dissolve();
    }
}
