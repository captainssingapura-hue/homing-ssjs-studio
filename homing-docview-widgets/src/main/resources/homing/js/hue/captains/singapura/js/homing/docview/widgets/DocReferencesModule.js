// =============================================================================
// DocReferences — a doc's references, as a table on the relation grid: each
// reference the doc declares a row - its name, what it names, where it goes,
// how often the doc cites it (ReferenceRows). Enter, or a double press, on a
// row opens what it names: a doc at its authentic path, here; an address off
// the site, in a tab of its own; a doc no catalogue places, nowhere - and it
// says so. Asked to show a reference, the grid's cursor goes to its row, and
// the keys with it. A ContentWidget of the type references, asked by the doc
// alone; it fills the box it is lent.
//
//   new DocReferences(container, params)   params: { doc }
//   w.show(name) → true when the doc declares it    w.open(name) → true when it went somewhere
//   w.count() → how many references the doc declares
// =============================================================================

class DocReferences extends ContentWidget {

    /** It fills the box it is lent: a column of a doc's page, not a part of its flow. */
    static SIZING = "fill";

    constructor(container, params) {
        super(container, params, REFERENCES, "references");
        css.addClass(this.root, dw_refs);
        this._rows = null;
        this._grid = null;
        this._cursor = null;   // the reference the grid's cursor is on
        this._said = null;
        this._pending = null;
    }

    count() { return this._rows ? this._rows.all().length : 0; }

    show(name) {
        if (!this._grid) { this._pending = name; return false; }   // not drawn yet: shown once it is
        if (!this._rows.row(name)) return false;
        this._grid.selectCell(name, "name");
        this._cursor = name;
        this._grid.focus();
        return true;
    }

    open(name) {
        var ref = this._rows && this._rows.row(name);
        if (!ref) return false;
        if (ref.kind === "doc" && ref.to) { HrefManagerInstance.navigate(ref.to); return true; }
        if (ref.kind === "external" && ref.to) { HrefManagerInstance.openNew(ref.to); return true; }
        this._say(ref.kind === "unplaced" ? "“" + (ref.title || ref.name) + "” is placed nowhere on this site: there is no page to open."
                                          : "“" + (ref.title || ref.name) + "” has no page to open.");
        return false;
    }

    _draw(content, branch, into) {
        var rows = content.rows || [], self = this;
        var head = branch.createElement("head", "div");
        css.addClass(head, dw_refs_head);
        var title = branch.createElement("title", "h2");
        css.addClass(title, dw_refs_title);
        title.textContent = "References";
        var count = branch.createElement("count", "span");
        css.addClass(count, dw_refs_count);
        count.textContent = String(rows.length);
        head.appendChild(title);
        head.appendChild(count);
        into.appendChild(head);
        this._said = branch.createElement("said", "p");
        css.addClass(this._said, dw_note);
        css.addClass(this._said, dw_hidden);
        if (!rows.length) {
            this._said.textContent = "The doc declares no references.";
            css.toggleClass(this._said, dw_hidden, false);
            into.appendChild(this._said);
            return;
        }
        var box = branch.createElement("box", "div");
        css.addClass(box, dw_refs_box);
        into.appendChild(box);
        into.appendChild(this._said);
        this._rows = new ReferenceRows({ branch: branch.createBranch("cells") });
        this._rows.show(rows);
        this._grid = new RelGrid({ container: box, branch: branch.createBranch("grid"), relation: this._rows, label: "References",
            header: { show: true, sticky: true }, onCursorMoved: function (pk) { self._cursor = pk; } });
        box.addEventListener("keydown", function (ev) { if (ev.key === "Enter" && self._cursor) { ev.preventDefault(); self.open(self._cursor); } });
        box.addEventListener("dblclick", function () { if (self._cursor) self.open(self._cursor); });
        if (this._pending) { var pending = this._pending; this._pending = null; this.show(pending); }
    }

    _say(text) {
        this._said.textContent = text;
        css.toggleClass(this._said, dw_hidden, !text);
    }
}
