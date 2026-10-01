// =============================================================================
// MermaidEngine — mermaid's diagrams drawn as SVG, for the diagram steward. The
// library is imported the first time a diagram is drawn, from the address the
// server resolved for it, and never before: a page with no diagram never
// reaches for it. Diagrams are drawn one at a time - mermaid draws in a scratch
// element of the page's own - in the design's colours: mermaid's base theme,
// each of its variables the design's value for a pair (MERMAID_PALETTE) - its
// ground the plate's, its nodes raised and edged in the accent, its text the
// body's ink, its lines a muted one - and dark when that ground is. A diagram
// mermaid cannot read is refused with why, and a library that cannot be loaded
// refuses every diagram after it.
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

    /** The library, once: imported when first wanted, and set to the design's colours. */
    static _library() {
        if (!MermaidEngine._loading) {
            MermaidEngine._loading = import(MERMAID_LIBRARY).then(function (lib) {
                var themed = MermaidEngine.themeVariables();
                lib.mermaid.initialize(Object.assign({ startOnLoad: false, securityLevel: "strict", suppressErrorRendering: true },
                    themed ? { theme: "base", themeVariables: themed } : { theme: "default" }));
                return lib.mermaid;
            }, function (e) {
                throw new Error("the mermaid library could not be loaded: " + MermaidEngine._why(e));
            });
        }
        return MermaidEngine._loading;
    }

    /**
     * Mermaid's base theme in the design's colours: each variable the design's value, read from the
     * page's root and painted on a pixel - so any way a design writes a colour is read the same - as
     * hex; a colour the design leaves see-through laid on the ground. A colour the design does not
     * give takes its neighbour's: a node the ground's, a cluster a node's. Dark when the ground is.
     * No ground to read - no design on the page - and there is nothing to theme: null, mermaid's own.
     */
    static themeVariables() {
        var root = getComputedStyle(document.documentElement), read = function (name) { return root.getPropertyValue(MERMAID_PALETTE[name]).trim(); };
        var ground = MermaidEngine._paint(read("background"), null);
        if (!ground) return null;
        var out = { background: ground };
        Object.keys(MERMAID_PALETTE).forEach(function (name) {
            if (name === "background") return;
            var value = read(name);
            if (name === "fontFamily") { if (value) out.fontFamily = value; return; }
            var hex = value ? MermaidEngine._paint(value, ground) : null;
            if (hex) out[name] = hex;
        });
        out.primaryColor = out.primaryColor || ground;
        out.secondaryColor = out.secondaryColor || out.primaryColor;
        out.tertiaryColor = out.tertiaryColor || out.primaryColor;
        out.edgeLabelBackground = ground;
        out.darkMode = MermaidEngine._luminance(ground) < 0.5;
        return out;
    }

    /** A colour - laid on the ground, when there is one - read off a pixel: "#rrggbb"; or null, when it is no colour, or nothing shows. */
    static _paint(value, ground) {
        if (!value || typeof OffscreenCanvas !== "function" || !CSS.supports("color", value)) return null;
        var pen = new OffscreenCanvas(1, 1).getContext("2d");
        if (ground) { pen.fillStyle = ground; pen.fillRect(0, 0, 1, 1); }
        pen.fillStyle = value;
        pen.fillRect(0, 0, 1, 1);
        var px = pen.getImageData(0, 0, 1, 1).data;
        if (px[3] === 0) return null;
        return "#" + [px[0], px[1], px[2]].map(function (n) { return (n < 16 ? "0" : "") + n.toString(16); }).join("");
    }

    static _luminance(hex) {
        var n = parseInt(hex.slice(1), 16);
        return (0.2126 * ((n >> 16) & 255) + 0.7152 * ((n >> 8) & 255) + 0.0722 * (n & 255)) / 255;
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
