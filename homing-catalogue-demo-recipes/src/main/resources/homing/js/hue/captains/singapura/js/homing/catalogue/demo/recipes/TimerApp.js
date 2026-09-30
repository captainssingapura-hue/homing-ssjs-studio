// =============================================================================
// TimerApp — a kitchen timer, as a page: what it is for, the minutes and
// seconds left, and two buttons - start or pause, and reset. What it counts is
// its params; it is a tool used beside a recipe, so its leaf opens it beside.
// =============================================================================

const _timerOwner = Object.freeze({ toString: () => "timer" });

class KitchenTimer {
    constructor(branch, params) {
        branch.activate(_timerOwner);
        this._branch = branch;
        this._total = Math.max(1, Number(params.minutes) || 5) * 60;
        this._left = this._total;
        this._tick = null;
        var root = this._el("root", "article", rc_root);
        this._el("title", "h1", rc_title, root).textContent = params.label || "Timer";
        this._clock = this._el("clock", "p", rc_clock, root);
        this._note = this._el("note", "p", rc_serves, root);
        var row = this._el("row", "div", rc_row, root);
        var self = this;
        this._go = this._button("go", "Start", "primary", function () { self._toggle(); }, row);
        this._reset = this._button("reset", "Reset", "plain", function () { self._stop(); self._left = self._total; self._show(); }, row);
        this.root = root;
        this._show();
    }

    _button(name, label, colour, fn, parent) {
        var builder = new ButtonBuilder(), el = this._branch.createElement(name, builder.tag);
        var b = builder.label(label).colour(colour).onClick(fn).build(el);
        parent.appendChild(el);
        return b;
    }

    _toggle() {
        if (this._tick) { this._stop(); this._show(); return; }
        if (this._left === 0) this._left = this._total;
        var self = this;
        this._tick = setInterval(function () {
            self._left = Math.max(0, self._left - 1);
            if (self._left === 0) self._stop();
            self._show();
        }, 1000);
        this._show();
    }

    _stop() { if (this._tick) { clearInterval(this._tick); this._tick = null; } }

    _show() {
        var m = Math.floor(this._left / 60), s = this._left % 60;
        this._clock.textContent = (m < 10 ? "0" : "") + m + ":" + (s < 10 ? "0" : "") + s;
        this._go.label(this._tick ? "Pause" : (this._left === this._total ? "Start" : (this._left === 0 ? "Again" : "Go on")));
        this._note.textContent = this._left === 0 ? "Done." : (this._tick ? "Counting down." : "Counts " + Math.round(this._total / 60) + " minutes down.");
    }

    _el(name, tag, cls, parent) {
        var el = this._branch.createElement(name, tag);
        if (cls) css.addClass(el, cls);
        if (parent) parent.appendChild(el);
        return el;
    }
}

function appMain(el, params) {
    el.appendChild(new KitchenTimer(domOpsParty.createBranch("timer"), params).root);
}
