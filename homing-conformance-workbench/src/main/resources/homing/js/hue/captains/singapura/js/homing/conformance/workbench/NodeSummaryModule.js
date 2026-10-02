// =============================================================================
// NodeSummary — the conformance workbench's summary: the node that is picked,
// as the crates feed has it - the root, a crate, a package, a module - its
// kind, its name, and its facts: how many crates and modules, the package its
// modules live under, the crates it requires, the crate a module ships in and
// its form. It follows the workbench party's pick.
//
//   new NodeSummary(container, params)   params: none
// =============================================================================

class NodeSummary extends WorkbenchPane {
    constructor(container, params) {
        super(container, "nodeSummary", "Summary", "Pick a crate, a package or a module in the navigator to see it here.");
    }

    hears() {
        var self = this;
        return { Picked: function (m) { self.follow(m.to); } };
    }

    joined() { this.tell({ kind: "CurrentRequested" }); }

    read(to) {
        return WorkbenchFeeds.crateNodes().then(function (nodes) { return nodes.get(to) || null; });
    }

    show(n, b) {
        var card = this.text("card", "article", wb_card, null, null, b);
        this.text("kicker", "p", wb_kicker, n.kind === "module" ? String(n.form).toLowerCase().replace(/_/g, "-") + " module" : n.kind, card, b);
        this.text("title", "h2", wb_title, n.label, card, b);
        if (n.kind === "module") this.text("fqcn", "p", wb_code, n.fqcn, card, b);
        this.facts(b, card, NodeSummary.factsOf(n));
        return card;
    }

    static factsOf(n) {
        switch (n.kind) {
            case "crates":  return [["crates", n.crates], ["modules", n.modules]];
            case "crate":   return [["modules", n.modules], ["package", n.prefix || "(several)"], ["requires", n.requires.length ? n.requires.join(", ") : "none"]];
            case "package": return [["crate", n.crate], ["modules", n.modules]];
            case "module":  return [["crate", n.crate], ["form", n.form]];
            default:        return [];
        }
    }
}
