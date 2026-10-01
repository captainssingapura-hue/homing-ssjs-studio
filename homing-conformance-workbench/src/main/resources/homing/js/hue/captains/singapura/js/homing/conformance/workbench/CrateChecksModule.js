// =============================================================================
// CrateChecks — the conformance workbench's crate conformance: the crate of
// the node that is picked - the crate itself, or the crate a package or a
// module ships in - and how it stands: its orphan modules, its illegal
// imports, its modules' rule violations, rolled up. The crate is where they
// aggregate. It follows the workbench party's pick.
//
//   new CrateChecks(container, params)   params: none
// =============================================================================

class CrateChecks extends WorkbenchPane {
    constructor(container, params) {
        super(container, "crateChecks", "Crate conformance", "Pick a crate, or anything in one, to see how the crate stands.");
    }

    hears() {
        var self = this;
        return { Picked: function (m) { self.follow(m.to); } };
    }

    joined() { this.tell({ kind: "CurrentRequested" }); }

    read(to) {
        return Promise.all([WorkbenchFeeds.crateNodes(), WorkbenchFeeds.json("crateConformance")]).then(function (r) {
            var n = r[0].get(to);
            var name = !n ? null : n.kind === "crate" ? n.label : n.crate;
            return name && r[1].crates[name] ? r[1].crates[name] : null;
        });
    }

    show(cr, b) {
        var card = this.text("card", "article", wb_card, null, null, b);
        var rules = cr.ruleFindings || [];
        var issues = cr.orphans.length + cr.illegalImports.length + rules.length;
        this.verdict(b, card, cr.ok, cr.name);
        this.text("count", "p", wb_hint, cr.modules + " modules · " + (cr.ok ? "conformant" : issues + " issue" + (issues === 1 ? "" : "s")), card, b);
        this.section(b, card, "orphans", "Orphan modules", cr.orphans);
        this.section(b, card, "imports", "Illegal imports", cr.illegalImports);
        this.section(b, card, "rules", "Rule violations", rules);
        return card;
    }
}
