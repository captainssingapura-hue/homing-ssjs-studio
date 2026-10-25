// =============================================================================
// DocLooks — a document's elements in the house's look: each the leaf it is, worn
// inline on the native element a doc's widget makes - a quote, a block of code,
// code, a link, a caption, a tab, a badge of a tone. The element stays what the
// browser makes it - a quote's blocks, its code and links stay inside it - and
// wears its leaf's look, as the native controls do. A doc's table is not one of
// them: it is a ProseTable, worn by the grid's design.
//
//   DocLooks.wear(el, "quote")          → el, in the quote's look
// =============================================================================

class DocLooks {
    static wear(el, leaf) {
        var look = DocLooks._looks[leaf];
        if (!look) look = DocLooks._looks[leaf] = new Look(Object.freeze({ LEAF: leaf }), ROLE.STANDALONE);
        look.wear(el);
        return el;
    }
}

/** A look per leaf, made once and worn as often as asked. */
DocLooks._looks = {};
