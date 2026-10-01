// =============================================================================
// DocImage — a doc's image part, a figure: SVG drawn inline, so it takes the theme's
// colours - read by SvgMarkup, nothing in it that runs kept - in a view that
// zooms and pans in place, its bar above it; or a raster by its address. Its alt
// text its accessible name, its caption under it. A ContentWidget of the type
// image.
//
//   new DocImage(container, params)   params: { doc, key }
//   w.zoom → the SvgPanZoom of an SVG image, once shown
// =============================================================================

class DocImage extends ContentWidget {
    constructor(container, params) {
        super(container, params, IMAGE, "image");
        this.zoom = null;
    }

    _draw(img, branch, into) {
        var figure = branch.createElement("figure", "figure");
        css.addClass(figure, dw_figure);
        if (img.svg) {
            var svg = SvgMarkup.xml(img.svg);
            css.addClass(svg, dw_svg);
            svg.setAttribute("role", "img");
            svg.setAttribute("aria-label", img.alt);
            this.zoom = new SvgPanZoom(branch.createBranch("zoom"), { svg: svg, label: img.alt || "An image" });
            css.addClass(this.zoom.root, dw_drawing);
            var bar = new PanZoomBar(branch.createBranch("zoomBar"), this.zoom);
            css.addClass(bar.root, dw_figure_bar);
            figure.appendChild(bar.root);
            figure.appendChild(this.zoom.root);
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
