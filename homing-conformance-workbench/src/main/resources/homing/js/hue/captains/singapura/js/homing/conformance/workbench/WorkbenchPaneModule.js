// =============================================================================
// WorkbenchPane — a workbench pane that shows what it is told: a box it fills,
// scrolling on its own; a hint until there is something to show; and what it
// shows on a branch of its own, put in place of the one before - so a pane that
// is told again and again keeps one view, never a pile. A pane extends it and
// says what it shows (show) when told; the reading, the order and the failing
// are said once, here.
//
//   class NodeSummary extends WorkbenchPane {
//       constructor(container, params) { super(container, "nodeSummary", "Summary", "Pick a node…"); }
//       hears() { return { Picked: m => this.follow(m.to) }; }
//       read(to)        → Promise of what to show for `to`, or null to show the hint
//       show(what, b)   draws `what` on the branch b, returning the element to put in place
//   }
//   pane.follow(to)   read, then shown - unless something else was asked meanwhile
//   pane.hint(text)   the hint shown again, saying this
// =============================================================================

class WorkbenchPane extends WorkbenchWidget {
    constructor(container, name, label, hint) {
        super(container, name, label);
        this._box = this.text("box", "div", wb_pane, null, this.root);
        this._hint = this.text("hint", "p", wb_hint, hint, this._box);
        this._hintText = hint;
        this._view = null;
        this._viewEl = null;
        this._views = 0;
        this._asked = null;
    }

    /** What to show for a key: a subclass's. */
    read(to) { return Promise.resolve(null); }

    /** Draws what was read on its branch, returning the element put in place. A subclass's. */
    show(what, branch) { return null; }

    follow(to) {
        var self = this;
        this._asked = to;
        this.read(to).then(function (what) {
            if (!self.alive || self._asked !== to) return;
            if (what == null) { self.hint(self._hintText); return; }
            self._put(what);
        }, function (err) {
            if (!self.alive || self._asked !== to) return;
            self._clear();
            self._hint.textContent = "It could not be read: " + err.message;
            css.setClass(self._hint, wb_failed);
        });
    }

    hint(text) {
        this._clear();
        this._hint.textContent = text;
        css.setClass(this._hint, wb_hint);
    }

    _put(what) {
        this._clear();
        var branch = this.branch.createBranch("view" + (++this._views));
        branch.activate(this);
        var el = this.show(what, branch);
        if (el) this._box.appendChild(el);
        this._view = branch;
        this._viewEl = el;
        css.toggleClass(this._hint, wb_hidden, true);
    }

    _clear() {
        if (this._view) { this._view.dissolve(); this._view = null; this._viewEl = null; }
        css.toggleClass(this._hint, wb_hidden, false);
    }

    /** A verdict line: ✓ or ✗ and what it is of, in the word its state is. */
    verdict(branch, into, ok, text) {
        var el = this.text("verdict", "p", wb_verdict, (ok ? "✓ " : "✗ ") + text, into, branch);
        css.addClass(el, ok ? wb_success : wb_danger);
        return el;
    }

    /** A section: its heading with how many, then a line each - or, when there are none, that there are none. */
    section(branch, into, name, title, items, clean) {
        this.text(name + "-head", "p", wb_section, title + " (" + items.length + ")", into, branch);
        if (!items.length) { var none = this.text(name + "-none", "p", wb_line, clean || "none", into, branch); css.addClass(none, clean ? wb_success : wb_hint); return; }
        var self = this;
        items.forEach(function (item, i) {
            var line = self.text(name + "-" + i, "p", wb_line, "✗ " + item, into, branch);
            css.addClass(line, wb_danger);
        });
    }

    /** Facts: a key, then its value, a row each. */
    facts(branch, into, rows) {
        var dl = this.text("facts", "dl", wb_facts, null, into, branch), self = this;
        rows.forEach(function (r, i) {
            self.text("k" + i, "dt", wb_key, r[0], dl, branch);
            self.text("v" + i, "dd", wb_value, String(r[1]), dl, branch);
        });
        return dl;
    }

    disposed() { this._clear(); }
}
