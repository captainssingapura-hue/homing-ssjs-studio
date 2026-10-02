// =============================================================================
// DiagramSteward — the steward of a doc's diagrams: the one member of the
// diagram hierarchy that draws. Sent a Fetch, it reads each item's source - the
// code part its key names, from the doc's payload, by DocSources - and has the
// engine for the part's language draw it, in the background; when it is drawn
// it tells the party Loaded with the SVG, and when it cannot be, Failed with
// why. An engine's library is loaded only when a diagram of its language is
// wanted. Mermaid's engine is there from the start; another is registered.
//
//   new DiagramSteward(tell)   steward.reactors { Fetch }   steward.asked() → the keys it was sent for
//   DiagramSteward.engine(language, engine)   engine.draw(source) → Promise of SVG markup
//   DiagramSteward.draws(language) → whether an engine draws the language
// =============================================================================

class DiagramSteward {
    constructor(tell) {
        if (typeof tell !== "function") throw new Error("[DiagramSteward] hired with the means to tell its party");
        var self = this;
        this._tell = tell;
        this._asked = [];
        this.reactors = Object.freeze({ Fetch: function (m) { m.items.forEach(function (item) { self._fetch(item.params); }); } });
    }

    static engine(language, engine) {
        if (!engine || typeof engine.draw !== "function") throw new Error("[DiagramSteward] an engine draws: engine.draw(source) → Promise of SVG markup");
        DiagramSteward._engines.set(String(language).toLowerCase(), engine);
    }

    static draws(language) { return DiagramSteward._engines.has(String(language || "").toLowerCase()); }

    asked() { return this._asked.slice(); }

    _fetch(params) {
        var self = this, at = ContentParams.object(params), key = ContentParams.key(params);
        this._asked.push(key);
        if (!at.doc || at.key === undefined) { this._tell({ kind: "Failed", params: params, why: "a diagram is asked for by its doc and its key" }); return; }
        DocSources.payload(at.doc).then(function (payload) {
            var item = payload.items.find(function (i) { return i.type === "code" && ContentParams.key(i.params) === key; });
            if (!item) throw new Error("the doc has no code part at " + at.key);
            var language = item.content.language, engine = DiagramSteward._engines.get(language);
            if (!engine) throw new Error("no engine draws " + (language || "code with no language"));
            return engine.draw(item.content.source).then(function (svg) {
                self._tell({ kind: "Loaded", params: params, content: { language: language, svg: String(svg) } });
            });
        }).catch(function (e) {
            self._tell({ kind: "Failed", params: params, why: String(e && e.message || e) });
        });
    }
}

/** The engines, by language: mermaid's, until another is registered. */
DiagramSteward._engines = new Map([["mermaid", MermaidEngine]]);
