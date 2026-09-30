package hue.captains.singapura.js.homing.catalogue.demo.recipes;

import hue.captains.singapura.js.homing.core.CssClass;
import hue.captains.singapura.js.homing.core.CssGroup;
import hue.captains.singapura.js.homing.core.Wearable;

import java.util.List;

import static hue.captains.singapura.js.homing.design.DesignClass.of;
import static hue.captains.singapura.js.homing.design.Emphasis.Muted;
import static hue.captains.singapura.js.homing.design.Layer.Raised;
import static hue.captains.singapura.js.homing.design.Target.Color;
import static hue.captains.singapura.js.homing.design.Target.Shape;
import static hue.captains.singapura.js.homing.design.Target.Type;
import static hue.captains.singapura.js.homing.design.Text.Body;
import static hue.captains.singapura.js.homing.design.Text.Caption;
import static hue.captains.singapura.js.homing.design.Text.Heading;
import static hue.captains.singapura.js.homing.design.Text.Label;

/** A recipe card's sheet, in the design's words: a raised card, a heading, what goes in and how. */
public record RecipeStyles() implements CssGroup<RecipeStyles> {

    public static final RecipeStyles INSTANCE = new RecipeStyles();

    /** The card: raised, one column. */
    public record rc_root() implements CssClass<RecipeStyles> {
        @Override public List<? extends Wearable> wears() {
            return List.of(of(Body.class, Type.Face.class), of(Body.class, Color.Ink.class), of(Raised.class, Color.Surface.class), of(Raised.class, Shape.Corner.class));
        }
        @Override public String body() { return """
            display: flex;
            flex-direction: column;
            gap: 8px;
            max-width: 38rem;
            margin: 24px 32px;
            padding: 20px 24px;
            box-sizing: border-box;
            """;
        }
    }

    public record rc_title() implements CssClass<RecipeStyles> {
        @Override public List<? extends Wearable> wears() {
            return List.of(of(Heading.class, Type.Face.class), of(Heading.class, Type.Scale.class), of(Heading.class, Type.Weight.class), of(Heading.class, Color.Ink.class));
        }
        @Override public String body() { return "margin: 0;\n"; }
    }

    public record rc_serves() implements CssClass<RecipeStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Caption.class, Type.Scale.class), of(Muted.class, Color.Ink.class)); }
        @Override public String body() { return "margin: 0;\n"; }
    }

    public record rc_heading() implements CssClass<RecipeStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Label.class, Type.Scale.class), of(Label.class, Type.Weight.class)); }
        @Override public String body() { return "margin: 8px 0 0;\n"; }
    }

    public record rc_list() implements CssClass<RecipeStyles> {
        @Override public String body() { return """
            margin: 0;
            padding-left: 1.4em;
            line-height: 1.6;
            """;
        }
    }

    @Override
    public List<CssClass<RecipeStyles>> cssClasses() { return List.of(new rc_root(), new rc_title(), new rc_serves(), new rc_heading(), new rc_list()); }
}
