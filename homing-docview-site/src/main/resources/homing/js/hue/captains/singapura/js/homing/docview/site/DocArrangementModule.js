// =============================================================================
// DocArrangement — a doc's arrangement, made on the page from the tree its
// payload brings: the tree placement the tree layout lays out (engine "tree"),
// each part of a node's leaf a widget of the part's type, its params the doc's
// address and the part's key - and nothing about where it sits. The widgets
// are named in reading order, w0, w1, …: a node's leaf, then its children's.
//
//   DocArrangement.of(tree, doc) → { engine: "tree", workspace: "doc-view",
//       widgets: { [ref]: { kind, params: { doc, key } } },
//       root: { name, label, leaf: [ref], children: [node] } }   frozen all the way down
// =============================================================================

class DocArrangement {

    static of(tree, doc) {
        if (!tree || !Array.isArray(tree.leaf) || !Array.isArray(tree.children)) throw new Error("[DocArrangement] a doc's tree is required: { name, label, leaf, children }");
        if (typeof doc !== "string" || !doc) throw new Error("[DocArrangement] the doc's address is required");
        var widgets = {}, n = 0;
        var node = function (t) {
            var leaf = t.leaf.map(function (part) {
                var ref = "w" + (n++);
                widgets[ref] = Object.freeze({ kind: part.type, params: Object.freeze({ doc: doc, key: part.key }) });
                return ref;
            });
            return Object.freeze({ name: t.name, label: t.label, leaf: Object.freeze(leaf), children: Object.freeze(t.children.map(node)) });
        };
        var root = node(tree);
        return Object.freeze({ engine: "tree", workspace: "doc-view", widgets: Object.freeze(widgets), root: root });
    }
}
