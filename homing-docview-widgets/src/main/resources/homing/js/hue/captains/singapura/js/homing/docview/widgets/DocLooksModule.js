// =============================================================================
// DocLooks — a document's elements in the house's look: each the leaf it is, worn
// inline on the native element a doc's widget makes - a quote, a block of code, a
// table and its cells, code, a link, a caption, a tab, a badge of a tone. The
// element stays what the browser makes it - a quote's blocks, a cell's code and
// links stay inside it - and wears its leaf's look, as the native controls do.
//
//   DocLooks.wear(el, "quote")          → el, in the quote's look
//   DocLooks.wear(el, "tab", part)      part: { only | except } as Look's
//   DocLooks.current(el, on)            the one the reader is on, said by its reflection (aria-current):
//                                       the leaf's look draws it; off, the reflection is taken away
// =============================================================================

class DocLooks {
    static wear(el, leaf, part) {
        var look = DocLooks._looks[leaf];
        if (!look) look = DocLooks._looks[leaf] = new Look(Object.freeze({ LEAF: leaf }), ROLE.STANDALONE);
        look.wear(el, part);
        return el;
    }
}

/** Its state, the current one, shown as a leaf shows it: by its reflection, which its look's treatment draws. */
DocLooks.current = function (el, on) {
    if (on) el.setAttribute("aria-current", "true"); else el.removeAttribute("aria-current");
    return el;
};

/** A look per leaf, made once and worn as often as asked. */
DocLooks._looks = {};
