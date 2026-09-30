// =============================================================================
// CatalogueListingApp — a catalogue as a page of the site: the catalogue
// widgets' host, at the catalogue whose address was stamped into the page, in
// the MPA's main slot, under the chrome every page of the site wears.
//
// It offers two views, a bar of two buttons over the box it lends the one
// shown: the tree - the site's whole catalogue, and the details of what is
// picked (CatalogueBrowser) - or the cards - this catalogue's own, as tiles
// (CatalogueCards). The view chosen is kept for the visit, so browsing on
// keeps it.
//
// The page is the substrate of the catalogue party the widgets meet in
// (Messaging Parties Are Joined Top-Down): a party at the root, its own
// secretary the type's; the page a member of it, first - it picks its own
// catalogue, so the tree opens with the cursor on it and the details show it -
// then the widget, made, grafted and joined. Opening is the page's: on
// Opening it goes where the entry's app says - in place, or beside.
// =============================================================================

const _listingOwner = Object.freeze({ toString: () => "catalogueListing" });
var _LISTING_VIEWS = Object.freeze({
    tree:  Object.freeze({ label: "Tree",  make: function (box, at) { return new CatalogueBrowser(box, { at: "/" }); } }),
    cards: Object.freeze({ label: "Cards", make: function (box, at) { return new CatalogueCards(box, { at: at }); } })
});
var _LISTING_KEPT = "catalogue-listing-view";

function appMain(el, params) {
    var at = (params && params.path) || "/";
    css.addClass(el, cl_page);
    var place = domOpsParty.createBranch("catalogueListing");
    place.activate(_listingOwner);
    var party = new MessagingParty(CATALOGUE, CatalogueChoiceSecretary);
    party.join("page", { Opening: function (m) { CatalogueWidget.go({ to: m.to, opens: m.opens }); } })
         .tell({ kind: "Pick", to: at });
    var bar = place.createElement("bar", "div");
    css.addClass(bar, cl_bar);
    bar.setAttribute("role", "group");
    bar.setAttribute("aria-label", "View");
    var box = place.createElement("box", "div");
    css.addClass(box, cl_host);
    el.appendChild(bar);
    el.appendChild(box);
    var given = {};
    given[CATALOGUE.name] = party;
    var buttons = {}, shown = { name: null, widget: null };
    function show(name) {
        if (shown.name === name) return;
        if (shown.widget) shown.widget.dispose();
        var widget = _LISTING_VIEWS[name].make(box, at);
        place.graft("widget", widget.roots.dom);
        focusParty.root.graft("widget", widget.roots.focus);
        widget.join(given);
        shown.name = name;
        shown.widget = widget;
        Object.keys(buttons).forEach(function (n) {
            buttons[n].colour(n === name ? "primary" : "plain");
            buttons[n].el.setAttribute("aria-pressed", String(n === name));
        });
        _keep(name);
    }
    Object.keys(_LISTING_VIEWS).forEach(function (name) {
        var builder = new ButtonBuilder(), b = place.createElement("view-" + name, builder.tag);
        buttons[name] = builder.label(_LISTING_VIEWS[name].label).size(-0.5).onClick(function () { show(name); }).build(b);
        bar.appendChild(b);
    });
    show(_kept());
}

/** The view kept for the visit - the tree, when none is, or storage is not to be had. */
function _kept() {
    try {
        var v = sessionStorage.getItem(_LISTING_KEPT);
        return _LISTING_VIEWS[v] ? v : "tree";
    } catch (e) { return "tree"; }
}

function _keep(name) {
    try { sessionStorage.setItem(_LISTING_KEPT, name); } catch (e) { /* the view is not kept, and that is all */ }
}
