package hue.captains.singapura.js.homing.catalogue.site;

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

/**
 * The catalogue listing's sheet, in the design's words and nothing of its own:
 * the bodies lay out, the designs colour, set and shape. A catalogue's head -
 * its badge as a kicker, its name as a heading, its summary as a lede - then
 * what is under it as tiles: raised, a press away, each its name, its badge
 * and its summary.
 */
public record ListingStyles() implements CssGroup<ListingStyles> {

    public static final ListingStyles INSTANCE = new ListingStyles();

    /** The listing: its head, then its sections, one column of the width a listing reads well at. */
    public record cl_root() implements CssClass<ListingStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Body.class, Type.Face.class), of(Body.class, Color.Ink.class)); }
        @Override public String body() { return """
            display: flex;
            flex-direction: column;
            gap: 20px;
            max-width: 64rem;
            padding: 24px 32px;
            box-sizing: border-box;
            """;
        }
    }

    /** A catalogue's badge, above its name. */
    public record cl_kicker() implements CssClass<ListingStyles> {
        @Override public List<? extends Wearable> wears() {
            return List.of(of(Kicker.class, Type.Face.class), of(Kicker.class, Type.Scale.class), of(Kicker.class, Type.Weight.class),
                           of(Kicker.class, Type.Treatment.class), of(Kicker.class, Color.Ink.class));
        }
        @Override public String body() { return "margin: 0;\n"; }
    }

    /** A catalogue's name. */
    public record cl_title() implements CssClass<ListingStyles> {
        @Override public List<? extends Wearable> wears() {
            return List.of(of(Heading.class, Type.Face.class), of(Heading.class, Type.Scale.class), of(Heading.class, Type.Weight.class), of(Heading.class, Color.Ink.class));
        }
        @Override public String body() { return "margin: 0;\n"; }
    }

    /** A catalogue's summary, under its name. */
    public record cl_summary() implements CssClass<ListingStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Lede.class, Color.Ink.class)); }
        @Override public String body() { return "margin: 0;\n"; }
    }

    /** A section's heading: Catalogues, Pages. */
    public record cl_section_title() implements CssClass<ListingStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Label.class, Type.Scale.class), of(Label.class, Type.Weight.class), of(Muted.class, Color.Ink.class)); }
        @Override public String body() { return "margin: 0 0 8px;\n"; }
    }

    /** A section's entries: as many tiles to a row as the width holds. */
    public record cl_grid() implements CssClass<ListingStyles> {
        @Override public String body() { return """
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
            gap: 12px;
            """;
        }
    }

    /**
     * An entry: a link, as a raised tile - a control, so it wears the ring the design
     * draws on {@code :focus-visible}; a press away.
     */
    public record cl_tile() implements CssClass<ListingStyles> {
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

    /** An entry's name, with its icon. */
    public record cl_tile_name() implements CssClass<ListingStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Label.class, Type.Scale.class), of(Label.class, Type.Weight.class)); }
        @Override public String body() { return "overflow-wrap: anywhere;\n"; }
    }

    /** An entry's badge. */
    public record cl_tile_badge() implements CssClass<ListingStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Kicker.class, Type.Scale.class), of(Kicker.class, Type.Treatment.class), of(Muted.class, Color.Ink.class)); }
        @Override public String body() { return ""; }
    }

    /** An entry's summary. */
    public record cl_tile_summary() implements CssClass<ListingStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Caption.class, Type.Scale.class), of(Muted.class, Color.Ink.class)); }
        @Override public String body() { return ""; }
    }

    /** Nothing here yet, said quietly. */
    public record cl_empty() implements CssClass<ListingStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Muted.class, Color.Ink.class)); }
        @Override public String body() { return "margin: 0;\n"; }
    }

    /** A catalogue that could not be read, said where it would have been. */
    public record cl_failed() implements CssClass<ListingStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Danger.class, Color.Ink.class)); }
        @Override public String body() { return "margin: 0;\n"; }
    }

    /** What is hidden until it is filled: a section with nothing in it. */
    public record cl_hidden() implements CssClass<ListingStyles> {
        @Override public String body() { return "display: none;\n"; }
    }

    @Override
    public List<CssClass<ListingStyles>> cssClasses() {
        return List.of(new cl_root(), new cl_kicker(), new cl_title(), new cl_summary(), new cl_section_title(), new cl_grid(),
                new cl_tile(), new cl_tile_name(), new cl_tile_badge(), new cl_tile_summary(), new cl_empty(), new cl_failed(), new cl_hidden());
    }
}
