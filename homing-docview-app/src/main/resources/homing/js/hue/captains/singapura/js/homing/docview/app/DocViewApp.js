// =============================================================================
// DocViewApp — a doc, viewed. The same app for every doc: it is told which by
// its params - the doc's authentic path - and nothing else. It reads the doc's
// payload, makes the arrangement from the tree the payload brings, and lays it
// out: the contents on the left, the doc on the right, in a split grid. Every
// part's widget is mounted at once, each made from its type and its params;
// each asks its content party, whose steward reads what it needs as it is
// wanted - the plain parts from the payload already read, an image by its key.
//
// The contents and the doc meet here: a section picked in the contents is
// shown; a section folded there is folded here; the section in view is
// followed there. The address's fragment is the section in view - a section's
// address opens at it, and the fragment follows the reader. The keys: the
// contents lead; a widget that takes them gives them back to the contents.
// The references, when the doc declares any: a third column, a table of them; a
// citation pressed in the doc shows its reference there, the cursor on it - or,
// with Ctrl or ⌘, opens what it names in a tab of its own; a #ref:name address
// shows it too.
// The stage: one, modal, a party the widgets are given; its steward asks the
// doc's layout to lend the widget asked for and takes it back after - the very
// widget, never a copy - so the stage knows the layout, and nothing else.
// =============================================================================

const _docViewOwner = Object.freeze({ toString: () => "docView" });

/** The widget types a doc's tree places: its primitives. */
var _DOC_KINDS = Object.freeze({ prose: DocProse, code: DocCode, table: DocTable, image: DocImage });

class DocView {
    constructor(place, el, payload, at) {
        var a = DocArrangement.of(payload.tree, payload.doc), self = this;
        this._refs = payload.references || [];
        var cells = [{ node: { kind: "cell", id: "contents" }, ratio: 1 }, { node: { kind: "cell", id: "doc" }, ratio: 3 }];
        if (this._refs.length) cells.push({ node: { kind: "cell", id: "references" }, ratio: 1.4 });
        var shell = DocView._el(place, "shell", dv_shell, el);
        var grid = new SplitGrid(place.createBranch("grid"), {
            host: shell, minCellPx: 160, seam: true,
            layout: { kind: "split", orientation: "horizontal", children: cells }
        });
        var contentsBox = DocView._el(place, "contentsBox", dv_cell, grid.cell("contents"));
        var docBox = DocView._el(place, "docBox", dv_cell, grid.cell("doc"));
        var given = {};
        given[PROSE.name] = new MessagingParty(PROSE, ContentSecretary, ProseSteward);
        given[CODE.name] = new MessagingParty(CODE, ContentSecretary, CodeSteward);
        given[TABLE.name] = new MessagingParty(TABLE, ContentSecretary, TableSteward);
        given[IMAGE.name] = new MessagingParty(IMAGE, ContentSecretary, ImageSteward);
        given[DIAGRAM.name] = new MessagingParty(DIAGRAM, ContentSecretary, DiagramSteward);
        given[REFERENCES.name] = new MessagingParty(REFERENCES, ContentSecretary, ReferencesSteward);
        // the stage: one, modal; its steward swaps a widget the layout keeps with the stage, asking the layout alone
        given[STAGE.name] = new MessagingParty(STAGE, StageSecretary,
            StageSteward.over({ placement: function () { return self.layout; }, branch: place.createBranch("stage") }));
        this.contents = new TreeToc(contentsBox, { arrangement: a, label: "Contents" });
        this.layout = new TreeLayout(docBox, { arrangement: a, kinds: _DOC_KINDS, given: given, onShown: function (path) { self._shown(path); } });
        this.contents.onPick(function (path) { self.layout.show(path); });
        this.contents.onFold(function (path, open) { self.layout.fold(path, !open); });
        place.graft("contents", this.contents.roots.dom);
        place.graft("doc", this.layout.roots.dom);
        focusParty.root.graft("contents", this.contents.roots.focus);
        this.contents.graft("doc", this.layout.roots.focus);
        if (this._refs.length) {
            this.references = new DocReferences(DocView._el(place, "referencesBox", dv_cell, grid.cell("references")), { doc: payload.doc });
            this.references.join(given);
            place.graft("references", this.references.roots.dom);
            this.contents.graft("references", this.references.roots.focus);
        }
        docBox.addEventListener("click", function (ev) { self._clicked(ev); });
        this.contents.follow(this.layout.shown());
        this._off = HrefManagerInstance.onHashChange(function (h) { self._go(h); });
        if (at) requestAnimationFrame(function () { self._go(at); });
    }

    /** A fragment followed: a citation's - #ref:name - its reference shown, the fragment the section's again; else the section it names. */
    _go(fragment) {
        var path = decodeURIComponent(String(fragment || ""));
        if (path.indexOf("ref:") === 0) { this._cite(path.slice(4), false); HrefManagerInstance.replaceHash(this.layout.shown() || ""); return; }
        if (path) this.layout.show(path);
    }

    /** A citation pressed in the doc: followed as the page follows one, not as its fragment. */
    _clicked(ev) {
        var cite = ev.target && ev.target.closest ? ev.target.closest("[data-ref]") : null;
        if (!cite) return;
        ev.preventDefault();
        this._cite(cite.getAttribute("data-ref"), ev.ctrlKey || ev.metaKey);
    }

    /** A citation followed: its reference shown beside the doc, the cursor on it - or, asked directly, what it names opened in a tab of its own. */
    _cite(name, direct) {
        var ref = this._refs.find(function (r) { return r.name === name; });
        if (direct && ref && ref.to && (ref.kind === "doc" || ref.kind === "external")) { HrefManagerInstance.openNew(ref.to); return; }
        if (this.references) this.references.show(name);
    }

    /** The section in view: followed in the contents, and written to the fragment - the root's, none. */
    _shown(path) {
        if (this.contents) this.contents.follow(path);
        HrefManagerInstance.replaceHash(path || "");
    }

    static _el(place, name, cls, parent) {
        var e = place.createElement(name, "div");
        css.addClass(e, cls);
        parent.appendChild(e);
        return e;
    }
}

function appMain(el, params) {
    css.addClass(el, dv_page);
    var place = domOpsParty.createBranch("docView");
    place.activate(_docViewOwner);
    var status = place.createElement("status", "p");
    css.addClass(status, dv_status);
    el.appendChild(status);
    if (!params.doc) { status.textContent = "Nothing to read: the page does not know which doc it is."; return; }
    status.textContent = "Reading " + (params.title || "the doc") + "...";
    var at = HrefManagerInstance.hash();   // read before the doc is laid out, which writes the section in view to it
    DocSources.payload(params.doc).then(function (payload) {
        new DocView(place, el, payload, at);
        css.addClass(status, dv_hidden);   // gone once the doc is laid out: a doc that fails to be is said, not hidden
    }).catch(function (e) {
        console.error("[DocView] the doc could not be read", e);
        status.textContent = "The doc could not be read: " + String(e && e.message || e);
        css.toggleClass(status, dv_hidden, false);
    });
}
