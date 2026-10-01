// =============================================================================
// DocTable — a doc's table part: its caption, its columns' titles, its rows; every
// cell markdown text drawn as prose's inline elements are, set to its own or
// its column's alignment, spanning the columns and rows it says, wearing its
// badge, strong or set back. A ContentWidget of the type table.
//
//   new DocTable(container, params)   params: { doc, key }
// =============================================================================

var _TABLE_ALIGN = Object.freeze({ left: "left", center: "center", right: "right" });
var _TABLE_BADGE = Object.freeze({ success: "success", warning: "warning", error: "danger", danger: "danger" });

class DocTable extends ContentWidget {
    constructor(container, params) { super(container, params, TABLE, "table"); }

    _draw(t, branch, into) {
        var md = new MarkdownDom(branch), n = 0;
        var el = function (tag, cls, parent) {
            var e = branch.createElement("t" + (n++), tag);
            if (cls) css.addClass(e, cls);
            parent.appendChild(e);
            return e;
        };
        var table = el("table", dw_table, el("div", dw_table_box, into));
        if (t.caption) md.inline(t.caption, el("caption", dw_caption, table));
        var head = el("tr", null, el("thead", null, table));
        t.columns.forEach(function (c) { md.inline(c.title, DocTable._aligned(el("th", dw_th, head), c.align)); });
        var body = el("tbody", null, table);
        t.rows.forEach(function (row) {
            var tr = el("tr", null, body);
            row.cells.forEach(function (cell, i) {
                var td = DocTable._aligned(el("td", dw_td, tr), cell.align || (t.columns[i] ? t.columns[i].align : ""));
                if (cell.colSpan > 1) td.colSpan = cell.colSpan;
                if (cell.rowSpan > 1) td.rowSpan = cell.rowSpan;
                if (cell.emphasis === "strong") css.addClass(td, dw_strong);
                else if (cell.emphasis === "muted") css.addClass(td, dw_dim);
                md.inline(cell.text, DocTable._badged(el, cell.badge, td));
            });
        });
    }

    /** What a cell's text goes in: a badge of its status, when it wears one; else the cell. */
    static _badged(el, badge, td) {
        var status = _TABLE_BADGE[badge];
        if (!status) return td;
        var b = el("span", dw_badge, td);
        css.addClass(b, status === "success" ? dw_success : status === "warning" ? dw_warning : dw_danger);
        return b;
    }

    static _aligned(cell, align) {
        var a = _TABLE_ALIGN[align];
        if (a) css.addClass(cell, a === "left" ? dw_left : a === "center" ? dw_center : dw_right);
        return cell;
    }
}
