// =============================================================================
// ModuleChecks — the conformance workbench's module conformance: the module
// that is picked and its own findings - its layering's and its rules', as the
// build's report has them. A crate or a package picked is no module: the hint
// says so. It follows the workbench party's pick.
//
//   new ModuleChecks(container, params)   params: none
// =============================================================================

class ModuleChecks extends WorkbenchPane {
    constructor(container, params) {
        super(container, "moduleChecks", "Module conformance", "Pick a module to see its own findings.");
    }

    hears() {
        var self = this;
        return { Picked: function (m) { self.follow(m.to); } };
    }

    joined() { this.tell({ kind: "CurrentRequested" }); }

    read(to) {
        if (to.indexOf("module:") !== 0) return Promise.resolve(null);
        return WorkbenchFeeds.json("crateConformance").then(function (c) { return c.modules[to.slice("module:".length)] || null; });
    }

    show(mr, b) {
        var card = this.text("card", "article", wb_card, null, null, b);
        this.verdict(b, card, mr.ok, mr.moduleClass.slice(mr.moduleClass.lastIndexOf(".") + 1));
        this.text("where", "p", wb_hint, "crate " + mr.crate + " · " + mr.form, card, b);
        this.text("fqcn", "p", wb_code, mr.moduleClass, card, b);
        this.section(b, card, "findings", "Findings", mr.findings, "✓ conformant");
        return card;
    }
}
