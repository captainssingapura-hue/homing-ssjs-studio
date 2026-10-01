// =============================================================================
// DocSources — a doc as the page reads it, the one place DocView's routes are
// reached. A doc's payload - its arrangement and every plain part's content -
// is fetched once, however many stewards read it; a heavy part is fetched by
// its key when it is wanted. A doc whose tree could not be built rejects with
// the reason the server gave.
//
//   DocSources.payload(doc) → Promise of { doc, arrangement, items: [{ type, params, content }] }
//   DocSources.part(doc, key) → Promise of { type, params, content }
// =============================================================================

class DocSources {

    static payload(doc) {
        var known = DocSources._payloads.get(doc);
        if (known) return known;
        var p = DocSources._json(DocSources.PAYLOAD + "?doc=" + encodeURIComponent(doc)).then(function (j) {
            if (j.failed) throw new Error("the doc's tree could not be built: " + j.failed);
            return j;
        });
        DocSources._payloads.set(doc, p);
        return p;
    }

    static part(doc, key) {
        return DocSources._json(DocSources.CONTENT + "?doc=" + encodeURIComponent(doc) + "&key=" + encodeURIComponent(key));
    }

    static _json(url) {
        return fetch(url).then(function (r) {
            if (!r.ok) throw new Error(r.status === 404 ? "not found" : "the server answered " + r.status);
            return r.json();
        });
    }
}

/** Where DocView's routes answer. */
DocSources.PAYLOAD = "/doc-view/payload";
DocSources.CONTENT = "/doc-view/content";
DocSources._payloads = new Map();
