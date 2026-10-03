// =============================================================================
// CatalogueTree — a site's catalogue as a tree: from the catalogue at `at`
// down - its catalogues (its own and the trees it grafts, alike) and its pages,
// each a row with its icon, name and badge; a catalogue folds, a page is a
// leaf; what is under a catalogue read from the site when it is first
// unfolded. The catalogue at `at` is the tree's root row, open from the start,
// what is under it beneath it - the listing's own catalogue, where the tree is.
// It paints no ground of its own: it lies on whatever holds it - a pane, a
// sheet - as the details do.
//
// Joined to a catalogue party, the cursor is the pick: a move onto an entry
// tells the party it is picked, and what the party says is picked, the cursor
// moves to - the catalogues that hold it unfolded first, read off its address;
// a catalogue, unfolded itself.
// Enter on an entry, or a double press, asks for it to open. Not joined, it
// works alone, and opens an entry itself, as its app says it opens.
//
// The keys: the tree's own - ↑ ↓ Home End walk it, ← → fold and unfold, Enter
// opens. Given the keys by any road but the browser's focus arriving in a row,
// it hands them on into the tree; Escape the tree did not take gives them back.
// It leads the keys of the view it is in: told the reader is reading an entry
// (Reading) - a press beside it, in a widget with no keys of its own - it moves
// the cursor there and takes the keys, or keeps them, into the tree.
//
//   new CatalogueTree(container, params)   params: { at } - a catalogue's address, "/" by default
//   tree.picked()   the entry the cursor was last put on, or told of; or null
// =============================================================================

class CatalogueTree extends CatalogueWidget {

    /** Keys of its own: a member, and the view's lead. */
    static KEYS = true;

    constructor(container, params) {
        super(container, "catalogueTree", "Catalogue tree");
        var self = this;
        this._at = (params && params.at) || "/";
        this._nodes = new Map();    // an entry's address → { entry, depth, kids: null (not read) | [address] }
        this._roots = [];
        this._open = new Set();
        this._cells = new Map();
        this._cellsBranch = this.branch.createBranch("cells");
        this._cellsBranch.activate(this);
        this._picked = null;
        this._pending = null;
        var box = this.branch.createElement("box", "div");
        css.addClass(box, wg_scroll);
        this.root.appendChild(box);
        this._tree = new RelTree({
            container: box, branch: this.branch.createBranch("tree"), label: "Catalogue", folder: true,
            surface: false,   // no ground of its own: it lies on what holds it, as the details do
            relation: { view: function () { return self._places(); }, cellFor: function (key) { return self._cellFor(key); } },
            ask: function (q) { return self._answer(q); },
            onCursorMoved: function (key) { if (key !== self._picked && self._nodes.has(key)) { self._picked = key; self.pick(key); } },
            onActivated: function (key) { var n = self._nodes.get(key); if (n) self.open(n.entry); }
        });
        this.entries.get(this._at).then(function (e) {
            if (!self.alive) return;
            // the catalogue at `at`: the root row, open, its own beneath it
            self._nodes.set(self._at, { entry: e, depth: 0, kids: self._read(e, 1) });
            self._open.add(self._at);
            self._roots = [self._at];
            self._tree.tell(new RelTreeViewChanged());
            if (self._pending) self._follow(self._pending);
        }, function (e) { console.error("[CatalogueTree] the catalogue at " + self._at + " was not read: " + e.message); });
    }

    hears() {
        var self = this;
        return {
            Picked: function (m) { if (m.to !== self._picked) self._follow(m.to); },   // its own pick, echoed, is where it is already
            // the reader is reading an entry, pressed beside: the cursor there, and the keys - kept, or had back - into the tree
            Reading: function (m) { if (m.to !== self._picked) self._follow(m.to); self.activate(); }
        };
    }

    joined() { this.tell({ kind: "CurrentRequested" }); }

    picked() { return this._picked; }

    /** Asked for the keys: claimed - nothing, when it holds them already - and into the tree either way. */
    activate() { Keys.claim(this.focus); this._tree.focus(); }

    /** Given the keys: into the tree - unless the browser's focus arriving in a row is what gave them. */
    granted(by) { if (by !== "native") this._tree.focus(); }

    /** A catalogue's entries, read into nodes at this depth: their addresses, in the site's order. */
    _read(e, depth) {
        var self = this;
        return e.children.map(function (c) {
            if (!self._nodes.has(c.to)) self._nodes.set(c.to, { entry: c, depth: depth, kids: c.kind === "catalogue" ? null : [] });
            return c.to;
        });
    }

    /** What is under a catalogue node: read once, from the site. */
    _load(to) {
        var n = this._nodes.get(to), self = this;
        if (!n || n.kids) return Promise.resolve(n);
        return this.entries.get(to).then(function (e) { n.kids = self._read(e, n.depth + 1); return n; });
    }

    /** What the tree presents now: every node, those under a folded one left out. */
    _places() {
        var out = [], open = this._open, nodes = this._nodes;
        (function place(keys) {
            keys.forEach(function (key) {
                var n = nodes.get(key), catalogue = n.entry.kind === "catalogue";
                out.push({ key: key, depth: n.depth, fold: catalogue ? (open.has(key) ? "open" : "closed") : "leaf" });
                if (catalogue && open.has(key) && n.kids) place(n.kids);
            });
        })(this._roots);
        return out;
    }

    _cellFor(key) {
        var n = this._nodes.get(key);
        if (!n) throw new Error("[CatalogueTree] no such entry: " + key);
        var c = this._cells.get(key);
        if (!c) {
            c = new CatalogueRowCell({ branch: this._cellsBranch.createBranch("r" + this._cells.size), entry: n.entry });
            this._cells.set(key, c);
        }
        return c;
    }

    /** The tree's questions: an unfold read from the site the first time, a fold answered from the nodes. */
    _answer(q) {
        var self = this;
        if (q instanceof RelTreeUnfold) return this._load(q.key).then(function () { self._open.add(q.key); return new RelTreeView(self._places()); });
        if (q instanceof RelTreeFold) { this._open.delete(q.key); return Promise.resolve(new RelTreeView(this._places())); }
        return Promise.resolve();
    }

    /**
     * The cursor to what someone else picked - the catalogues that hold it read and unfolded
     * first, as its address names them, the root among them; and a catalogue picked, unfolded too,
     * so what is in it shows. The root itself picked, the cursor is on the root row.
     */
    _follow(to) {
        var self = this;
        this._picked = to;
        if (to !== this._at && !CatalogueEntries.under(this._at, to)) return;
        if (!this._roots.length) { this._pending = to; return; }
        this._pending = null;
        var holders = to === this._at ? [to] : [this._at].concat(CatalogueEntries.between(this._at, to), [to]);
        holders.reduce(function (p, h) { return p.then(function () { return self._load(h); }); }, Promise.resolve()).then(function () {
            if (!self.alive || self._picked !== to) return;
            holders.forEach(function (h) { var n = self._nodes.get(h); if (n && n.entry.kind === "catalogue") self._open.add(h); });
            self._tree.tell(new RelTreeViewChanged());
            if (self._tree.cursor() !== to) self._tree.selectNode(to);
        });
    }

    disposed() {
        this._tree.destroy();
        this._cells.forEach(function (c) { c.dispose(); });
        this._cells.clear();
    }
}
