// =============================================================================
// DocImage — a doc's image part, a figure: SVG drawn inline, so it takes the theme's
// colours - read by SvgMarkup, nothing in it that runs kept - or a raster
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
            var svg = SvgMarkup.xml(img.svg);
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
}
