// =============================================================================
// DocImage — a doc's image part, a figure: SVG drawn inline, so it takes the theme's
// colours - read from its markup, nothing in it that runs kept - or a raster
// by its address; its alt text its accessible name, its caption under it. A
// ContentWidget of the type image.
//
//   new DocImage(container, params)   params: { doc, key }
// =============================================================================

class DocImage extends ContentWidget {
    constructor(container, params) { super(container, params, IMAGE, "image"); }

    _draw(img, branch, into) {
        var figure = branch.createElement("figure", "figure");
        css.addClass(figure, dw_figure);
        if (img.svg) {
            var svg = DocImage._svg(img.svg);
            css.addClass(svg, dw_svg);
            svg.setAttribute("role", "img");
            svg.setAttribute("aria-label", img.alt);
            figure.appendChild(svg);
        } else if (img.src) {
            var pic = branch.createElement("picture", "img");
            pic.setAttribute("src", img.src);
            pic.setAttribute("alt", img.alt);
            figure.appendChild(pic);
        } else {
            throw new Error("a raster this view does not fetch yet");
        }
        if (img.caption) {
            var caption = branch.createElement("caption", "figcaption");
            css.addClass(caption, dw_caption);
            caption.textContent = img.caption;
            figure.appendChild(caption);
        }
        into.appendChild(figure);
    }

    /** SVG markup read into an element: refused when it is not SVG; nothing that runs kept. */
    static _svg(markup) {
        var root = new DOMParser().parseFromString(markup, "image/svg+xml").documentElement;
        if (!root || root.localName !== "svg") throw new Error("its markup is not SVG");
        DocImage._clean(root);
        return root;
    }

    /** An element and all under it: a parse error refused; scripts dropped; handlers and script addresses taken off. */
    static _clean(el) {
        if (el.localName === "parsererror") throw new Error("its SVG could not be read");
        Array.from(el.attributes).forEach(function (a) {
            if (/^on/i.test(a.name) || (/ref$/i.test(a.name) && /^\s*javascript:/i.test(a.value))) el.removeAttribute(a.name);
        });
        Array.from(el.children).forEach(function (child) {
            if (child.localName === "script") el.removeChild(child);
            else DocImage._clean(child);
        });
    }
}
