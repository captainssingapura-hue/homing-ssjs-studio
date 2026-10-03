// =============================================================================
// DocCode — a doc's code part: one widget for every language, polymorphic by
// language. It hands the language and the source to the renderer registered
// for the language; a language no renderer takes is drawn as its source, said
// by its language. A renderer that fails is answered with the source too: the
// source is always there. A renderer is handed what the widget may do for it:
// ask another content party, with the widget's own params - a diagram's
// renderer asks for the drawing - and offer the widget to the stage. Mermaid's
// is registered from the start. A ContentWidget of the type code, kept for the
// stage by a placement that keeps such.
//
//   new DocCode(container, params)      params: { doc, key }
//   DocCode.register(language, draw)    draw(content, branch, into, host) - the renderer for a language;
//                                       host.ask(type, { Content(content), Unavailable(why) })
//                                       host.offerStage(branch) → a button's element, or null: no stage here
//                                       host.keysInto(control) - the control the keys go into when the widget is given them
//   DocCode.source(content, branch, into)   the source as it is: what every language falls back to
// =============================================================================

class DocCode extends ContentWidget {

    /** A placement that keeps widgets for a stage keeps this one. */
    static STAGEABLE = true;

    constructor(container, params) { super(container, params, CODE, "code"); }

    static register(language, draw) {
        if (typeof draw !== "function") throw new Error("[DocCode] a renderer draws: draw(content, branch, into, host)");
        DocCode._renderers.set(String(language).toLowerCase(), draw);
    }

    _draw(content, branch, into) {
        var draw = DocCode._renderers.get(content.language), self = this;
        if (!draw) { DocCode.source(content, branch, into); return; }
        var host = Object.freeze({
            ask: function (type, on) { self._ask(type, on); },
            offerStage: function (at) { return self._offerStage(at); },
            keysInto: function (control) { self._keysInto(control); }
        });
        try { draw(content, branch, into, host); }
        catch (e) {
            var fallback = this._dom.createBranch("source");
            fallback.activate(this);
            DocCode.source(content, fallback, into);
        }
    }

    /** The source as it is, said by its language when it has one. */
    static source(content, branch, into) {
        if (content.language) {
            var lang = branch.createElement("lang", "p");
            css.addClass(lang, dw_lang);
            lang.textContent = content.language;
            into.appendChild(lang);
        }
        var pre = branch.createElement("pre", "pre");
        css.addClass(pre, dw_pre);
        var code = branch.createElement("code", "code");
        code.textContent = content.source;
        pre.appendChild(code);
        into.appendChild(pre);
    }
}

/** The renderers, by language: mermaid's diagram, until another is registered; any other language, its source. */
DocCode._renderers = new Map([["mermaid", CodeDiagram.draw]]);
