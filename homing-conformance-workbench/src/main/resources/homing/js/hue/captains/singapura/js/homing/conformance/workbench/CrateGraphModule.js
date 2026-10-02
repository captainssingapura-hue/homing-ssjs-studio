// =============================================================================
// CrateGraph — the conformance workbench's dependency graph: every crate of
// the studio's closure and the crates each requires, as the graph feed draws it
// in mermaid - drawn by DocView's engine in the design's colours, read into the
// page as SVG with nothing in it that runs, and shown in a view that zooms and
// pans, its bar above it. A library that cannot be loaded says so where the
// graph would be. It follows no pick: the graph is the whole.
//
// The keys: the view's own while it holds them - + and − zoom, 0 fits, the
// arrows pan; Escape the view did not take gives them back.
//
//   new CrateGraph(container, params)   params: none
// =============================================================================

class CrateGraph extends WorkbenchWidget {
    constructor(container, params) {
        super(container, "crateGraph", "Dependency graph");
        var self = this;
        this._box = this.text("box", "div", wb_graph, null, this.root);
        this._status = this.text("status", "p", wb_hint, "Drawing the dependency graph…", this._box);
        this._bar = this.text("bar", "div", wb_graph_bar, null, this._box);
        this._view = this.text("view", "div", wb_graph_view, null, this._box);
        this._zoom = null;
        this._zoomBar = null;
        WorkbenchFeeds.text("crateGraph").then(function (source) { return MermaidEngine.draw(source); }).then(function (markup) {
            if (!self.alive) return;
            self._zoom = new SvgPanZoom(self.branch.createBranch("zoom"), { svg: SvgMarkup.html(markup), label: "Dependency graph" });
            self._view.appendChild(self._zoom.root);
            self._zoomBar = new PanZoomBar(self.branch.createBranch("zoomBar"), self._zoom);
            self._bar.appendChild(self._zoomBar.root);
            css.toggleClass(self._status, wb_hidden, true);
        }).catch(function (err) {
            if (!self.alive) return;
            self._status.textContent = "The graph could not be drawn: " + err.message;
            css.setClass(self._status, wb_failed);
        });
    }

    /** Holding the keys: the view's first, then Escape gives them back. */
    keyDown(ev) {
        if (this._zoom && this._zoom.key(ev)) return true;
        return super.keyDown(ev);
    }

    disposed() {
        if (this._zoomBar) { this._zoomBar.dispose(); this._zoomBar = null; }
        if (this._zoom) { this._zoom.dispose(); this._zoom = null; }
    }
}
