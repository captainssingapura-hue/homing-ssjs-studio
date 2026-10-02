// =============================================================================
// ImageSteward — the steward of a doc's images: an image is heavy, so it is not
// in the doc's payload; each is fetched by its key when it is wanted, from
// DocSources, and told Loaded - or Failed, with why.
//
//   new ImageSteward(tell)   steward.reactors { Fetch }   steward.asked() → the keys it was sent for
// =============================================================================

class ImageSteward {
    constructor(tell) {
        if (typeof tell !== "function") throw new Error("[ImageSteward] hired with the means to tell its party");
        var self = this;
        this._tell = tell;
        this._asked = [];
        this.reactors = Object.freeze({ Fetch: function (m) { m.items.forEach(function (item) { self._fetch(item.params); }); } });
    }

    asked() { return this._asked.slice(); }

    _fetch(params) {
        var self = this, at = ContentParams.object(params);
        this._asked.push(ContentParams.key(params));
        if (!at.doc || at.key === undefined) { this._tell({ kind: "Failed", params: params, why: "an image is asked for by its doc and its key" }); return; }
        DocSources.part(at.doc, at.key).then(function (part) {
            if (part.type !== "image") throw new Error("the part at " + at.key + " is " + part.type + ", not an image");
            self._tell({ kind: "Loaded", params: params, content: part.content });
        }).catch(function (e) {
            self._tell({ kind: "Failed", params: params, why: String(e && e.message || e) });
        });
    }
}
