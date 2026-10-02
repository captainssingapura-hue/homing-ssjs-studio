// =============================================================================
// WorkbenchWidget — what every workbench widget is to its host, said once; a
// workbench widget extends it and adds what it shows.
//
// A self-contained widget (the Workspace & Widgets doctrines): made with the
// container its host lends it and its params, and nothing else; its DomOps and
// focus parties its own, offered as roots for its host to graft; the workbench
// party declared by type in Java, and joined after it is made. Not joined, it
// works alone: a navigator still walks, a pane still shows what it is told.
//
//   class NodeSummary extends WorkbenchWidget {
//       constructor(container, params) { super(container, "nodeSummary", "Summary"); ... }
//       hears()  { return { Picked: m => ... }; }   the workbench kinds it follows
//       joined() { this.tell({ kind: "CurrentRequested" }); }
//       disposed() { ...what it holds beside its branch... }
//   }
//   widget.root  widget.roots { dom, focus }  widget.focus  widget.branch
//   widget.join(given)  widget.leave()   given: { [type name]: party }
//   widget.member       its workbench membership while joined, null otherwise
//   widget.tell(m)      told when joined, nothing when not
//   widget.pick(to)     a pick, told when joined
//   widget.open(to)     an asking to open, told when joined
//   widget.alive        true until it is disposed
//   widget.activate()   widget.keyDown(ev)   widget.dispose()
//   widget.text(name, tag, cls, text, into?)   an element of its own, its text set, appended
// =============================================================================

var _workbenchWidgets = 0;

class WorkbenchWidget {
    constructor(container, name, label) {
        if (!container || typeof container.appendChild !== "function") throw new Error("[" + name + "] a container is required: the one its host lends it");
        var n = name + "-" + (++_workbenchWidgets);
        this._name = name;
        this._dom = domOpsParties.mobile(n);
        this._dom.activate(Object.freeze({ toString: function () { return name; } }));
        var root = this._dom.createElement("root", "div");
        css.addClass(root, wg_fill);
        root.setAttribute("role", "region");
        root.setAttribute("aria-label", label);
        this.root = root;
        this._focusParty = focusParties.mobile(n);
        this.focus = this._focusParty.root.join(name, this);
        this._off = Keys.claimOn(root, this.focus);
        this.roots = Object.freeze({ dom: this._dom, focus: this._focusParty });
        this.member = null;
        this.alive = true;
        this._joined = false;
        container.appendChild(root);
    }

    /** The widget's own branch: what it mints its elements and sub-branches on. */
    get branch() { return this._dom; }

    /** The workbench kinds it follows: a reactor a kind. None, unless said. */
    hears() { return {}; }

    /** Called once it has joined what it was given. */
    joined() {}

    /** Called as it is disposed, before its branch goes. */
    disposed() {}

    join(given) {
        if (this._joined) throw new Error("[" + this._name + "] joined already: leave first");
        this._joined = true;
        var party = given && given[WORKBENCH.name];
        if (party) this.member = party.join(this._name, this.hears());
        this.joined();
    }

    leave() {
        if (this.member) { this.member.leave(); this.member = null; }
        this._joined = false;
    }

    tell(m) { if (this.member) this.member.tell(m); }

    pick(to) { this.tell({ kind: "Pick", to: to }); }

    open(to) { this.tell({ kind: "Open", to: to }); }

    /** An element on a branch - its own unless said - its class worn, its text set, appended to `into` when given. */
    text(name, tag, cls, text, into, branch) {
        var el = (branch || this.branch).createElement(name, tag);
        if (cls) css.addClass(el, cls);
        if (text != null) el.textContent = text;
        if (into) into.appendChild(el);
        return el;
    }

    activate() { Keys.claim(this.focus); }

    /** Holding the keys, nothing natively focused: Escape gives them back. */
    keyDown(ev) {
        if (ev.key === "Escape") { Keys.yield(this.focus); return true; }
        return false;
    }

    dispose() {
        this.alive = false;
        this.leave();
        this.disposed();
        if (this._off) { this._off(); this._off = null; }
        this._focusParty.dissolve();
        this._dom.dissolve();
    }
}
