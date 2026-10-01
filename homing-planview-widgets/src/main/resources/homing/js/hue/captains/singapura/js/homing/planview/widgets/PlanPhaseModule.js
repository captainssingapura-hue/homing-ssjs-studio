// =============================================================================
// PlanPhase — a plan's phase: where it stands - its status, its progress over
// its tasks, its effort - and its summary; then its details, folded until they
// are asked for: its description, its tasks, its metrics, the phases it depends
// on - each a link to its section - its verification, its rollback, its notes.
// The button that asks for them says whether they are shown; nothing else
// unfolds them, and nothing about where the phase sits is known here. Every text
// markdown. A ContentWidget of the type plan-phase.
//
//   new PlanPhase(container, params)   params: { doc, key }
//   phase.unfolded() → whether its details are shown   phase.unfold(open)
// =============================================================================

class PlanPhase extends ContentWidget {
    constructor(container, params) {
        super(container, params, PLAN_PHASE, "plan-phase");
        this._open = false;
        this._toggle = null;
        this._fold = null;
    }

    unfolded() { return this._open; }

    /** Its details shown, or folded again - when it has any; → whether it has. */
    unfold(open) {
        if (!this._fold) return false;
        this._open = !!open;
        css.toggleClass(this._fold, pl_hidden, !this._open);
        this._toggle.setAttribute("aria-expanded", String(this._open));
        this._toggle.textContent = this._open ? "Hide details" : "Show details";
        return true;
    }

    _draw(c, branch, into) {
        var md = new MarkdownDom(branch), self = this;
        var head = branch.createElement("head", "div");
        css.addClass(head, pl_phase_head);
        head.appendChild(PlanMarks.badge(branch, "status", c.status, c.statusLabel));
        if (c.tasks.length) {
            var done = c.tasks.filter(function (t) { return t.done; }).length;
            head.appendChild(PlanMarks.bar(branch, "bar", c.progress, "Tasks done"));
            PlanPhase._text(branch, "share", "span", pl_figure, done + " of " + c.tasks.length + " tasks done", head);
        }
        if (c.effort) PlanPhase._text(branch, "effort", "span", pl_figure, "Effort: " + c.effort, head);
        into.appendChild(head);
        if (c.summary) md.blocks(c.summary, into);
        var details = this._details(branch, md, c);
        if (!details) return;
        this._toggle = branch.createElement("toggle", "button");
        css.addClass(this._toggle, dw_view);
        css.addClass(this._toggle, pl_toggle);
        this._toggle.setAttribute("type", "button");
        this._toggle.addEventListener("click", function () { self.unfold(!self._open); });
        this._fold = branch.createElement("fold", "div");
        this._fold.appendChild(details);
        into.appendChild(this._toggle);
        into.appendChild(this._fold);
        this.unfold(false);
    }

    /** The details, a block each - or null, when the phase has none. */
    _details(branch, md, c) {
        var details = branch.createElement("details", "div"), any = false;
        css.addClass(details, pl_details);
        var block = function (name, title) { any = true; return PlanPhase._block(branch, name, title, details); };
        if (c.description) { any = true; md.blocks(c.description, details); }
        if (c.tasks.length) PlanPhase._tasks(branch, md, c.tasks, block("tasks", "Tasks"));
        if (c.metrics.length) PlanPhase._metrics(branch, c.metrics, block("metrics", "Metrics"));
        if (c.after.length) PlanPhase._after(branch, md, c.after, block("after", "Depends on"));
        [["verification", "Verification"], ["rollback", "Rollback"], ["notes", "Notes"]].forEach(function (b) {
            if (c[b[0]]) md.blocks(c[b[0]], block(b[0], b[1]));
        });
        return any ? details : null;
    }

    static _tasks(branch, md, tasks, into) {
        var list = PlanPhase._list(branch, "taskList", into);
        tasks.forEach(function (t, i) {
            var task = branch.createElement("task" + i, "li");
            css.addClass(task, pl_task);
            task.appendChild(PlanMarks.check(branch, "check" + i, t.done, t.done ? "Done" : "Not done"));
            var text = branch.createElement("taskText" + i, "span");
            md.inline(t.text, text);
            task.appendChild(text);
            list.appendChild(task);
        });
    }

    static _metrics(branch, metrics, into) {
        var box = branch.createElement("metricsBox", "div"), table = branch.createElement("metricsTable", "table");
        css.addClass(box, dw_table_box);
        css.addClass(table, dw_table);
        var head = branch.createElement("metricsColumns", "tr");
        ["Metric", "Before", "After", "Change"].forEach(function (t, j) { PlanPhase._text(branch, "th" + j, "th", dw_th, t, head); });
        table.appendChild(head);
        metrics.forEach(function (m, i) {
            var row = branch.createElement("metric" + i, "tr");
            [m.label, m.before, m.after, m.delta].forEach(function (t, j) { PlanPhase._text(branch, "td" + i + "_" + j, "td", dw_td, t, row); });
            table.appendChild(row);
        });
        box.appendChild(table);
        into.appendChild(box);
    }

    /** The phases it depends on: each a link to its section - or, not in the plan, its id alone - and why. */
    static _after(branch, md, after, into) {
        var list = PlanPhase._list(branch, "afterList", into);
        after.forEach(function (a, i) {
            var item = branch.createElement("after" + i, "li");
            css.addClass(item, pl_task);
            var text = branch.createElement("afterText" + i, "span");
            var name = "Phase " + a.phase + (a.label ? " — " + a.label : "");
            if (a.path) {
                var link = PlanPhase._text(branch, "afterLink" + i, "a", dw_link, name, text);
                HrefManagerInstance.set(link, "#" + a.path);
            } else {
                text.append(name + " (not in this plan)");
            }
            if (a.reason) { text.append(": "); md.inline(a.reason, text); }
            item.appendChild(text);
            list.appendChild(item);
        });
    }

    static _block(branch, name, title, into) {
        var block = branch.createElement(name, "div");
        css.addClass(block, pl_block);
        PlanPhase._text(branch, name + "Head", "p", pl_block_head, title, block);
        into.appendChild(block);
        return block;
    }

    static _list(branch, name, into) {
        var list = branch.createElement(name, "ul");
        css.addClass(list, pl_rows);
        into.appendChild(list);
        return list;
    }

    static _text(branch, name, tag, cls, text, into) {
        var e = branch.createElement(name, tag);
        css.addClass(e, cls);
        e.textContent = text;
        into.appendChild(e);
        return e;
    }
}
