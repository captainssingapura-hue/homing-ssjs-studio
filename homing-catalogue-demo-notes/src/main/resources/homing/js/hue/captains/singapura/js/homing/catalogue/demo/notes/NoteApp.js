// =============================================================================
// NoteApp — a note, as a page: its title, when it was written, and its text,
// a paragraph to each blank line. What it shows is its params; where it sits,
// the chrome's trail says - the page knows neither the catalogue nor the site.
// =============================================================================

const _noteOwner = Object.freeze({ toString: () => "note" });

class NoteView {
    constructor(branch, note) {
        branch.activate(_noteOwner);
        var root = NoteView._el(branch, "root", "article", nt_root);
        NoteView._el(branch, "title", "h1", nt_title, root).textContent = note.title;
        if (note.when) NoteView._el(branch, "when", "p", nt_when, root).textContent = note.when;
        String(note.text || "").split(/\n\s*\n/).forEach(function (para, i) {
            if (para.trim()) NoteView._el(branch, "para" + i, "p", nt_para, root).textContent = para.trim();
        });
        this.root = root;
    }

    static _el(branch, name, tag, cls, parent) {
        var el = branch.createElement(name, tag);
        css.addClass(el, cls);
        if (parent) parent.appendChild(el);
        return el;
    }
}

function appMain(el, params) {
    el.appendChild(new NoteView(domOpsParty.createBranch("note"), params).root);
}
