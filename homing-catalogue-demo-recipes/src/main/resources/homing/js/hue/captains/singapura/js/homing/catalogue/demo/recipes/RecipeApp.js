// =============================================================================
// RecipeApp — a recipe, as a page: its name, how many it serves, what goes in
// and how. What it shows is its params - a list of one arrives as its one
// value, so each is read as a list; where it sits, the chrome's trail says.
// =============================================================================

const _recipeOwner = Object.freeze({ toString: () => "recipe" });

class RecipeCard {
    constructor(branch, recipe) {
        branch.activate(_recipeOwner);
        this._branch = branch;
        var root = this._el("root", "article", rc_root);
        this._el("title", "h1", rc_title, root).textContent = recipe.name;
        this._el("serves", "p", rc_serves, root).textContent = "Serves " + recipe.serves;
        this._list("ingredients", "What goes in", "ul", RecipeCard._many(recipe.ingredient), root);
        this._list("steps", "How", "ol", RecipeCard._many(recipe.step), root);
        this.root = root;
    }

    /** A stamped param as a list: none, one value, or many. */
    static _many(v) { return v == null ? [] : [].concat(v); }

    _list(name, heading, tag, items, parent) {
        this._el(name + "-heading", "h2", rc_heading, parent).textContent = heading;
        var list = this._el(name, tag, rc_list, parent);
        var self = this;
        items.forEach(function (text, i) { self._el(name + i, "li", null, list).textContent = text; });
    }

    _el(name, tag, cls, parent) {
        var el = this._branch.createElement(name, tag);
        if (cls) css.addClass(el, cls);
        if (parent) parent.appendChild(el);
        return el;
    }
}

function appMain(el, params) {
    el.appendChild(new RecipeCard(domOpsParty.createBranch("recipe"), params).root);
}
