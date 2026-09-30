// =============================================================================
// DocInspectorApp — a doc's first stage, seen whole. It asks for the doc's
// payload by the address it is at - the doc's authentic path, its params' doc
// - and shows the three things the payload holds, side by side:
//
//   the tree         every heading, its label drawn by its runs, its name in
//                    the path, and the parts of its leaf - each its type and key
//   the arrangement  every widget: its name, its type, the key it asks by
//   the payload      as it came, the JSON a page loads
//
// A doc whose tree could not be built is said so, with the reason. Everything
// is made through the page's party; nothing is written as markup.
// =============================================================================

const _inspectorOwner = Object.freeze({ toString: () => "docInspector" });
var _INSPECTOR_ROUTE = "/doc-view/payload";

class DocInspector {
    constructor(branch, params) {
        branch.activate(_inspectorOwner);
        this._branch = branch;
        this._n = 0;
        var root = this._el("article", di_column);
        this._el("h1", di_title, root).textContent = params.title || "A doc";
        var at = this._el("p", di_muted, root);
        css.addClass(at, di_code);
        at.textContent = params.doc || "(reached without an address)";
        this._status = this._el("p", di_muted, root);
        this._status.textContent = "Asking for its payload...";
        this._sections = this._el("div", di_sections, root);
        this.root = root;
        if (params.doc) this._load(params.doc);
        else this._status.textContent = "Nothing to ask for: the page does not know where it is.";
    }

    _el(tag, cls, parent) {
        var el = this._branch.createElement("e" + (this._n++), tag);
        if (cls) css.addClass(el, cls);
        if (parent) parent.appendChild(el);
        return el;
    }

    _load(doc) {
        var self = this;
        fetch(_INSPECTOR_ROUTE + "?doc=" + encodeURIComponent(doc))
            .then(function (r) { if (!r.ok) throw new Error("the server answered " + r.status); return r.json(); })
            .then(function (p) { self._show(p); })
            .catch(function (e) { self._fail(String(e && e.message || e)); });
    }

    _fail(why) {
        this._status.textContent = "Its payload could not be read: " + why;
        css.addClass(this._status, di_failed);
    }

    _show(p) {
        if (p.failed) { this._fail("its tree could not be built - " + p.failed); return; }
        var a = p.arrangement, widgets = Object.keys(a.widgets), nodes = DocInspector._count(a.root);
        this._status.textContent = nodes + " headings, " + widgets.length + " widgets, " + p.items.length + " parts in the payload";
        this._tree(a);
        this._arrangement(a, widgets);
        this._payload(p);
    }

    static _count(node) { return 1 + node.children.reduce(function (n, c) { return n + DocInspector._count(c); }, 0); }

    _section(title) {
        var s = this._el("section", di_section, this._sections);
        s.setAttribute("aria-label", title);
        this._el("h2", di_heading, s).textContent = title;
        return s;
    }

    _tree(a) {
        var list = this._el("ul", di_tree, this._section("The tree"));
        this._node(a, a.root, "", list);
    }

    /** A node: its label by its runs, its path, its leaf's parts; then its children, nested. */
    _node(a, node, path, list) {
        var li = this._el("li", di_node, list), label = this._el("span", null, li), self = this;
        li.setAttribute("data-path", path);
        if (!node.label.runs.length) label.textContent = node.label.text;
        node.label.runs.forEach(function (r) {
            var run = self._el(r.kind === "code" ? "code" : r.kind === "strong" ? "strong" : r.kind === "emphasis" ? "em" : "span", null, label);
            run.textContent = r.text;
        });
        var name = this._el("span", di_muted, li);
        css.addClass(name, di_code);
        name.textContent = path === "" ? "/ (the root)" : path;
        if (node.leaf.length) {
            var chips = this._el("span", di_chips, li);
            node.leaf.forEach(function (ref) {
                var w = a.widgets[ref];
                self._el("span", di_chip, chips).textContent = w.kind + " " + w.params.key;
            });
        }
        if (node.children.length) {
            var sub = this._el("ul", di_tree, li);
            node.children.forEach(function (c) { self._node(a, c, path === "" ? c.name : path + "/" + c.name, sub); });
        }
    }

    _arrangement(a, widgets) {
        var table = this._el("table", di_table, this._section("The arrangement")), self = this;
        var head = this._el("tr", null, this._el("thead", null, table));
        ["widget", "type", "key"].forEach(function (h) { self._el("th", di_cell, head).textContent = h; });
        var body = this._el("tbody", null, table);
        widgets.forEach(function (ref) {
            var row = self._el("tr", null, body), w = a.widgets[ref];
            [ref, w.kind, w.params.key].forEach(function (v) { self._el("td", di_cell, row).textContent = v; });
        });
    }

    _payload(p) {
        this._el("pre", di_pre, this._section("The payload")).textContent = JSON.stringify(p, null, 2);
    }
}

function appMain(el, params) {
    css.addClass(el, di_page);
    el.appendChild(new DocInspector(domOpsParty.createBranch("docInspector"), params).root);
}
