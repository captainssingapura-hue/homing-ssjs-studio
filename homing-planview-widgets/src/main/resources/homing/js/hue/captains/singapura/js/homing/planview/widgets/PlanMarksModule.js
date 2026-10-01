// =============================================================================
// PlanMarks — the marks a plan's widgets set beside what they say: a status's
// badge, in the colour of how it stands; whether a thing is done, a ring filled
// or not; and progress, a bar as far along as its share. Every element made
// through the branch it is given.
//
//   PlanMarks.badge(branch, name, mark, word)    mark: a status's slug - done, resolved, in-progress, open, blocked, …
//   PlanMarks.check(branch, name, done, word)
//   PlanMarks.bar(branch, name, percent, word)
// =============================================================================

class PlanMarks {

    /** A status's badge: done or resolved went well; in progress or open is under way; blocked is in danger; any other has no colour of its own. */
    static badge(branch, name, mark, word) {
        var b = branch.createElement(name, "span");
        css.addClass(b, dw_badge);
        css.addClass(b, PlanMarks._TONES[mark] || pl_quiet);
        b.textContent = word || mark;
        return b;
    }

    /** Whether a thing is done: a ring, filled when it is - its word its accessible name. */
    static check(branch, name, done, word) {
        var c = branch.createElement(name, "span");
        css.addClass(c, pl_check);
        if (done) css.addClass(c, pl_check_done);
        c.setAttribute("role", "img");
        c.setAttribute("aria-label", word || (done ? "Done" : "Not done"));
        return c;
    }

    /** Progress: a track, and as much of it done as the share - 0 to 100 - which is data on it. */
    static bar(branch, name, percent, word) {
        var share = Math.max(0, Math.min(100, Math.round(Number(percent) || 0)));
        var track = branch.createElement(name, "div");
        css.addClass(track, pl_bar);
        track.setAttribute("role", "progressbar");
        track.setAttribute("aria-valuemin", "0");
        track.setAttribute("aria-valuemax", "100");
        track.setAttribute("aria-valuenow", String(share));
        if (word) track.setAttribute("aria-label", word);
        var done = branch.createElement(name + "Done", "div");
        css.addClass(done, pl_bar_done);
        done.style.setProperty("--pl-done", share + "%");   // DATA, via the runtime var
        track.appendChild(done);
        return track;
    }
}

/** How each status stands, as a colour. */
PlanMarks._TONES = Object.freeze({ "done": dw_success, "resolved": dw_success, "in-progress": dw_warning, "open": dw_warning, "blocked": dw_danger });
