// =============================================================================
// CrateNavigator — the conformance workbench's navigator: the studio's own
// crates, under one root, each crate's modules by their packages - as the
// crates feed nests them - a module a leaf. A row says what its node is: a
// crate, a package, a module's kind. Its cursor is the workbench's pick; Enter
// on a module asks for it to open, and the source pane shows it.
//
// Above the tree, which modules it shows, by their kind: every one, widgets,
// components - simple and composed, or either - other UI, non-UI, styles or
// infrastructure, each with how many; a crate or a package with none of them
// is left out.
//
//   new CrateNavigator(container, params)   params: none
//   navigator.picked()
// =============================================================================

class CrateNavigator extends WorkbenchNavigator {
    constructor(container, params) {
        super(container, "crateNavigator", "Crates");
        var self = this, shows = this.branch.createElement("shows", "select");
        css.addClass(shows, wb_filter);
        shows.setAttribute("aria-label", "Which modules it shows, by their kind");
        shows.addEventListener("change", function () { self._show(shows.value); });
        this.root.insertBefore(shows, this.root.firstChild);
        this._shows = shows;
    }

    read() {
        var self = this;
        return WorkbenchFeeds.crateNodes().then(function (nodes) {
            var root = nodes.get("crates");
            self._choices(root);
            return root;
        });
    }

    /** The kinds to show: every module, then each kind - the components, then either of them - with how many. */
    _choices(root) {
        var count = {}, self = this;
        root.types.forEach(function (t) { count[t.type] = t; });
        var simple = count["component-simple"] || { modules: 0 }, composed = count["component-composed"] || { modules: 0 };
        var choices = [["", "Every module", root.modules]];
        root.types.forEach(function (t) {
            if (t.type === "component-simple") choices.push(["component", "Components", simple.modules + composed.modules]);
            choices.push([t.type, (t.type.indexOf("component-") === 0 ? "\u2003" : "") + t.label, t.modules]);
        });
        choices.forEach(function (c) {
            var o = self.branch.createElement("show-" + (c[0] || "all"), "option");
            o.value = c[0];
            o.textContent = c[1] + " (" + c[2] + ")";
            self._shows.appendChild(o);
        });
    }

    /** Only the modules of a kind - a component of either kind for "component" - and what holds them; every module for none. */
    _show(type) {
        this.keep(!type ? null : function (n) {
            return n.kind === "module" && (n.type === type || (type === "component" && String(n.type).indexOf("component-") === 0));
        });
    }

    row(node) {
        var badge = node.kind === "module" ? String(node.typeLabel || node.form || "").toLowerCase()
                  : node.kind === "crates" ? node.crates + " crates" : node.kind;
        return { to: node.key, icon: "", name: node.label, badge: badge.toUpperCase() };
    }
}
