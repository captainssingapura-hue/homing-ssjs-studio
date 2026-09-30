// =============================================================================
// CatalogueListingApp — a catalogue's listing as a page of the site: the
// CatalogueListing of the catalogue whose address was stamped into the page,
// in the MPA's main slot, under the chrome every page of the site wears.
// =============================================================================

function appMain(el, params) {
    el.appendChild(new CatalogueListing(domOpsParty.createBranch("catalogueListing"), params.path).root);
}
