// =============================================================================
// CatalogueListingApp — a catalogue as a page of the site: the catalogue
// widgets' host, at the catalogue whose address was stamped into the page, in
// the MPA's main slot, under the chrome every page of the site wears.
//
// It shows the tree: this catalogue, what is under it - not the site's whole;
// the way up is the chrome's - and the details of what is picked
// (CatalogueBrowser), in the box it lends it.
//
// The page is the substrate of the catalogue party the widgets meet in
// (Messaging Parties Are Joined Top-Down): a party at the root, its own
// secretary the type's; the page a member of it, first - it picks its own
// catalogue, the tree's root row, so the cursor and the details open on it -
// then the widget, made, grafted and joined. Opening is the page's: on Opening
// it goes where the entry's app says - in place, or beside.
// =============================================================================

const _listingOwner = Object.freeze({ toString: () => "catalogueListing" });

function appMain(el, params) {
    var at = (params && params.path) || "/";
    css.addClass(el, cl_page);
    var place = domOpsParty.createBranch("catalogueListing");
    place.activate(_listingOwner);
    var party = new MessagingParty(CATALOGUE, CatalogueChoiceSecretary);
    party.join("page", { Opening: function (m) { CatalogueWidget.go({ to: m.to, opens: m.opens }); } })
         .tell({ kind: "Pick", to: at });
    var box = place.createElement("box", "div");
    css.addClass(box, cl_host);
    el.appendChild(box);
    var given = {};
    given[CATALOGUE.name] = party;
    var widget = new CatalogueBrowser(box, { at: at, here: at });   // the page is the catalogue's: the details offer no way to it
    place.graft("widget", widget.roots.dom);
    focusParty.root.graft("widget", widget.roots.focus);
    widget.join(given);
}
