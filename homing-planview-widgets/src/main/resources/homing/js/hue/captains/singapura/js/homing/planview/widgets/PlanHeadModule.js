// =============================================================================
// PlanHead — a plan's head: where the plan stands, at a glance. What it is - its
// kicker - and its lede; its progress over every task, a bar; how many phases
// are done, decisions open and acceptance met, each of how many; and the docs
// it names - its execution plan, its dossier - each a link where the site reads
// it, or said to be placed nowhere. A ContentWidget of the type plan-head.
//
//   new PlanHead(container, params)   params: { doc, key }
// =============================================================================

class PlanHead extends ContentWidget {
    constructor(container, params) { super(container, params, PLAN_HEAD, "plan-head"); }

    _draw(c, branch, into) {
        if (c.kicker) PlanHead._text(branch, "kicker", "p", pl_kicker, c.kicker, into);
        if (c.lede) {
            var lede = branch.createElement("lede", "p");
            css.addClass(lede, dw_para);
            new MarkdownDom(branch).inline(c.lede, lede);
            into.appendChild(lede);
        }
        var progress = branch.createElement("progress", "div");
        css.addClass(progress, pl_progress);
        progress.appendChild(PlanMarks.bar(branch, "bar", c.progress, "Tasks done"));
        PlanHead._text(branch, "share", "span", pl_figure, c.progress + "% of the tasks done", progress);
        into.appendChild(progress);
        var facts = branch.createElement("facts", "ul");
        css.addClass(facts, pl_facts);
        PlanHead._fact(branch, "phases", c.phases, "phases done", facts);
        PlanHead._fact(branch, "decisions", c.decisions, "decisions open", facts);
        PlanHead._fact(branch, "acceptance", c.acceptance, "acceptance criteria met", facts);
        if (facts.childNodes.length) into.appendChild(facts);
        if (c.docs.length) into.appendChild(PlanHead._docs(branch, c.docs));
    }

    /** A count, said when there is anything to count: so many of how many, and of what. */
    static _fact(branch, name, count, what, into) {
        if (!count.total) return;
        var fact = branch.createElement(name, "li");
        PlanHead._text(branch, name + "Count", "span", pl_count, count.of + " of " + count.total, fact);
        fact.append(" " + what);
        into.appendChild(fact);
    }

    /** The docs the plan names: each its role, then a link where the site reads it - or, placed nowhere, said so. */
    static _docs(branch, docs) {
        var line = branch.createElement("docs", "p");
        css.addClass(line, pl_docs);
        docs.forEach(function (d, i) {
            var named = branch.createElement("doc" + i, "span");
            PlanHead._text(branch, "role" + i, "span", pl_label, d.role, named);
            if (d.kind === "doc" && d.to) {
                var link = PlanHead._text(branch, "link" + i, "a", dw_link, d.title || d.to, named);
                HrefManagerInstance.set(link, d.to);
            } else {
                var nowhere = PlanHead._text(branch, "nowhere" + i, "span", dw_badge, "not placed on this site", named);
                css.addClass(nowhere, dw_warning);
            }
            line.appendChild(named);
        });
        return line;
    }

    static _text(branch, name, tag, cls, text, into) {
        var e = branch.createElement(name, tag);
        css.addClass(e, cls);
        e.textContent = text;
        into.appendChild(e);
        return e;
    }
}
