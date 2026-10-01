// =============================================================================
// CrateNavigator — the conformance workbench's navigator: the studio's own
// crates, under one root, each crate's modules by their packages - as the
// crates feed nests them - a module a leaf. A row says what its node is: a
// crate, a package, a module's form. Its cursor is the workbench's pick; Enter
// on a module asks for it to open, and the source pane shows it.
//
//   new CrateNavigator(container, params)   params: none
//   navigator.picked()
// =============================================================================

class CrateNavigator extends WorkbenchNavigator {
    constructor(container, params) {
        super(container, "crateNavigator", "Crates");
    }

    read() {
        return WorkbenchFeeds.crateNodes().then(function (nodes) { return nodes.get("crates"); });
    }

    row(node) {
        var badge = node.kind === "module" ? String(node.form || "").toLowerCase().replace(/_/g, "-")
                  : node.kind === "crates" ? node.crates + " crates" : node.kind;
        return { to: node.key, icon: "", name: node.label, badge: badge.toUpperCase() };
    }
}
