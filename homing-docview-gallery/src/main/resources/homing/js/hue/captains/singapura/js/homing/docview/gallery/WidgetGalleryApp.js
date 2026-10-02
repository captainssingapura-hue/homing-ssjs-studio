// =============================================================================
// WidgetGalleryApp — a doc's primitives, each working alone. A page of one
// primitive's specimens - or all of them - each a card: what it is called and
// what it shows, the params its widget is made with, and the widget, made from
// its type and those params and nothing else.
//
// The page holds the root content parties - prose, code, table, image - each
// with the content secretary and the steward it hires, which reads the doc a
// part is asked of: a doc's payload once, an image by its key. Every widget is
// joined to them, and asks; one specimen of each primitive names no part, and
// its widget says the content is unavailable.
// =============================================================================

const _galleryOwner = Object.freeze({ toString: () => "docviewGallery" });

/** The primitives, by the type their widgets are of. */
var _GALLERY_KINDS = Object.freeze({ prose: DocProse, code: DocCode, table: DocTable, image: DocImage });

function appMain(el, params) {
    css.addClass(el, gl_page);
    var place = domOpsParty.createBranch("gallery"), n = 0;
    place.activate(_galleryOwner);
    var mk = function (tag, cls, parent) {
        var e = place.createElement("g" + (n++), tag);
        if (cls) css.addClass(e, cls);
        parent.appendChild(e);
        return e;
    };
    var given = {};
    given[PROSE.name] = new MessagingParty(PROSE, ContentSecretary, ProseSteward);
    given[CODE.name] = new MessagingParty(CODE, ContentSecretary, CodeSteward);
    given[TABLE.name] = new MessagingParty(TABLE, ContentSecretary, TableSteward);
    given[IMAGE.name] = new MessagingParty(IMAGE, ContentSecretary, ImageSteward);
    given[DIAGRAM.name] = new MessagingParty(DIAGRAM, ContentSecretary, DiagramSteward);

    var kinds = params.kind === "all" ? Object.keys(_GALLERY_KINDS) : [params.kind];
    var intro = mk("p", gl_intro, el);
    intro.textContent = "Each widget below is made from its type and its params alone - a doc's address and a part's key - and asks "
        + "its content party for what it shows. The page's stewards read the reference docs; one specimen of each names no part.";
    var made = [];
    kinds.forEach(function (kind) {
        var Kind = _GALLERY_KINDS[kind], specimens = GALLERY_SPECIMENS[kind];
        if (!Kind || !specimens) { mk("p", gl_note, el).textContent = "No primitive is called " + kind + "."; return; }
        var section = mk("section", gl_kind, el);
        section.setAttribute("aria-label", kind);
        mk("h2", gl_kind_title, section).textContent = kind;
        specimens.forEach(function (s) {
            var card = mk("article", gl_card, section);
            card.setAttribute("data-specimen", kind + " " + s.key);
            mk("h3", gl_title, card).textContent = s.title;
            mk("p", gl_params, card).textContent = "doc=" + s.doc + "   key=" + s.key;
            mk("p", gl_note, card).textContent = s.note;
            var box = mk("div", null, card), widget = new Kind(box, { doc: s.doc, key: s.key }), slot = "w" + made.length;
            place.graft(slot, widget.roots.dom);
            focusParty.root.graft(slot, widget.roots.focus);
            made.push(widget);
        });
    });
    made.forEach(function (w) { w.join(given); });
}
