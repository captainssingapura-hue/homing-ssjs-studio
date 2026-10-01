// =============================================================================
// DocViewApp — a doc, viewed. The same app for every doc: it is told which by
// its params - the doc's authentic path - and nothing else. It is the desk
// (DocDesk) with a doc's primitives, which the desk knows already: the contents
// beside the doc, every part a widget asking its content party, the references
// the doc's last section, one stage for every widget that offers itself.
// =============================================================================

function appMain(el, params) {
    DocDesk.open(el, params.doc, params.title, null);
}
