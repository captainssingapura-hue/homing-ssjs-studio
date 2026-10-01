// =============================================================================
// DocDesk — the desk a doc is read on, and a plan: the same desk for any tree a
// payload brings. It is told the address of what it reads, and nothing else. It
// reads the payload, makes the arrangement from the tree the payload brings,
// and lays it out: the contents on the left, the tree on the right, in a split
// grid. Every part's widget is mounted at once, each made from its type and its
// params; each asks its content party, whose steward reads what it needs as it
// is wanted - the plain parts from the payload already read, an image by its key.
//
// The contents and the tree meet here: a section picked in the contents is
// shown; a section folded there is folded here; the section in view is
// followed there. The address's fragment is the section in view - a section's
// address opens at it, and the fragment follows the reader. The keys: the
// contents lead; a widget that takes them gives them back to the contents.
// The references, when the payload brings any: the last section, a list of
// them, in the contents as any section is; a citation pressed shows its
// reference there, marked - or, with Ctrl or ⌘, opens what it names in a tab of
// its own; a #ref:name address shows it too.
// The stage: one, modal, a party the widgets are given; its steward asks the
// layout to lend the widget asked for and takes it back after - the very
// widget, never a copy - so the stage knows the layout, and nothing else.
//
// The desk knows a doc's primitives and their parties. An app that reads other
// trees offers its own widget types and parties beside them (extra):
//
//   DocDesk.open(el, address, title, extra?)   what an app's main does: the payload read, the desk laid out
//   new DocDesk(place, el, payload, at, extra?)
//     extra   { kinds: { [type]: WidgetClass }, parties: function (given) - adds the app's parties to given }
// =============================================================================

const _docDeskOwner = Object.freeze({ toString: () => "docDesk" });

/** The widget types any tree on the desk may place: a doc's primitives, and the list of its references. */
var _DOC_KINDS = Object.freeze({ prose: DocProse, code: DocCode, table: DocTable, image: DocImage, references: DocReferences });

class DocDesk {
    constructor(place, el, payload, at, extra) {
        var refs = payload.references || [], self = this, more = extra || {};
        var tree = DocDesk._withReferences(payload.tree, refs);
        var a = DocArrangement.of(tree, payload.doc);
        this._refs = refs;
        this._refsPath = refs.length ? tree.children[tree.children.length - 1].name : null;
        var shell = DocDesk._el(place, "shell", dv_shell, el);
        var grid = new SplitGrid(place.createBranch("grid"), {
            host: shell, minCellPx: 160, seam: true,
            layout: { kind: "split", orientation: "horizontal", children: [
                { node: { kind: "cell", id: "contents" }, ratio: 1 },
                { node: { kind: "cell", id: "doc" }, ratio: 3 }] }
        });
        var contentsBox = DocDesk._el(place, "contentsBox", dv_cell, grid.cell("contents"));
        var docBox = DocDesk._el(place, "docBox", dv_cell, grid.cell("doc"));
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
        if (typeof more.parties === "function") more.parties(given);
        var kinds = Object.assign({}, _DOC_KINDS, more.kinds || {});
        this.contents = new TreeToc(contentsBox, { arrangement: a, label: "Contents" });
        this.layout = new TreeLayout(docBox, { arrangement: a, kinds: kinds, given: given, onShown: function (path) { self._shown(path); } });
        this.references = this._refsPath ? this.layout.widget(a.root.children[a.root.children.length - 1].leaf[0]) : null;
        this.contents.onPick(function (path) { self.layout.show(path); });
        this.contents.onFold(function (path, open) { self.layout.fold(path, !open); });
        place.graft("contents", this.contents.roots.dom);
        place.graft("doc", this.layout.roots.dom);
        focusParty.root.graft("contents", this.contents.roots.focus);
        this.contents.graft("doc", this.layout.roots.focus);
        docBox.addEventListener("click", function (ev) { self._clicked(ev); });
        this.contents.follow(this.layout.shown());
        this._off = HrefManagerInstance.onHashChange(function (h) { self._go(h); });
        if (at) requestAnimationFrame(function () { self._go(at); });
    }

    /** What an app's main does: the payload of what is at the address read, and the desk laid out - or why not, said. */
    static open(el, address, title, extra) {
        css.addClass(el, dv_page);
        var place = domOpsParty.createBranch("docDesk");
        place.activate(_docDeskOwner);
        var status = place.createElement("status", "p");
        css.addClass(status, dv_status);
        el.appendChild(status);
        if (!address) { status.textContent = "Nothing to read: the page does not know what it is."; return; }
        status.textContent = "Reading " + (title || "it") + "...";
        var at = HrefManagerInstance.hash();   // read before the tree is laid out, which writes the section in view to it
        DocSources.payload(address).then(function (payload) {
            new DocDesk(place, el, payload, at, extra);
            css.addClass(status, dv_hidden);   // gone once the tree is laid out: one that fails to be is said, not hidden
        }).catch(function (e) {
            console.error("[DocDesk] " + address + " could not be read", e);
            status.textContent = "It could not be read: " + String(e && e.message || e);
            css.toggleClass(status, dv_hidden, false);
        });
    }

    /**
     * The tree with its references appended - its last section, References, its leaf the list of
     * them - named so no section of its own is shadowed. The tree as it came, when it has none.
     */
    static _withReferences(tree, refs) {
        if (!refs.length) return tree;
        var taken = new Set(tree.children.map(function (c) { return c.name; })), name = "references";
        for (var n = 2; taken.has(name); n++) name = "references-" + n;
        var section = { name: name, label: { text: "References", runs: [] }, leaf: [{ type: "references", key: name + ":0" }], children: [] };
        return Object.assign({}, tree, { children: tree.children.concat([section]) });
    }

    /** A fragment followed: a citation's - #ref:name - its reference shown, the fragment the section's again; else the section it names. */
    _go(fragment) {
        var path = decodeURIComponent(String(fragment || ""));
        if (path.indexOf("ref:") === 0) { this._cite(path.slice(4), false); HrefManagerInstance.replaceHash(this.layout.shown() || ""); return; }
        if (path) this.layout.show(path);
    }

    /** A citation pressed: followed as the page follows one, not as its fragment. */
    _clicked(ev) {
        var cite = ev.target && ev.target.closest ? ev.target.closest("[data-ref]") : null;
        if (!cite) return;
        ev.preventDefault();
        this._cite(cite.getAttribute("data-ref"), ev.ctrlKey || ev.metaKey);
    }

    /** A citation followed: its reference shown in the references, marked - or, asked directly, what it names opened in a tab of its own. */
    _cite(name, direct) {
        var ref = this._refs.find(function (r) { return r.name === name; });
        if (direct && ref && ref.to && (ref.kind === "doc" || ref.kind === "external")) { HrefManagerInstance.openNew(ref.to); return; }
        if (!this.references) return;
        this.layout.show(this._refsPath);
        this.references.show(name);
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
