// =============================================================================
// ConformanceReportPane — the conformance workbench's report: the one the
// build exported, read whole and decoded through the generated codecs into
// typed instances - no raw JSON on the screen. Its verdict first - errors,
// warnings, or clean - then a section per module type, as the policy gives each
// type a rule set: the rules every module of the type is held to, folded until
// asked for, and every module, clean ones too, its findings under it. A finding
// is said in the word its state is: an error danger, a debt (pre-existing,
// baselined) warning, an allowance info; a module that passes with findings is
// a warning too, never clean. It follows no pick: the report is the whole.
//
//   new ConformanceReportPane(container, params)   params: none
// =============================================================================

var _REPORT_TYPES = Object.freeze({
    "consumer": "Consumer", "primitive": "Primitive", "secretary": "Secretary", "pure-logic": "Pure logic",
    "manager-injector": "Manager injector", "generated-css": "Generated CSS", "bundled-external": "Bundled external"
});
var _REPORT_ORDER = Object.freeze(["consumer", "primitive", "secretary", "pure-logic", "manager-injector", "generated-css", "bundled-external"]);

class ConformanceReportPane extends WorkbenchPane {
    constructor(container, params) {
        super(container, "conformanceReport", "Conformance report", "Reading the report the build exported…");
        this.follow("report");
    }

    read(to) {
        return WorkbenchFeeds.json("report").then(function (data) {
            return { report: ConformanceReportCodec.transformFrom(data.summary),
                     modules: data.modules.map(function (w) { return ModuleResultCodec.transformFrom(w); }) };
        });
    }

    show(r, b) {
        var report = r.report, self = this;
        var card = this.text("card", "article", wb_card, null, null, b);
        var ok = report.errorCount === 0, warned = ok && report.warningCount > 0;
        var head = this.text("verdict", "p", wb_verdict, !ok ? "✗ " + report.errorCount + " error" + (report.errorCount === 1 ? "" : "s")
                : warned ? "⚠ Conformant · " + report.warningCount + " warning" + (report.warningCount === 1 ? "" : "s") : "✓ Conformant", card, b);
        css.addClass(head, !ok ? wb_danger : warned ? wb_warning : wb_success);
        this.text("summary", "p", wb_hint, report.moduleCount + " modules · " + report.warningCount + " warnings · " + report.baselineSize
                + " baselined · " + (report.allowPreExisting ? "pre-existing allowed" : "pre-existing refused"), card, b);
        var ruleSets = {};
        (report.ruleSets || []).forEach(function (rs) { ruleSets[rs.id] = rs; });
        var byType = {};
        r.modules.forEach(function (m) { (byType[m.type] = byType[m.type] || []).push(m); });
        var order = _REPORT_ORDER.concat(Object.keys(byType).filter(function (t) { return _REPORT_ORDER.indexOf(t) < 0; }));
        order.forEach(function (type, t) {
            var ms = byType[type];
            if (!ms || !ms.length) return;
            self._type(b, card, "t" + t, type, ms, ruleSets[ms[0].ruleSet], ms[0].ruleSet);
        });
        return card;
    }

    /** A type's section: its name and how many, its rule set folded, every module with its findings. */
    _type(b, into, name, type, ms, rs, setId) {
        var self = this, sec = this.text(name, "section", wb_type, null, into, b);
        // A downstream's own type is named by the rule set it registered for it.
        this.text(name + "-head", "p", wb_section, (_REPORT_TYPES[type] || (rs && rs.title) || type) + " (" + ms.length + " module" + (ms.length === 1 ? "" : "s") + ")", sec, b);
        var withFindings = ms.filter(function (m) { return m.findings.length; }).length;
        var rules = rs && rs.rules ? rs.rules : [];
        var fold = this.text(name + "-rules", "details", wb_rules, null, sec, b);
        this.text(name + "-rules-said", "summary", null, "rule set: " + setId + " (" + rules.length + " rule" + (rules.length === 1 ? "" : "s") + ") · "
                + (ms.length - withFindings) + " clean / " + withFindings + " with findings", fold, b);
        rules.forEach(function (rule, i) { self.text(name + "-rule" + i, "p", wb_rule, rule.id + " — " + rule.intent, fold, b); });
        ms.forEach(function (m, i) { self._module(b, sec, name + "-m" + i, m); });
    }

    /** A module: errors danger, passing with findings a warning, clean success; its findings under it. */
    _module(b, into, name, m) {
        var self = this, warn = m.pass && m.findings.length > 0;
        var line = this.text(name, "p", wb_line, (!m.pass ? "✗ " : warn ? "⚠ " : "✓ ") + m.moduleClass.slice(m.moduleClass.lastIndexOf(".") + 1)
                + (m.findings.length ? " (" + m.findings.length + ")" : ""), into, b);
        line.setAttribute("title", m.moduleClass);
        css.addClass(line, !m.pass ? wb_danger : warn ? wb_warning : wb_success);
        m.findings.forEach(function (f, i) {
            var fl = self.text(name + "-f" + i, "p", wb_line, (f.severity === "ERROR" ? "✗" : "⚠") + " [" + f.rule + "] " + f.message
                    + "  · " + String(f.disposition).toLowerCase().replace("_", "-"), into, b);
            css.addClass(fl, wb_finding);
            css.addClass(fl, f.severity === "ERROR" ? wb_danger : f.disposition === "ALLOWED" ? wb_info : f.disposition === "PRE_EXISTING" ? wb_warning : wb_danger);
        });
    }
}
