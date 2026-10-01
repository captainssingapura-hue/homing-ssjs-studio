package hue.captains.singapura.js.homing.catalogue.widgets;

import hue.captains.singapura.js.homing.core.CssClass;
import hue.captains.singapura.js.homing.core.CssGroup;
import hue.captains.singapura.js.homing.core.Wearable;

import java.util.List;

import static hue.captains.singapura.js.homing.design.Box.Control;
import static hue.captains.singapura.js.homing.design.DesignClass.of;
import static hue.captains.singapura.js.homing.design.Emphasis.Muted;
import static hue.captains.singapura.js.homing.design.Feedback.Danger;
import static hue.captains.singapura.js.homing.design.Interaction.Interactive;
import static hue.captains.singapura.js.homing.design.Layer.Raised;
import static hue.captains.singapura.js.homing.design.Structure.Hairline;
import static hue.captains.singapura.js.homing.design.Target.Affordance;
import static hue.captains.singapura.js.homing.design.Target.Color;
import static hue.captains.singapura.js.homing.design.Target.Motion;
import static hue.captains.singapura.js.homing.design.Target.Shape;
import static hue.captains.singapura.js.homing.design.Target.Type;
import static hue.captains.singapura.js.homing.design.Text.Body;
import static hue.captains.singapura.js.homing.design.Text.Caption;
import static hue.captains.singapura.js.homing.design.Text.Heading;
import static hue.captains.singapura.js.homing.design.Text.Kicker;
import static hue.captains.singapura.js.homing.design.Text.Label;
import static hue.captains.singapura.js.homing.design.Text.Lede;
import static hue.captains.singapura.js.homing.design.Text.Link;

/**
 * The catalogue widgets' sheet, in the design's words and nothing of their own:
 * the bodies lay out, the designs colour, set and shape. The browser's one sheet,
 * its two panes in a golden split; a tree row's name and badge; the details,
 * flat on the sheet; and the cards - a catalogue's head, then its entries as
 * raised tiles, each a press away.
 */
public record CatalogueStyles() implements CssGroup<CatalogueStyles> {

    public static final CatalogueStyles INSTANCE = new CatalogueStyles();

    // ── the browser ─────────────────────────────────────────────────────────

    /** The browser: room around the one sheet it lays its two panes on, the sheet in its middle. */
    public record cw_browser() implements CssClass<CatalogueStyles> {
        @Override public String body() { return """
            display: flex;
            flex-direction: column;
            align-items: center;
            box-sizing: border-box;
            padding: 16px;
            min-height: 0;
            """;
        }
    }

    /**
     * Where the sheet lies: the page's middle part - its golden share, 61.8% - never narrower than
     * 880px, all of a narrower page, and no wider than 1440px however wide the page is. The sheet
     * fills it, as a panel fills what holds it.
     */
    public record cw_sheet() implements CssClass<CatalogueStyles> {
        @Override public String body() { return """
            display: flex;
            flex-direction: column;
            flex: 1 1 auto;
            min-height: 0;
            box-sizing: border-box;
            width: min(100%, max(880px, 61.8%));
            max-width: 1440px;
            """;
        }
    }

    /**
     * The sheet's two panes, side by side in a golden split: the tree the lesser part, the details
     * the greater - 1 to 1.618 - the tree never narrower than its names can bear.
     */
    public record cw_split() implements CssClass<CatalogueStyles> {
        @Override public String body() { return """
            display: grid;
            grid-template-columns: minmax(180px, 1fr) minmax(0, 1.618fr);
            flex: 1 1 auto;
            min-height: 0;
            """;
        }
    }

    /** A pane the browser lends a subordinate: a box it fills, scrolling on its own. */
    public record cw_pane() implements CssClass<CatalogueStyles> {
        @Override public String body() { return """
            position: relative;
            box-sizing: border-box;
            min-width: 0;
            min-height: 0;
            overflow: auto;
            """;
        }
    }

    /** The tree's pane: the lesser part, a hairline at its end - the one line on the sheet. */
    public record cw_pane_tree() implements CssClass<CatalogueStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Hairline.class, Color.Edge.class)); }
        @Override public String body() { return "padding: 12px 8px;\nborder-inline-end-width: 1px;\nborder-inline-end-style: solid;\n"; }
    }

    /** The details' pane: the greater part, the entry set in it with room to read. */
    public record cw_pane_details() implements CssClass<CatalogueStyles> {
        @Override public String body() { return "padding: 28px 36px;\n"; }
    }

    // ── a tree row ──────────────────────────────────────────────────────────

    /** A tree row: the entry's icon and name, then its badge. */
    public record cw_row() implements CssClass<CatalogueStyles> {
        @Override public String body() { return """
            display: inline-flex;
            align-items: baseline;
            gap: 8px;
            min-width: 0;
            """;
        }
    }

    public record cw_row_name() implements CssClass<CatalogueStyles> {
        @Override public String body() { return """
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
            """;
        }
    }

    public record cw_row_badge() implements CssClass<CatalogueStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Kicker.class, Type.Scale.class), of(Kicker.class, Type.Treatment.class), of(Muted.class, Color.Ink.class)); }
        @Override public String body() { return "flex: 0 0 auto;\n"; }
    }

    // ── the details ─────────────────────────────────────────────────────────

    /** The details: the picked entry's card, and the hint until one is picked. */
    public record cw_details() implements CssClass<CatalogueStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Body.class, Type.Face.class), of(Body.class, Color.Ink.class)); }
        @Override public String body() { return """
            overflow: auto;
            padding: 4px;
            box-sizing: border-box;
            """;
        }
    }

    /** The entry picked, flat on the sheet: its kicker, its name, its summary, the way to it - a column with room between. */
    public record cw_entry() implements CssClass<CatalogueStyles> {
        @Override public String body() { return """
            display: flex;
            flex-direction: column;
            align-items: flex-start;
            gap: 12px;
            max-width: 64ch;
            """;
        }
    }

    /** The way to the entry - Browse, Open: a link, set as a label is. */
    public record cw_way() implements CssClass<CatalogueStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Link.class, Color.Ink.class), of(Link.class, Type.Decoration.class), of(Label.class, Type.Weight.class)); }
        @Override public String body() { return "margin-top: 8px;\n"; }
    }

    /** Pick an entry to see it here - said quietly. */
    public record cw_hint() implements CssClass<CatalogueStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Muted.class, Color.Ink.class)); }
        @Override public String body() { return "margin: 0;\n"; }
    }

    /** What could not be read, said where it would have been. */
    public record cw_failed() implements CssClass<CatalogueStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Danger.class, Color.Ink.class)); }
        @Override public String body() { return "margin: 0;\n"; }
    }

    /** What is hidden until it is filled. */
    public record cw_hidden() implements CssClass<CatalogueStyles> {
        @Override public String body() { return "display: none;\n"; }
    }

    // ── the cards ───────────────────────────────────────────────────────────

    /** The cards: a catalogue's head, then its sections, one column of the width a listing reads well at. */
    public record cw_cards() implements CssClass<CatalogueStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Body.class, Type.Face.class), of(Body.class, Color.Ink.class)); }
        @Override public String body() { return """
            overflow: auto;
            display: flex;
            flex-direction: column;
            gap: 20px;
            padding: 24px 32px;
            box-sizing: border-box;
            """;
        }
    }

    /** A catalogue's badge, above its name. */
    public record cw_kicker() implements CssClass<CatalogueStyles> {
        @Override public List<? extends Wearable> wears() {
            return List.of(of(Kicker.class, Type.Face.class), of(Kicker.class, Type.Scale.class), of(Kicker.class, Type.Weight.class),
                           of(Kicker.class, Type.Treatment.class), of(Kicker.class, Color.Ink.class));
        }
        @Override public String body() { return "margin: 0;\n"; }
    }

    /** A catalogue's name. */
    public record cw_title() implements CssClass<CatalogueStyles> {
        @Override public List<? extends Wearable> wears() {
            return List.of(of(Heading.class, Type.Face.class), of(Heading.class, Type.Scale.class), of(Heading.class, Type.Weight.class), of(Heading.class, Color.Ink.class));
        }
        @Override public String body() { return "margin: 0;\n"; }
    }

    /** A catalogue's summary, under its name. */
    public record cw_summary() implements CssClass<CatalogueStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Lede.class, Color.Ink.class)); }
        @Override public String body() { return "margin: 0;\n"; }
    }

    /** A section's heading: Catalogues, Pages. */
    public record cw_section_title() implements CssClass<CatalogueStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Label.class, Type.Scale.class), of(Label.class, Type.Weight.class), of(Muted.class, Color.Ink.class)); }
        @Override public String body() { return "margin: 0 0 8px;\n"; }
    }

    /** A section's entries: as many tiles to a row as the width holds. */
    public record cw_grid() implements CssClass<CatalogueStyles> {
        @Override public String body() { return """
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
            gap: 12px;
            """;
        }
    }

    /** An entry: a link, as a raised tile - a control, so it wears the ring the design draws on {@code :focus-visible}. */
    public record cw_tile() implements CssClass<CatalogueStyles> {
        @Override public List<? extends Wearable> wears() {
            return List.of(of(Raised.class, Color.Surface.class), of(Raised.class, Shape.Corner.class),
                           of(Control.class, Color.Edge.class), of(Control.class, Shape.Rule.class),
                           of(Interactive.class, Affordance.Cursor.class), of(Interactive.class, Motion.Ease.class),
                           of(Body.class, Color.Ink.class));
        }
        @Override public String body() { return """
            display: flex;
            flex-direction: column;
            gap: 4px;
            padding: 12px 14px;
            text-decoration: none;
            min-width: 0;
            """;
        }
    }

    public record cw_tile_name() implements CssClass<CatalogueStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Label.class, Type.Scale.class), of(Label.class, Type.Weight.class)); }
        @Override public String body() { return "overflow-wrap: anywhere;\n"; }
    }

    public record cw_tile_badge() implements CssClass<CatalogueStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Kicker.class, Type.Scale.class), of(Kicker.class, Type.Treatment.class), of(Muted.class, Color.Ink.class)); }
        @Override public String body() { return ""; }
    }

    public record cw_tile_summary() implements CssClass<CatalogueStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Caption.class, Type.Scale.class), of(Muted.class, Color.Ink.class)); }
        @Override public String body() { return ""; }
    }

    @Override
    public List<CssClass<CatalogueStyles>> cssClasses() {
        return List.of(new cw_browser(), new cw_sheet(), new cw_split(), new cw_pane(), new cw_pane_tree(), new cw_pane_details(),
                new cw_row(), new cw_row_name(), new cw_row_badge(),
                new cw_details(), new cw_entry(), new cw_way(), new cw_hint(), new cw_failed(), new cw_hidden(),
                new cw_cards(), new cw_kicker(), new cw_title(), new cw_summary(), new cw_section_title(), new cw_grid(),
                new cw_tile(), new cw_tile_name(), new cw_tile_badge(), new cw_tile_summary());
    }
}
