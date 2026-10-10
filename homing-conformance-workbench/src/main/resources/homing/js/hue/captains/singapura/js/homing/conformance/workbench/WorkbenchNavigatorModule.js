// =============================================================================
// WorkbenchNavigator — a workbench's navigator: a tree the feed gives, as the
// relation tree, a row each - its root first and open, as a listing shows the
// catalogue it is of; a node with children folds. A subclass says what it reads
// (read) and how a row looks (row); the rest is said once, here.
//
// Joined to the workbench party, the cursor is the pick: a move onto a node
// tells the party it is picked, and what the party says is picked, the cursor
// moves to - the nodes that hold it unfolded first. Enter on a node, or a
// double press, asks for it to open.
//
// The keys: the tree's own - the arrows walk and fold, Enter opens. Given the
// keys by any road but the browser's focus arriving in a row, it hands them on
// into the tree; Escape the tree did not take gives them back.
//
//   class CrateNavigator extends WorkbenchNavigator {
//       constructor(container, params) { super(container, "crateNavigator", "Crates"); }
//       read()    → Promise of the root node { key, label, kind, children: [node] }
//       row(node) → { icon, name, badge }: the row the node is drawn as
//   }
//   navigator.picked()   the key the cursor was last put on, or told of; or null
//   navigator.keep(keeps)  only the leaves keeps(node) answers true for, and the
//                        nodes that hold them; every node again with null
// =============================================================================

class WorkbenchNavigator extends WorkbenchWidget {
    constructor(container, name, label) {
        super(container, name, label);
        var self = this;
        this._nodes = new Map();      // a node's key → { node, depth, kids: [key], holders: [key] }
        this._roots = [];
        this._open = new Set();
        this._cells = new Map();
        this._cellSeq = 0;
        this._cellsBranch = this.branch.createBranch("cells");
        this._cellsBranch.activate(this);
        this._picked = null;
        this._pending = null;
        this._keeps = null;
        this._shown = null;     // the keys kept by keeps, with those that hold them; null when all are
        var box = this.branch.createElement("box", "div");
        css.addClass(box, wg_scroll);
        this.root.appendChild(box);
        this._tree = new RelTree({
            container: box, branch: this.branch.createBranch("tree"), label: label, folder: true,
            surface: false,   // no ground of its own: it lies on what holds it
            relation: { view: function () { return self._places(); }, cellFor: function (key) { return self._cellFor(key); } },
            ask: function (q) { return self._answer(q); },
            onCursorMoved: function (key) { if (key !== self._picked && self._nodes.has(key)) { self._picked = key; self.pick(key); } },
            onActivated: function (key) { if (self._nodes.has(key)) self.open(key); }
        });
        this.read().then(function (root) {
            if (!self.alive) return;
            self._take(root, 0, [], self._roots);
            self._open.add(root.key);
            if (self._keeps) self._shown = self._kept(self._keeps);
            self._tree.tell(new RelTreeViewChanged());
            if (self._pending) self._follow(self._pending);
        }, function (err) { console.error("[" + name + "] the tree could not be read: " + err.message); });
    }

    /** The tree it walks: a promise of its root node. A subclass's. */
    read() { return Promise.reject(new Error("read() is a subclass's to say")); }

    /** The row a node is drawn as. */
    row(node) { return { to: node.key, icon: "", name: node.label, badge: String(node.kind || "").toUpperCase() }; }

    hears() {
        var self = this;
        return { Picked: function (m) { if (m.to !== self._picked) self._follow(m.to); } };   // its own pick, echoed, is where it is already
    }

    joined() { this.tell({ kind: "CurrentRequested" }); }

    picked() { return this._picked; }

    /** Only the leaves keeps(node) is true for, and the nodes that hold them - the roots always; every node again with null. */
    keep(keeps) {
        this._keeps = keeps || null;
        if (!this._roots.length) return;
        this._shown = this._keeps ? this._kept(this._keeps) : null;
        this._tree.tell(new RelTreeViewChanged());
    }

    /** Given the keys: into the tree - unless the browser's focus arriving in a row is what gave them. */
    granted(by) { if (by !== "native") this._tree.focus(); }

    /** A node and those under it, each with the keys of the nodes that hold it. */
    _take(node, depth, holders, into) {
        var self = this, kids = [];
        this._nodes.set(node.key, { node: node, depth: depth, kids: kids, holders: holders });
        into.push(node.key);
        (node.children || []).forEach(function (c) { self._take(c, depth + 1, holders.concat([node.key]), kids); });
    }

    /** What the tree presents now: every node kept, those under a folded one left out. */
    _places() {
        var out = [], open = this._open, nodes = this._nodes, shown = this._shown;
        (function place(keys) {
            keys.forEach(function (key) {
                if (shown && !shown.has(key)) return;
                var n = nodes.get(key), kids = shown ? n.kids.filter(function (k) { return shown.has(k); }) : n.kids;
                out.push({ key: key, depth: n.depth, fold: kids.length ? (open.has(key) ? "open" : "closed") : "leaf" });
                if (kids.length && open.has(key)) place(kids);
            });
        })(this._roots);
        return out;
    }

    /** The keys a filter keeps: each leaf it is true for, each node holding one, and the roots. */
    _kept(keeps) {
        var nodes = this._nodes, out = new Set();
        function visit(key) {
            var n = nodes.get(key), any = false;
            n.kids.forEach(function (k) { if (visit(k)) any = true; });
            var kept = n.kids.length ? any : !!keeps(n.node);
            if (kept) out.add(key);
            return kept;
        }
        this._roots.forEach(function (key) { visit(key); out.add(key); });
        return out;
    }

    _cellFor(key) {
        var n = this._nodes.get(key);
        if (!n) throw new Error("[" + this._name + "] no such node: " + key);
        var c = this._cells.get(key);
        if (!c) {
            c = new CatalogueRowCell({ branch: this._cellsBranch.createBranch("r" + (++this._cellSeq)), entry: this.row(n.node) });
            this._cells.set(key, c);
        }
        return c;
    }

    /** The tree's questions: a fold and an unfold, answered from the nodes; anything else, with nothing. */
    _answer(q) {
        if (q instanceof RelTreeUnfold) { this._open.add(q.key); return Promise.resolve(new RelTreeView(this._places())); }
        if (q instanceof RelTreeFold) { this._open.delete(q.key); return Promise.resolve(new RelTreeView(this._places())); }
        return Promise.resolve();
    }

    /** The cursor to what someone else picked - the nodes that hold it unfolded first. */
    _follow(to) {
        this._picked = to;
        if (!this._roots.length) { this._pending = to; return; }
        this._pending = null;
        var n = this._nodes.get(to), self = this;
        if (!n) return;
        var folded = n.holders.filter(function (h) { return !self._open.has(h); });
        if (folded.length) {
            folded.forEach(function (h) { self._open.add(h); });
            this._tree.tell(new RelTreeViewChanged());
        }
        if (this._tree.cursor() !== to) this._tree.selectNode(to);
    }

    disposed() {
        this._tree.destroy();
        this._cells.forEach(function (c) { c.dispose(); });
        this._cells.clear();
    }
}
