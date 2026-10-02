// =============================================================================
// CatalogueEntries — the site's catalogue, as the widgets read it: an entry by
// its authentic path, from the site's route (/catalogue/entry) - a catalogue
// with what is under it, or a page - each read once and kept, so a tree, its
// details and its cards on one page ask the site once for each entry.
//
//   entry  { to, kind: "catalogue" | "page", name, summary, badge, icon,
//            opens: "in-place" | "new-tab", children: [entry without children] }
//
//   new CatalogueEntries()
//   entries.get(to) → Promise<entry>        a failed read is not kept, so it may be asked again
//   CatalogueEntries.between(at, to)        the catalogues strictly between two addresses, outermost
//                                           first - what holds `to` below `at`: its authentic path
//                                           says, so nothing is looked up
//   CatalogueEntries.under(at, to)          whether `to` is strictly below `at`
// =============================================================================

var _ENTRY_ROUTE = "/catalogue/entry";

class CatalogueEntries {
    constructor() { this._kept = new Map(); }

    static get ROUTE() { return _ENTRY_ROUTE; }

    get(to) {
        var kept = this._kept.get(to);
        if (kept) return kept;
        var self = this;
        var p = fetch(_ENTRY_ROUTE + "?path=" + encodeURIComponent(to))
            .then(function (r) { if (!r.ok) throw new Error("HTTP " + r.status + " for " + to); return r.json(); });
        p.catch(function () { self._kept.delete(to); });
        this._kept.set(to, p);
        return p;
    }

    static _segments(path) { return String(path).split("/").filter(function (s) { return s.length > 0; }); }

    static under(at, to) {
        var a = CatalogueEntries._segments(at), t = CatalogueEntries._segments(to);
        if (t.length <= a.length) return false;
        for (var i = 0; i < a.length; i++) if (a[i] !== t[i]) return false;
        return true;
    }

    static between(at, to) {
        if (!CatalogueEntries.under(at, to)) return [];
        var a = CatalogueEntries._segments(at), t = CatalogueEntries._segments(to), out = [];
        for (var n = a.length + 1; n < t.length; n++) out.push("/" + t.slice(0, n).join("/"));
        return out;
    }
}
