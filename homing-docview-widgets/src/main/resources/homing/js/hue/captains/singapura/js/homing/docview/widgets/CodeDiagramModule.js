// =============================================================================
// CodeDiagram — a diagram's code part, drawn: the renderer DocCode hands a
// diagram's language to. A small selector on top - the diagram, or its source -
// and under it the one picked; the diagram first. The source is there at once.
// The diagram is asked of the diagram party, with the widget's own params, and
// is drawn when the steward says it is ready; until then the plate says it is
// being drawn, and when the steward says it cannot be, why.
//
//   CodeDiagram.draw(content, branch, into, ask)   DocCode's renderer contract
//   d.view() → "diagram" | "source"   d.pick(view)   d.state() → "drawing" | "drawn" | "failed"
// =============================================================================

class CodeDiagram {

    static draw(content, branch, into, ask) { return new CodeDiagram(content, branch, into, ask); }

    constructor(content, branch, into, ask) {
        var self = this;
        this._branch = branch;
        this._language = content.language;
        var bar = branch.createElement("views", "div");
        css.addClass(bar, dw_views);
        bar.setAttribute("role", "tablist");
        bar.setAttribute("aria-label", "The diagram, or its source");
        this._tabs = { diagram: this._tab(bar, "diagram", "Diagram"), source: this._tab(bar, "source", "Source") };
        var lang = branch.createElement("lang", "span");
        css.addClass(lang, dw_lang);
        lang.textContent = content.language;
        bar.appendChild(lang);
        into.appendChild(bar);
        this._panels = { diagram: this._plate(into), source: this._source(content.source, into) };
        this.pick("diagram");
        this._at("drawing", "Drawing the diagram…");
        ask(DIAGRAM, {
            Content: function (diagram) { self._drawn(diagram.svg); },
            Unavailable: function (why) { self._at("failed", "The diagram could not be drawn: " + why); }
        });
    }

    view() { return this._view; }

    state() { return this._state; }

    pick(view) {
        var self = this;
        if (!this._panels[view]) throw new Error("[CodeDiagram] a view is the diagram or the source, not " + view);
        this._view = view;
        Object.keys(this._panels).forEach(function (v) {
            self._tabs[v].setAttribute("aria-selected", String(v === view));
            css.toggleClass(self._panels[v], dw_hidden, v !== view);
        });
    }

    _tab(bar, view, text) {
        var self = this, tab = this._branch.createElement(view + "Tab", "button");
        css.addClass(tab, dw_view);
        tab.setAttribute("type", "button");
        tab.setAttribute("role", "tab");
        tab.textContent = text;
        tab.addEventListener("click", function () { self.pick(view); });
        bar.appendChild(tab);
        return tab;
    }

    /** The diagram's plate: what it says while there is no drawing, then the drawing. */
    _plate(into) {
        var plate = this._branch.createElement("plate", "div");
        css.addClass(plate, dw_plate);
        plate.setAttribute("role", "tabpanel");
        this._note = this._branch.createElement("drawing", "p");
        css.addClass(this._note, dw_note);
        plate.appendChild(this._note);
        into.appendChild(plate);
        return plate;
    }

    _source(source, into) {
        var panel = this._branch.createElement("source", "div");
        panel.setAttribute("role", "tabpanel");
        var pre = this._branch.createElement("pre", "pre");
        css.addClass(pre, dw_pre);
        var code = this._branch.createElement("code", "code");
        code.textContent = source;
        pre.appendChild(code);
        panel.appendChild(pre);
        into.appendChild(panel);
        return panel;
    }

    _drawn(markup) {
        if (this._state === "drawn") return;
        try {
            var svg = SvgMarkup.html(markup);
            css.addClass(svg, dw_diagram);
            svg.setAttribute("role", "img");
            svg.setAttribute("aria-label", "A " + this._language + " diagram");
            this._panels.diagram.appendChild(svg);
            this._at("drawn", "");
        } catch (e) {
            this._at("failed", "The diagram could not be shown: " + String(e && e.message || e));
        }
    }

    _at(state, text) {
        this._state = state;
        this._panels.diagram.setAttribute("data-diagram", state);
        this._note.textContent = text;
        css.toggleClass(this._note, dw_hidden, !text);
    }
}
