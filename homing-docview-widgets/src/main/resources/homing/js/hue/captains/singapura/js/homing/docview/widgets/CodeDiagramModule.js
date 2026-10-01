// =============================================================================
// CodeDiagram — a diagram's code part, drawn: the renderer DocCode hands a
// diagram's language to. A small selector on top - the diagram, or its source -
// and under it the one picked; the diagram first. The source is there at once.
// The diagram is asked of the diagram party, with the widget's own params, and
// is drawn when the steward says it is ready; until then a plate says it is
// being drawn, and when the steward says it cannot be, why. Drawn, it zooms and
// pans - an SvgPanZoom, its bar beside the selector while the diagram is the
// view picked - and it offers itself to the stage, when the page has one. It
// fills the box it is in: its content's height in the flow of a doc, all of a
// box its host sizes - the stage's.
//
//   CodeDiagram.draw(content, branch, into, host)   DocCode's renderer contract: host.ask, host.offerStage
//   d.view() → "diagram" | "source"   d.pick(view)   d.state() → "drawing" | "drawn" | "failed"
//   d.zoom → the SvgPanZoom, once drawn
// =============================================================================

class CodeDiagram {

    static draw(content, branch, into, host) { return new CodeDiagram(content, branch, into, host); }

    constructor(content, branch, into, host) {
        var self = this;
        this._branch = branch;
        this._language = content.language;
        this.zoom = null;
        this._slot = null;
        this._host = host;
        var head = branch.createElement("head", "div");
        css.addClass(head, dw_views);
        var tabs = branch.createElement("views", "div");
        css.addClass(tabs, dw_views);
        tabs.setAttribute("role", "tablist");
        tabs.setAttribute("aria-label", "The diagram, or its source");
        this._tabs = { diagram: this._tab(tabs, "diagram", "Diagram"), source: this._tab(tabs, "source", "Source") };
        head.appendChild(tabs);
        var lang = branch.createElement("lang", "span");
        css.addClass(lang, dw_lang);
        lang.textContent = content.language;
        head.appendChild(lang);
        this._tools = branch.createElement("tools", "div");
        css.addClass(this._tools, dw_views);
        css.addClass(this._tools, dw_push);
        head.appendChild(this._tools);
        into.appendChild(head);
        this._head = head;
        this._panels = { diagram: this._diagram(into), source: this._source(content.source, into) };
        this.pick("diagram");
        this._at("drawing", "Drawing the diagram…");
        host.ask(DIAGRAM, {
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
        if (this._slot) css.toggleClass(this._slot, dw_hidden, view !== "diagram");
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

    /** The diagram's panel: a plate that says what there is while there is no drawing, then the drawing. */
    _diagram(into) {
        var panel = this._branch.createElement("diagram", "div");
        css.addClass(panel, dw_fill);
        panel.setAttribute("role", "tabpanel");
        this._plate = this._branch.createElement("plate", "div");
        css.addClass(this._plate, dw_plate);
        this._note = this._branch.createElement("drawing", "p");
        css.addClass(this._note, dw_note);
        this._plate.appendChild(this._note);
        panel.appendChild(this._plate);
        into.appendChild(panel);
        return panel;
    }

    _source(source, into) {
        var panel = this._branch.createElement("source", "div");
        css.addClass(panel, dw_fill);
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

    /** Drawn: the drawing in a view that zooms and pans, on the plate's ground; its bar in the head. */
    _drawn(markup) {
        if (this._state === "drawn") return;
        try {
            var svg = SvgMarkup.html(markup);
            css.addClass(svg, dw_diagram);
            svg.setAttribute("role", "img");
            svg.setAttribute("aria-label", "A " + this._language + " diagram");
            this.zoom = new SvgPanZoom(this._branch.createBranch("zoom"), { svg: svg, label: "The " + this._language + " diagram" });
            css.addClass(this.zoom.root, dw_drawing);
            this._panels.diagram.appendChild(this.zoom.root);
            this._slot = this._branch.createElement("zoomSlot", "div");
            this._slot.appendChild(new PanZoomBar(this._branch.createBranch("zoomBar"), this.zoom).root);
            this._tools.appendChild(this._slot);
            css.toggleClass(this._slot, dw_hidden, this._view !== "diagram");
            var offer = this._host.offerStage(this._branch);
            if (offer) this._tools.appendChild(offer);
            css.toggleClass(this._plate, dw_hidden, true);
            this._at("drawn", "");
        } catch (e) {
            this._at("failed", "The diagram could not be shown: " + String(e && e.message || e));
        }
    }

    _at(state, text) {
        this._state = state;
        this._panels.diagram.setAttribute("data-diagram", state);
        this._note.textContent = text;
        if (state !== "drawn") css.toggleClass(this._plate, dw_hidden, false);
    }
}
