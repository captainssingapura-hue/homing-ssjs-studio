// =============================================================================
// ReferencesSteward — the steward of a doc's references: sent a Fetch, it reads
// each item's doc payload - once for the doc, from DocSources - and tells the
// party Loaded with the doc's references, as the site resolved them; or Failed,
// with why. Nothing is resolved here: a reference's place is the site's to know.
//
//   new ReferencesSteward(tell)   steward.reactors { Fetch }   steward.asked() → the docs it was sent for
// =============================================================================

class ReferencesSteward {
    constructor(tell) {
        if (typeof tell !== "function") throw new Error("[ReferencesSteward] hired with the means to tell its party");
        var self = this;
        this._tell = tell;
        this._asked = [];
        this.reactors = Object.freeze({ Fetch: function (m) { m.items.forEach(function (item) { self._fetch(item.params); }); } });
    }

    asked() { return this._asked.slice(); }

    _fetch(params) {
        var self = this, at = ContentParams.object(params);
        this._asked.push(at.doc);
        if (!at.doc) { this._tell({ kind: "Failed", params: params, why: "references are asked for by their doc" }); return; }
        DocSources.payload(at.doc).then(function (payload) {
            self._tell({ kind: "Loaded", params: params, content: { rows: payload.references || [] } });
        }).catch(function (e) {
            self._tell({ kind: "Failed", params: params, why: String(e && e.message || e) });
        });
    }
}
