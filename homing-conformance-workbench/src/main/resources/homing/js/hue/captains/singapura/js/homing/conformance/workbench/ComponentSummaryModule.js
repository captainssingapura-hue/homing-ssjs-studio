// =============================================================================
// ComponentSummary — the components workbench's summary: the node that is
// picked, asked of the components feed by its name path - a composition, a
// vehicle, a catalogue, or a component with its shape, its tag, the module it
// is and the crate that ships it, and the words it is reached by. It follows
// the workbench party's pick.
//
//   new ComponentSummary(container, params)   params: none
// =============================================================================

class ComponentSummary extends WorkbenchPane {
    constructor(container, params) {
        super(container, "componentSummary", "Component", "Pick a vehicle, a family or a component in the navigator to see it here.");
    }

    hears() {
        var self = this;
        return { Picked: function (m) { self.follow(m.to); } };
    }

    joined() { this.tell({ kind: "CurrentRequested" }); }

    /** A node's key is "/" and its name path; the root's, "/" alone - which the feed reads as the root. */
    read(to) {
        return WorkbenchFeeds.json("components", to === "/" ? "/" : to.slice(1));
    }

    show(d, b) {
        var card = this.text("card", "article", wb_card, null, null, b);
        this.text("kicker", "p", wb_kicker, d.kind === "component" ? d.shape + " component" : d.kind, card, b);
        this.text("title", "h2", wb_title, d.label || d.segment, card, b);
        var summary = d.summary || (d.kind === "composition" ? d.vehicles + " vehicles" : "");
        if (summary) this.text("summary", "p", wb_hint, summary, card, b);
        this.facts(b, card, ComponentSummary.factsOf(d));
        return card;
    }

    static factsOf(d) {
        if (d.kind === "component") return [["shape", d.shape], ["tag", d.tag], ["module", d.module], ["crate", d.crate], ["path", d.path]];
        var out = [];
        if (d.crate) out.push(["crate", d.crate]);
        if (d.components != null) out.push(["components", d.components]);
        return out;
    }
}
