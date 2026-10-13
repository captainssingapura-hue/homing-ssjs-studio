// =============================================================================
// DocTable — a doc's table part: its caption, its columns' titles, its rows,
// said as a DocTableRelation and drawn as a ProseTable - every cell markdown text
// drawn as prose's inline elements are, set to its own or its column's
// alignment, spanning the columns it says, wearing its badge, strong or set
// back. A row span is refused by the relation: the grid spans columns only. A
// press in it gives it the keys. A ContentWidget of the type table.
//
//   new DocTable(container, params)   params: { doc, key }
// =============================================================================

var _TABLE_BADGE = Object.freeze({ success: "success", warning: "warning", error: "danger", danger: "danger" });

class DocTable extends ContentWidget {
    constructor(container, params) { super(container, params, TABLE, "table"); }

    /** What the doc's table says, as a DocTableRelation - judged when made - drawn as a ProseTable. */
    _draw(t, branch, into) {
        var md = this._md = new MarkdownDom(branch);
        var table = md.table(new DocTableRelation({
            caption: t.caption || null,
            columns: t.columns.map(function (c) { return { title: c.title, align: c.align || null }; }),
            rows: t.rows.map(function (row) {
                return row.cells.map(function (cell) {
                    return { text: cell.text, align: cell.align || null, emphasis: cell.emphasis || null,
                             badge: _TABLE_BADGE[cell.badge] || null, colSpan: cell.colSpan, rowSpan: cell.rowSpan };
                });
            })
        }), into);
        this._keysInto(table.grid.el());
    }

    disposed() { if (this._md) this._md.dispose(); }
}
