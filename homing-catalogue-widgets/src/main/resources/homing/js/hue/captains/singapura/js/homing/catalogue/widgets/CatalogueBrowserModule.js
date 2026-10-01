// =============================================================================
// CatalogueBrowser — a site's catalogue browsed: the tree and the details of
// what is picked, side by side on one sheet - a panel lifted off the page once,
// in its middle part and never narrower than its least, everything on it flat:
// the tree the lesser part of a golden split, a hairline between them, each
// scrolling on its own.
// A composed widget, an umbrella over two of its own (CatalogueTree,
// CatalogueDetails), each lent a box of the browser's own and grafted: its
// DomOps party into the browser's, its focus party into the browser's - which
// the browser offers its own host, whole.
//
// Where they meet is a catalogue party of the browser's own: a scope. The tree
// tells it what is picked, and the details show it. The scope is linked to the
// party the browser is given, and its secretary (CatalogueScopeSecretary)
// keeps the edge: a pick made in the scope goes up, and an asking to open
// always - opening is the host's; a question is answered in the scope; what
// the party above says is picked, both are told. Joining is top-down: the
// scope made and linked, and the party above asked, before the tree joins and
// then the details; leaving is the other way round. Joined with nothing given,
// the scope stands alone, and the tree and the details still meet in it.
//
//   new CatalogueBrowser(container, params)   params: { at } - the catalogue browsed from, "/" by default
//   browser.root  browser.roots   { dom, focus }: its own, its subordinates' grafted in them
//   browser.join(given)   given: { [type name]: party }; a second join without a leave is refused
//   browser.leave()   browser.scope()   browser.activate()   browser.dispose()
// =============================================================================

const _catalogueBrowserOwner = Object.freeze({ toString: () => "catalogueBrowser" });
var _catalogueBrowsers = 0;

class CatalogueBrowser {
    constructor(container, params) {
        if (!container || typeof container.appendChild !== "function") throw new Error("[CatalogueBrowser] a container is required: the one its host lends it");
        var name = "catalogueBrowser-" + (++_catalogueBrowsers);
        this._dom = domOpsParties.mobile(name);
        this._dom.activate(_catalogueBrowserOwner);
        var root = this._dom.createElement("root", "div");
        css.addClass(root, wg_fill);
        css.addClass(root, cw_browser);
        root.setAttribute("role", "region");
        root.setAttribute("aria-label", "Catalogue, and the entry picked");
        container.appendChild(root);
        // one sheet, in the middle of the page and never less than its least: a panel, lifted off the
        // page once - in a design that draws depth, the only thing that is - and what it holds lies
        // flat on it, nothing raised off the sheet
        var place = this._dom.createElement("place", "div");
        css.addClass(place, cw_sheet);
        root.appendChild(place);
        this._sheet = new PanelBuilder().fills().host(place).build(this._dom.createBranch("sheet"));   // the panel activates the branch it is given
        this._sheet.elevation("elevated");
        var split = this._dom.createElement("split", "div");
        css.addClass(split, cw_split);
        var treeBox = this._dom.createElement("treeBox", "div");
        css.addClass(treeBox, cw_pane);
        css.addClass(treeBox, cw_pane_tree);
        var detailsBox = this._dom.createElement("detailsBox", "div");
        css.addClass(detailsBox, cw_pane);
        css.addClass(detailsBox, cw_pane_details);
        split.appendChild(treeBox);
        split.appendChild(detailsBox);
        this._sheet.body.appendChild(split);
        this.root = root;
        this._focusParty = focusParties.mobile(name);
        this._tree = new CatalogueTree(treeBox, { at: (params && params.at) || "/" });
        this._details = new CatalogueDetails(detailsBox, {});
        this._dom.graft("tree", this._tree.roots.dom);
        this._dom.graft("details", this._details.roots.dom);
        this._focusParty.root.graft("tree", this._tree.roots.focus);
        this._focusParty.root.graft("details", this._details.roots.focus);
        this.roots = Object.freeze({ dom: this._dom, focus: this._focusParty });
        this._scope = null;
        this._joined = false;
    }

    /** Joined: its own first - the scope made, linked above, and the party above asked - then its subordinates, in order. */
    join(given) {
        if (this._joined) throw new Error("[CatalogueBrowser] joined already: leave first");
        this._joined = true;
        if (!this._scope) this._scope = new MessagingParty(CATALOGUE, CatalogueScopeSecretary);
        var above = given && given[CATALOGUE.name];
        if (above) this._scope.link(above, "catalogueBrowser").tell({ kind: "CurrentRequested" });
        var mine = {};
        mine[CATALOGUE.name] = this._scope;
        this._tree.join(mine);
        this._details.join(mine);
    }

    /** Left the other way round: its subordinates, the last first, then its own link above. The scope's memory is kept. */
    leave() {
        if (!this._joined) return;
        this._details.leave();
        this._tree.leave();
        this._scope.unlink();
        this._joined = false;
    }

    scope() { return this._scope; }

    /** Asked for the keys: its tree is asked. A host never claims for what it holds. */
    activate() { this._tree.activate(); }

    dispose() {
        this.leave();
        this._details.dispose();
        this._tree.dispose();
        this._sheet.dispose();
        this._focusParty.dissolve();
        this._dom.dissolve();
    }
}
