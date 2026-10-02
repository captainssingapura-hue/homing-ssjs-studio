// =============================================================================
// ComponentNavigator — the components workbench's navigator: the components
// the studio's crates deliver, composed from their closure - root, vehicle,
// family, component - as the components feed gives them. A node's key is its
// name path below the root, after a "/" - the root's own, "/". Its cursor is
// the workbench's pick, and the summary shows what is picked.
//
//   new ComponentNavigator(container, params)   params: none
//   navigator.picked()
// =============================================================================

class ComponentNavigator extends WorkbenchNavigator {
    constructor(container, params) {
        super(container, "componentNavigator", "Components");
    }

    read() {
        return WorkbenchFeeds.json("components").then(function (root) {
            return (function keyed(n) {
                return { key: "/" + n.key, label: n.label, kind: n.kind, children: (n.children || []).map(keyed) };
            })(root);
        });
    }
}
