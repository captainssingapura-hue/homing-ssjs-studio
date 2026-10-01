// =============================================================================
// ModuleSource — the conformance workbench's full content: a module that is
// opened - Enter on it in the navigator, or a double press - shown as it is
// served, verbatim: the very artifact the browser loads, and the rules read.
// Opening anything that is not a module shows nothing new.
//
//   new ModuleSource(container, params)   params: none
// =============================================================================

class ModuleSource extends WorkbenchPane {
    constructor(container, params) {
        super(container, "moduleSource", "Full content", "Open a module in the navigator - Enter, or a double press - to read it as it is served.");
    }

    hears() {
        var self = this;
        return { Opening: function (m) { if (m.to.indexOf("module:") === 0) self.follow(m.to); } };
    }

    read(to) {
        var fqcn = to.slice("module:".length);
        return WorkbenchFeeds.source(fqcn).then(function (source) { return { fqcn: fqcn, source: source }; });
    }

    show(m, b) {
        var card = this.text("card", "article", wb_card, null, null, b);
        this.text("kicker", "p", wb_kicker, "as served", card, b);
        this.text("title", "h2", wb_title, m.fqcn.slice(m.fqcn.lastIndexOf(".") + 1), card, b);
        this.text("fqcn", "p", wb_code, m.fqcn, card, b);
        this.text("source", "pre", wb_source, m.source, card, b);
        return card;
    }
}
