// =============================================================================
// PlanList — a list of a plan's: its objectives, its decisions, or its
// acceptance - what the list says of itself, then a row each. A row: its mark -
// whether it is met, a ring filled or not; its head - its id, its title, and a
// decision's status as a badge; its text; and more about it, each labelled -
// what was chosen, why, notes. Every text markdown. A ContentWidget of the type
// plan-list.
//
//   new PlanList(container, params)   params: { doc, key }
// =============================================================================

class PlanList extends ContentWidget {
    constructor(container, params) { super(container, params, PLAN_LIST, "plan-list"); }

    _draw(c, branch, into) {
        var md = new MarkdownDom(branch);
        if (c.note) {
            var note = branch.createElement("note", "p");
            css.addClass(note, pl_figure);
            note.textContent = c.note;
            into.appendChild(note);
        }
        var rows = branch.createElement("rows", "ul");
        css.addClass(rows, pl_rows);
        c.rows.forEach(function (r, i) { rows.appendChild(PlanList._row(branch, md, r, i)); });
        into.appendChild(rows);
    }

    static _row(branch, md, r, i) {
        var row = branch.createElement("row" + i, "li");
        css.addClass(row, pl_row);
        var checked = r.mark === "met" || r.mark === "unmet";
        if (checked) row.appendChild(PlanMarks.check(branch, "check" + i, r.mark === "met", r.label));
        var body = branch.createElement("body" + i, "div");
        css.addClass(body, pl_row_body);
        var head = branch.createElement("head" + i, "div");
        css.addClass(head, pl_row_head);
        if (r.id) {
            var id = branch.createElement("id" + i, "span");
            css.addClass(id, pl_id);
            id.textContent = r.id;
            head.appendChild(id);
        }
        var title = branch.createElement("title" + i, "span");
        css.addClass(title, pl_row_title);
        md.inline(r.title, title);
        head.appendChild(title);
        if (r.mark && !checked) head.appendChild(PlanMarks.badge(branch, "badge" + i, r.mark, r.label));
        body.appendChild(head);
        if (r.text) md.blocks(r.text, body);
        r.more.forEach(function (m, j) {
            var more = branch.createElement("more" + i + "_" + j, "p");
            css.addClass(more, pl_more);
            var label = branch.createElement("label" + i + "_" + j, "span");
            css.addClass(label, pl_label);
            label.textContent = m.label;
            more.appendChild(label);
            md.inline(m.text, more);
            body.appendChild(more);
        });
        row.appendChild(body);
        return row;
    }
}
