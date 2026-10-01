// =============================================================================
// ReferenceRows — the references table's relation, a relation grid's root
// (view, columns, cellFor): a doc's references, a row each, in the order the
// doc declares them, each by its name - unique in a doc. Its columns: the name
// the doc cites it by; what it names; where it goes - an authentic path, an
// address, or that it is placed nowhere; how often the doc cites it. Read only:
// a reference is the doc's to declare. A cell is a stock text cell, on a branch
// of the relation's own.
//
//   new ReferenceRows({ branch })
//   rows.view(intent)  rows.columns()  rows.readOnlyColumns()  rows.labels()  rows.cellFor(pk, column)
//   rows.show(refs)    rows.row(name) → the reference, or null    rows.all()
//   ReferenceRows.text(ref, column)   what a cell says
// =============================================================================

const _referenceRowsOwner = Object.freeze({ toString: () => "referenceRows" });

class ReferenceRows {

    /** The columns, and what the header calls them. */
    static COLUMNS = Object.freeze(["name", "target", "where", "cited"]);
    static LABELS = Object.freeze({ name: "Name", target: "Target", where: "Where", cited: "Cited" });

    constructor(opts) {
        var o = opts || {};
        if (!o.branch) throw new Error("[ReferenceRows] opts.branch is required: the relation's own");
        this._branch = o.branch;
        this._branch.activate(_referenceRowsOwner);
        this._list = [];
        this._byName = new Map();
        this._cells = new Map();
        this._seq = 0;
    }

    view(intent) { return intent ? null : this._list.map(function (r) { return r.name; }); }

    columns() { return ReferenceRows.COLUMNS.slice(); }

    readOnlyColumns() { return ReferenceRows.COLUMNS.slice(); }

    labels() { return ReferenceRows.LABELS; }

    cellFor(pk, col) {
        var ref = this._byName.get(pk);
        if (!ref) throw new Error("[ReferenceRows] no such reference: " + pk);
        var k = pk + " " + col, c = this._cells.get(k);
        if (!c) {
            c = new RelGridTextCell({ branch: this._branch.createBranch("c" + (++this._seq)), value: ReferenceRows.text(ref, col) });
            this._cells.set(k, c);
        }
        return c;
    }

    show(refs) {
        var self = this;
        this._list = (refs || []).slice();
        this._byName = new Map();
        this._list.forEach(function (r) { self._byName.set(r.name, r); });
    }

    row(name) { return this._byName.get(name) || null; }

    all() { return this._list.slice(); }

    /** What a cell says of a reference: where it goes, as a reader needs it - a path, an address, or that it goes nowhere. */
    static text(ref, col) {
        switch (col) {
            case "name": return ref.name;
            case "target": return ref.title || "";
            case "where":
                if (ref.kind === "unplaced") return "not placed";
                if (ref.kind === "image") return "an image";
                return ref.to;
            case "cited":
                var n = ref.citedIn.length;
                return n === 0 ? "never" : n === 1 ? "1 section" : n + " sections";
            default: return "";
        }
    }
}
