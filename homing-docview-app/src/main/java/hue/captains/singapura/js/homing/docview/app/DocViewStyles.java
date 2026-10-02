package hue.captains.singapura.js.homing.docview.app;

import hue.captains.singapura.js.homing.core.CssClass;
import hue.captains.singapura.js.homing.core.CssGroup;
import hue.captains.singapura.js.homing.core.Wearable;

import java.util.List;

import static hue.captains.singapura.js.homing.design.DesignClass.of;
import static hue.captains.singapura.js.homing.design.Emphasis.Muted;
import static hue.captains.singapura.js.homing.design.Target.Color;
import static hue.captains.singapura.js.homing.design.Target.Type;
import static hue.captains.singapura.js.homing.design.Text.Body;

/**
 * DocView's sheet: its page - the whole of what the chrome leaves it, for the split grid of the
 * contents and the doc - a cell's box, and what it says while it reads the doc, or why it cannot.
 */
public record DocViewStyles() implements CssGroup<DocViewStyles> {

    public static final DocViewStyles INSTANCE = new DocViewStyles();

    /**
     * The MPA's slot, as DocView lays it out: the whole of what the page leaves it. Unlayered, so it
     * outranks the column the slot wears in the MPA's layout layer; it does not scroll - the doc does.
     */
    public record dv_page() implements CssClass<DocViewStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Body.class, Type.Face.class), of(Body.class, Color.Ink.class)); }
        @Override public String body() { return """
            position: relative;
            flex: 1 1 auto;
            display: flex;
            flex-direction: column;
            gap: 8px;
            width: auto;
            max-width: none;
            min-height: 0;
            margin: 0;
            padding: 8px 16px 12px;
            box-sizing: border-box;
            overflow: hidden;
            """;
        }
    }

    /** The split grid's host: the whole slot, a flex box the grid fills. */
    public record dv_shell() implements CssClass<DocViewStyles> {
        @Override public String body() { return "flex: 1 1 auto;\nmin-height: 0;\ndisplay: flex;\n"; }
    }

    /** A cell's box, lent to what fills it: positioned, the whole of the cell. */
    public record dv_cell() implements CssClass<DocViewStyles> {
        @Override public String body() { return "position: relative;\nflex: 1 1 auto;\nmin-height: 0;\noverflow: hidden;\n"; }
    }

    /** What it says while it reads the doc, or why it cannot. */
    public record dv_status() implements CssClass<DocViewStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Muted.class, Color.Ink.class)); }
        @Override public String body() { return "margin: 0;\n"; }
    }

    /** What is said away. */
    public record dv_hidden() implements CssClass<DocViewStyles> {
        @Override public String body() { return "display: none;\n"; }
    }

    @Override
    public List<CssClass<DocViewStyles>> cssClasses() { return List.of(new dv_page(), new dv_shell(), new dv_cell(), new dv_status(), new dv_hidden()); }
}
