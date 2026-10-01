// =============================================================================
// DocPartSteward — the steward of a doc's plain parts of one type: the one
// member of its content hierarchy that does I/O. Sent a Fetch, it reads each
// item's doc payload - once for the doc, from DocSources - and tells the party
// Loaded with the part its key names, or Failed with why. The first time a
// doc's payload comes, it tells every part of its type there - loading ahead -
// so a part wanted next is answered from what the party keeps.
//
//   new DocPartSteward(tell, type)   tell: the party's, as its steward; type: prose | code | table
//   steward.reactors  { Fetch }   steward.type   steward.asked() → the keys it was sent for
// =============================================================================

class DocPartSteward {
    constructor(tell, type) {
        if (typeof tell !== "function") throw new Error("[DocPartSteward] hired with the means to tell its party");
        var self = this;
        this.type = type;
        this._tell = tell;
        this._ahead = new Set();
        this._asked = [];
        this.reactors = Object.freeze({ Fetch: function (m) { m.items.forEach(function (item) { self._fetch(item.params); }); } });
    }

    asked() { return this._asked.slice(); }

    _fetch(params) {
        var self = this, at = ContentParams.object(params), key = ContentParams.key(params);
        this._asked.push(key);
        if (!at.doc || at.key === undefined) { this._tell({ kind: "Failed", params: params, why: "a part is asked for by its doc and its key" }); return; }
        DocSources.payload(at.doc).then(function (payload) {
            var told = self._loadAhead(at.doc, payload);
            if (told.has(key)) return;
            var item = payload.items.find(function (i) { return i.type === self.type && ContentParams.key(i.params) === key; });
            if (item) self._tell({ kind: "Loaded", params: params, content: item.content });
            else self._tell({ kind: "Failed", params: params, why: "the doc has no " + self.type + " part at " + at.key });
        }).catch(function (e) {
            self._tell({ kind: "Failed", params: params, why: String(e && e.message || e) });
        });
    }

    /** Every part of its type in a doc's payload, told the first time the doc's payload comes; → the keys told. */
    _loadAhead(doc, payload) {
        var told = new Set(), self = this;
        if (this._ahead.has(doc)) return told;
        this._ahead.add(doc);
        payload.items.forEach(function (i) {
            if (i.type !== self.type) return;
            told.add(ContentParams.key(i.params));
            self._tell({ kind: "Loaded", params: i.params, content: i.content });
        });
        return told;
    }
}
