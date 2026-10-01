// =============================================================================
// MermaidEngine — mermaid's diagrams drawn as SVG, for the diagram steward. The
// library is imported the first time a diagram is drawn, from the address the
// server resolved for it, and never before: a page with no diagram never
// reaches for it. Diagrams are drawn one at a time - mermaid draws in a scratch
// element of the page's own - in the theme their ground asks for: dark on a
// dark plate. A diagram mermaid cannot read is refused with why, and a
// library that cannot be loaded refuses every diagram after it.
//
//   MermaidEngine.draw(source) → Promise of the SVG markup; rejected with why
// =============================================================================

var _mermaidDrawn = 0;

class MermaidEngine {

    static draw(source) {
        var job = MermaidEngine._queue.then(function () {
            return MermaidEngine._library().then(function (mermaid) { return MermaidEngine._draw(mermaid, String(source)); });
        });
        MermaidEngine._queue = job.catch(function () {});
        return job;
    }

    /** Read first, so a diagram that cannot be read never reaches the page; then drawn under an id of its own. */
    static _draw(mermaid, source) {
        return Promise.resolve().then(function () { return mermaid.parse(source); }).then(function () {
            return mermaid.render("dv-mermaid-" + (++_mermaidDrawn), source);
        }).then(function (drawn) { return drawn.svg; }, function (e) {
            throw new Error("mermaid could not read it: " + MermaidEngine._why(e));
        });
    }

    /** The library, once: imported when first wanted, and set to the page's ground. */
    static _library() {
        if (!MermaidEngine._loading) {
            MermaidEngine._loading = import(MERMAID_LIBRARY).then(function (lib) {
                lib.mermaid.initialize({ startOnLoad: false, securityLevel: "strict", suppressErrorRendering: true,
                    theme: MermaidEngine._dark() ? "dark" : "default" });
                return lib.mermaid;
            }, function (e) {
                throw new Error("the mermaid library could not be loaded: " + MermaidEngine._why(e));
            });
        }
        return MermaidEngine._loading;
    }

    /**
     * Whether a diagram's ground is dark: the design's value for the plate's surface, painted on a
     * pixel - so any way a design writes a colour is read the same - and weighed. Light when it
     * cannot be told.
     */
    static _dark() {
        var ground = getComputedStyle(document.documentElement).getPropertyValue(MERMAID_GROUND).trim();
        if (!ground || typeof OffscreenCanvas !== "function") return false;
        var pen = new OffscreenCanvas(1, 1).getContext("2d");
        pen.fillStyle = ground;
        pen.fillRect(0, 0, 1, 1);
        var px = pen.getImageData(0, 0, 1, 1).data;
        return px[3] > 0 && (0.2126 * px[0] + 0.7152 * px[1] + 0.0722 * px[2]) / 255 < 0.5;
    }

    /** What went wrong, in a line: an error's first and last lines - the place, and what was expected there. */
    static _why(e) {
        var lines = String(e && e.message || e).split("\n").map(function (l) { return l.trim(); })
            .filter(function (l) { return l && !/^-*\^$/.test(l); });
        return lines.length > 2 ? lines[0] + " " + lines[lines.length - 1] : lines.join(" ");
    }
}

MermaidEngine._queue = Promise.resolve();
MermaidEngine._loading = null;
