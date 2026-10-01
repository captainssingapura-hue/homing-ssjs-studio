// =============================================================================
// DocReferences — a doc's references, as the doc's last section shows them: a
// list, each reference an entry. Its title, a link where it goes - a page of the
// site in place, an address off it in a tab of its own; what it is, when it is
// not a page of the site - off it, or placed nowhere; its summary; where it
// goes; and the sections that cite it, each a link back there. Asked to show
// one, its entry is marked and brought into view - never given the focus: the
// keys stay where they were. A ContentWidget of the type references, asked by
// its doc; it flows, as tall as its list.
//
//   new DocReferences(container, params)   params: { doc, key? }
//   w.show(name) → true when the doc declares it    w.count()
// =============================================================================

class DocReferences extends ContentWidget {

    constructor(container, params) {
        super(container, params, REFERENCES, "references");
        this._entries = new Map();   // name → its entry
        this._marked = null;
        this._pending = null;
        this._drawn = false;
    }

    count() { return this._entries.size; }

    show(name) {
        if (!this._drawn) { this._pending = name; return false; }   // not drawn yet: shown once it is
        var entry = this._entries.get(name);
        if (!entry) return false;
        if (this._marked) css.toggleClass(this._marked, dw_ref_current, false);
        css.toggleClass(entry, dw_ref_current, true);
        this._marked = entry;
        entry.scrollIntoView({ block: "center" });
        return true;
    }

    _draw(content, branch, into) {
        var rows = content.rows || [], self = this;
        this._drawn = true;
        if (!rows.length) {
            var none = branch.createElement("none", "p");
            css.addClass(none, dw_note);
            none.textContent = "The doc declares no references.";
            into.appendChild(none);
            return;
        }
        var list = branch.createElement("list", "ol");
        css.addClass(list, dw_refs);
        rows.forEach(function (ref, i) {
            var entry = self._entry(branch, ref, i);
            self._entries.set(ref.name, entry);
            list.appendChild(entry);
        });
        into.appendChild(list);
        if (this._pending) { var pending = this._pending; this._pending = null; this.show(pending); }
    }

    /** One reference: its title - a link where it goes - and what it is; its summary; where it goes, and where it is cited. */
    _entry(branch, ref, i) {
        var entry = branch.createElement("ref" + i, "li");
        css.addClass(entry, dw_ref);
        var head = branch.createElement("head" + i, "div");
        css.addClass(head, dw_ref_head);
        var goes = (ref.kind === "doc" || ref.kind === "external") && ref.to;
        var title = branch.createElement("title" + i, goes ? "a" : "span");
        css.addClass(title, dw_ref_title);
        title.textContent = ref.title || ref.name;
        if (goes) {
            css.addClass(title, dw_link);
            HrefManagerInstance.set(title, ref.to);
            if (ref.kind === "external") { title.setAttribute("target", "_blank"); title.setAttribute("rel", "noopener noreferrer"); }
        }
        head.appendChild(title);
        var kind = DocReferences.KINDS[ref.kind];
        if (kind) {
            var badge = branch.createElement("kind" + i, "span");
            css.addClass(badge, dw_badge);
            css.addClass(badge, ref.kind === "unplaced" ? dw_warning : dw_dim);
            badge.textContent = kind;
            head.appendChild(badge);
        }
        entry.appendChild(head);
        if (ref.summary) {
            var summary = branch.createElement("summary" + i, "p");
            css.addClass(summary, dw_ref_summary);
            summary.textContent = ref.summary;
            entry.appendChild(summary);
        }
        entry.appendChild(this._meta(branch, ref, i));
        return entry;
    }

    /** Where it goes, and the sections citing it - each a link back to its section. */
    _meta(branch, ref, i) {
        var meta = branch.createElement("meta" + i, "p");
        css.addClass(meta, dw_ref_meta);
        var where = ref.kind === "unplaced" ? "placed nowhere on this site" : ref.to;
        if (where) {
            var at = branch.createElement("where" + i, "span");
            css.addClass(at, ref.kind === "unplaced" ? dw_dim : dw_ref_where);
            at.textContent = where;
            meta.appendChild(at);
            meta.append(" · ");
        }
        if (!ref.citedIn.length) { meta.append("never cited"); return meta; }
        meta.append("cited in ");
        ref.citedIn.forEach(function (c, j) {
            if (j) meta.append(", ");
            var back = branch.createElement("cite" + i + "_" + j, "a");
            css.addClass(back, dw_link);
            HrefManagerInstance.set(back, "#" + c.path);
            back.textContent = c.label;
            meta.appendChild(back);
        });
        return meta;
    }
}

/** What a reference says it is, when it is not a page of the site. */
DocReferences.KINDS = Object.freeze({ external: "external", unplaced: "not placed", image: "image" });
