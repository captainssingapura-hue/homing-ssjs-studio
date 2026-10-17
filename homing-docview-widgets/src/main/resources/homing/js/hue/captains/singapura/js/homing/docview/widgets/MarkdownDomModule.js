// =============================================================================
// MarkdownDom — markdown made into elements, from the parser's tokens (marked's
// lexer): every element made through the branch it is given, never a string of
// markup. Blocks - paragraphs, headings, lists, quotes, code, tables, rules;
// and what is inline in them - emphasis, strong, code spans, links, line
// breaks. Raw HTML in the text is shown as the text it is.
//
// A link goes through the page's href manager; a citation, #ref:name, is drawn
// as one until the references say what it names.
//
// A TABLE is a ProseTable made from a DocTableRelation - a grid that reads as a
// document's table, its cells' text drawn by this inline markdown - and every
// table made is remembered: where the keys go when the owner is given them,
// and what goes with the owner.
//
//   new MarkdownDom(branch)   the branch is activated by its owner
//   md.blocks(text, into)     markdown's blocks, appended to `into`
//   md.inline(text, into)     a line's inline markdown - a table cell's - appended to `into`
//   md.table(relation, into)  a DocTableRelation as a ProseTable, appended to `into`; → the table
//   md.tables()               every table made     md.keysHost()   the last pressed table's, else the first
//   md.dispose()              every table made, taken down: the owner's to call
// =============================================================================

/** The house's headings, a leaf a level. */
const _MD_HEADINGS = Object.freeze([1, 2, 3, 4, 5, 6].map(function (n) { return Object.freeze({ LEAF: "heading" + n }); }));

class MarkdownDom {
    constructor(branch) {
        if (!branch) throw new Error("[MarkdownDom] a branch is required: the one it makes its elements in");
        this._branch = branch;
        this._n = 0;
        this._tables = [];
        this._pressed = null;
    }

    blocks(text, into) { this._blocks(marked.lexer(String(text || "")), into); }

    inline(text, into) { this._inline(marked.Lexer.lexInline(String(text || "")), into); }

    _el(tag, cls, parent) {
        var el = this._branch.createElement("md" + (this._n++), tag);
        if (cls) css.addClass(el, cls);
        if (parent) parent.appendChild(el);
        return el;
    }

    _blocks(tokens, into) {
        var self = this;
        tokens.forEach(function (t) { self._block(t, into); });
    }

    _block(t, into) {
        switch (t.type) {
            case "space": case "def": return;
            case "paragraph": this._inline(t.tokens, this._el("p", dw_para, into)); return;
            case "heading": this._heading(t, into); return;
            case "text": this._inline(t.tokens || [{ type: "text", text: t.text }], this._el("span", null, into)); return;
            case "blockquote": this._blocks(t.tokens, DocLooks.wear(this._el("blockquote", dw_quote, into), "quote")); return;
            case "list": this._list(t, into); return;
            case "code": DocLooks.wear(this._el("pre", dw_pre, into), "code-block").textContent = t.text; return;
            case "table": this._table(t, into); return;
            case "hr": DocLooks.wear(this._el("hr", dw_rule, into), "separator"); return;
            case "html": this._el("p", dw_para, into).textContent = t.raw; return;
            default: this._el("p", dw_para, into).textContent = t.raw || "";
        }
    }

    /** A heading, two levels under its doc's sections: the native one, in the house's look of its level - it holds links and code. */
    _heading(t, into) {
        var level = Math.min(t.depth + 2, 6), h = this._el("h" + level, dw_heading, into);
        new Look(_MD_HEADINGS[level - 1], ROLE.STANDALONE).wear(h);
        this._inline(t.tokens, h);
    }

    _list(t, into) {
        var list = this._el(t.ordered ? "ol" : "ul", dw_list, into), self = this;
        if (t.ordered && t.start !== "" && t.start !== 1) list.setAttribute("start", String(t.start));
        t.items.forEach(function (item) {
            var li = self._el("li", null, list);
            if (item.task) li.append(item.checked ? "[x] " : "[ ] ");
            self._blocks(item.tokens, li);
        });
    }

    /** A markdown table: its head's and its rows' text, each column set as the table says, as a ProseTable. */
    _table(t, into) {
        this.table(new DocTableRelation({
            columns: t.header.map(function (cell, c) { return { title: cell.text, align: t.align[c] || null }; }),
            rows: t.rows.map(function (row) { return row.map(function (cell) { return cell.text; }); })
        }), into);
    }

    /** A doc's table, from what it says: a ProseTable, its text this inline markdown, remembered. */
    table(relation, into) {
        var self = this;
        var table = new ProseTable(this._branch.createBranch("table" + this._tables.length), relation,
                                   { inline: function (text, el) { self.inline(text, el); } });
        table.root.addEventListener("mousedown", function () { self._pressed = table; });
        into.appendChild(table.root);
        this._tables.push(table);
        return table;
    }

    tables() { return this._tables.slice(); }

    /** Where the keys go when the owner is given them: the table last pressed, else the first. */
    keysHost() {
        var table = this._pressed || this._tables[0];
        return table && table.grid ? table.grid.el() : null;
    }

    dispose() {
        this._tables.forEach(function (t) { t.dispose(); });
        this._tables = [];
        this._pressed = null;
    }

    _inline(tokens, into) {
        var self = this;
        (tokens || []).forEach(function (t) { self._run(t, into); });
        return into;
    }

    _run(t, into) {
        switch (t.type) {
            case "text": if (t.tokens) this._inline(t.tokens, into); else into.append(MarkdownDom._unescaped(t.text)); return;
            case "escape": into.append(MarkdownDom._unescaped(t.text)); return;
            case "strong": this._inline(t.tokens, this._el("strong", null, into)); return;
            case "em": this._inline(t.tokens, this._el("em", null, into)); return;
            case "del": this._inline(t.tokens, this._el("del", null, into)); return;
            case "codespan": DocLooks.wear(this._el("code", dw_code_span, into), "code").textContent = MarkdownDom._unescaped(t.text); return;
            case "br": this._el("br", null, into); return;
            case "link": this._link(t, into); return;
            case "image": into.append(t.text || ""); return;
            case "html": into.append(t.raw); return;
            default: into.append(t.raw || "");
        }
    }

    /**
     * A link, followed through the href manager. A citation - {@code #ref:name} - is a link too, to
     * its fragment, so it is reached and followed as any link is; it says which reference it cites
     * (data-ref), and what following it means is the page's: the widget never knows the references.
     */
    _link(t, into) {
        var { href: target } = t;   // the token's own field: the address the link was written with
        if (String(target).indexOf("#ref:") === 0) {
            var cite = DocLooks.wear(this._el("a", dw_cite, into), "link");
            HrefManagerInstance.set(cite, target);
            cite.setAttribute("data-ref", String(target).slice(5));
            this._inline(t.tokens, cite);
            return;
        }
        var a = DocLooks.wear(this._el("a", dw_link, into), "link");
        HrefManagerInstance.set(a, target);
        if (t.title) a.setAttribute("title", t.title);
        this._inline(t.tokens, a);
    }

    /** Text as the lexer leaves it, its entities read back: what the reader wrote. */
    static _unescaped(s) {
        return String(s).replace(/&(amp|lt|gt|quot|#39);/g, function (m, e) {
            return { amp: "&", lt: "<", gt: ">", quot: "\"", "#39": "'" }[e];
        });
    }
}
