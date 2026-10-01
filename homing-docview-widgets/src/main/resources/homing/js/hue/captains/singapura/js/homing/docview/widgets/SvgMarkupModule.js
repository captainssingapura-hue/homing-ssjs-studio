// =============================================================================
// SvgMarkup — SVG markup read into an element, nothing in it that runs kept:
// scripts and embedded frames dropped, handlers and script addresses taken off.
// Read as XML, as an SVG file is; or as HTML, as a drawing that carries HTML in
// its labels must be - a <br> there is not XML.
//
//   SvgMarkup.xml(markup) → the <svg> element; throws when it is not SVG or cannot be read
//   SvgMarkup.html(markup) → the same, the markup read as HTML
// =============================================================================

class SvgMarkup {

    static xml(markup) {
        return SvgMarkup._svg(new DOMParser().parseFromString(String(markup), "image/svg+xml").documentElement);
    }

    static html(markup) {
        var body = new DOMParser().parseFromString(String(markup), "text/html").body;
        return SvgMarkup._svg(body && body.firstElementChild);
    }

    static _svg(root) {
        if (!root || root.localName !== "svg") throw new Error("its markup is not SVG");
        SvgMarkup._clean(root);
        return root;
    }

    /** An element and all under it: a parse error refused; what runs dropped; handlers and script addresses taken off. */
    static _clean(el) {
        if (el.localName === "parsererror") throw new Error("its SVG could not be read");
        Array.from(el.attributes).forEach(function (a) {
            if (/^on/i.test(a.name) || (/ref$/i.test(a.name) && /^\s*javascript:/i.test(a.value))) el.removeAttribute(a.name);
        });
        Array.from(el.children).forEach(function (child) {
            if (/^(script|iframe|object|embed)$/i.test(child.localName)) el.removeChild(child);
            else SvgMarkup._clean(child);
        });
    }
}
