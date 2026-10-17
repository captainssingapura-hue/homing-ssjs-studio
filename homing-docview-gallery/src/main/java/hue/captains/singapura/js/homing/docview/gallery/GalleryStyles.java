package hue.captains.singapura.js.homing.docview.gallery;

import hue.captains.singapura.js.homing.core.CssClass;
import hue.captains.singapura.js.homing.core.CssGroup;

import java.util.List;


/**
 * The gallery's sheet: its page, a reading column that scrolls; a primitive's section and its
 * title; a specimen's card, raised - its title, the params its widget is made with, what it
 * shows - and the widget under them.
 */
public record GalleryStyles() implements CssGroup<GalleryStyles> {

    public static final GalleryStyles INSTANCE = new GalleryStyles();

    /** The MPA's slot, as the gallery lays it out: a column that scrolls. */
    public record gl_page() implements CssClass<GalleryStyles> {
        @Override public String body() { return """
            flex: 1 1 auto;
            display: flex;
            flex-direction: column;
            gap: 16px;
            width: auto;
            max-width: none;
            min-height: 0;
            margin: 0;
            padding: 12px 24px 32px;
            box-sizing: border-box;
            overflow: auto;
            """;
        }
    }

    /** What the gallery is, said. */
    public record gl_intro() implements CssClass<GalleryStyles> {
        @Override public String body() { return "margin: 0;\nmax-width: 56rem;\n"; }
    }

    /** A primitive's section: its title, then its specimens' cards. */
    public record gl_kind() implements CssClass<GalleryStyles> {
        @Override public String body() { return "display: flex;\nflex-direction: column;\ngap: 12px;\nmax-width: 56rem;\n"; }
    }

    public record gl_kind_title() implements CssClass<GalleryStyles> {
        @Override public String body() { return "margin: 8px 0 0;\ntext-transform: capitalize;\n"; }
    }

    /** A specimen's card: raised. */
    public record gl_card() implements CssClass<GalleryStyles> {
        @Override public String body() { return "display: flex;\nflex-direction: column;\ngap: 6px;\nmargin: 0;\npadding: 12px 16px 16px;\nmin-width: 0;\n"; }
    }

    /** A specimen's title. */
    public record gl_title() implements CssClass<GalleryStyles> {
        @Override public String body() { return "margin: 0;\n"; }
    }

    /** The params its widget is made with. */
    public record gl_params() implements CssClass<GalleryStyles> {
        @Override public String body() { return "margin: 0;\nwhite-space: pre-wrap;\n"; }
    }

    /** What a specimen shows, said. */
    public record gl_note() implements CssClass<GalleryStyles> {
        @Override public String body() { return "margin: 0 0 6px;\n"; }
    }

    @Override
    public List<CssClass<GalleryStyles>> cssClasses() {
        return List.of(new gl_page(), new gl_intro(), new gl_kind(), new gl_kind_title(), new gl_card(), new gl_title(), new gl_params(), new gl_note());
    }
}
