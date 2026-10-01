package hue.captains.singapura.js.homing.docview.gallery;

import hue.captains.singapura.js.homing.docview.reference.ReferenceDocs;
import hue.captains.singapura.js.homing.docview.site.DocLeaves;
import hue.captains.singapura.js.homing.site.catalogue.Graft;
import hue.captains.singapura.js.homing.site.catalogue.L0_Catalogue;
import hue.captains.singapura.js.homing.site.catalogue.Leaf;
import hue.captains.singapura.js.homing.site.mpa.Mpa;
import hue.captains.singapura.js.homing.tree.NodeName;

import java.util.ArrayList;
import java.util.List;

/**
 * The gallery's root: a page for each primitive's specimens, and one for all of them; and the
 * reference docs they read from, grafted at {@value Specimens#DOCS}, each inspected.
 */
public record GalleryCatalogue() implements L0_Catalogue<GalleryCatalogue> {

    public static final GalleryCatalogue INSTANCE = new GalleryCatalogue();

    @Override public String name() { return "DocView widgets"; }
    @Override public String summary() { return "A doc's primitives, each working alone: made from its type and params, asking its content party"; }

    @Override
    public List<Graft<GalleryCatalogue>> grafts() { return List.of(Graft.of(this, Docs.INSTANCE)); }

    @Override
    public List<Leaf<GalleryCatalogue>> leaves(Mpa mpa) {
        var out = new ArrayList<Leaf<GalleryCatalogue>>();
        out.add(page(mpa, "all", "All widgets", "Every primitive's specimens, on one page"));
        out.add(page(mpa, "prose", "Prose", "Markdown text, drawn from its tokens: paragraphs, lists, quotes"));
        out.add(page(mpa, "code", "Code", "A language and its source, drawn by its renderer or as its source"));
        out.add(page(mpa, "table", "Table", "Columns and rows: alignment, spans, badges, captions"));
        out.add(page(mpa, "image", "Image", "A figure: SVG drawn inline, its caption under it"));
        return out;
    }

    private Leaf<GalleryCatalogue> page(Mpa mpa, String kind, String name, String summary) {
        return Leaf.of(this, new NodeName(kind), name, summary, mpa.page(WidgetGalleryApp.INSTANCE, new WidgetGalleryApp.Params(kind))).badge("WIDGET").icon("🧩");
    }

    /** The reference docs, each a leaf at its name, inspected: what the specimens read from. */
    public record Docs() implements L0_Catalogue<Docs> {

        public static final Docs INSTANCE = new Docs();

        @Override public String name() { return "Reference docs"; }
        @Override public NodeName slug() { return new NodeName(Specimens.DOCS.substring(1)); }
        @Override public String summary() { return "The docs the specimens are parts of, each inspected: its tree, its arrangement, its payload"; }

        @Override
        public List<Leaf<Docs>> leaves(Mpa mpa) {
            return ReferenceDocs.ALL.entrySet().stream().map(e -> DocLeaves.inspected(this, mpa, new NodeName(e.getKey()), e.getValue())).toList();
        }
    }
}
