package hue.captains.singapura.js.homing.docview.widgets;

import hue.captains.singapura.js.homing.design.DesignClass;

import java.util.List;

import static hue.captains.singapura.js.homing.design.DesignClass.of;
import static hue.captains.singapura.js.homing.design.Emphasis.Muted;
import static hue.captains.singapura.js.homing.design.Emphasis.Primary;
import static hue.captains.singapura.js.homing.design.Emphasis.Secondary;
import static hue.captains.singapura.js.homing.design.Feedback.Warning;
import static hue.captains.singapura.js.homing.design.Layer.Raised;
import static hue.captains.singapura.js.homing.design.Layer.Recessed;
import static hue.captains.singapura.js.homing.design.Target.Color;
import static hue.captains.singapura.js.homing.design.Target.Type;
import static hue.captains.singapura.js.homing.design.Text.Body;

/**
 * The design's colours a mermaid diagram is drawn in: each of the variables of mermaid's base
 * theme, and the design pair - and the property of it - whose value it takes. So a diagram is in
 * the theme the page is: drawn on the plate's ground, its nodes raised and edged in the accent,
 * its text the body's ink, its lines a muted one, its clusters recessed, its notes a warning's.
 * One table, read twice: {@link MermaidLibraryModule} names each pair's variable for the engine,
 * and the drawing reads every pair, so each design binds them wherever a drawing is - and the gate
 * says so, design by design.
 */
public final class MermaidPalette {

    private MermaidPalette() {}

    /** One of mermaid's theme variables, and the pair whose value - of this property - it takes. */
    public record Tint(String variable, DesignClass<?> pair, String property) {

        /** The design's variable for the pair's property: {@code --raised-color-surface-background-color}. */
        public String cssVariable() {
            String ref = pair.var(property);
            return ref.substring("var(".length(), ref.length() - 1);
        }
    }

    public static final List<Tint> TINTS = List.of(
            new Tint("background", DocWidgetStyles.PLATE_GROUND, "background-color"),
            new Tint("primaryColor", of(Raised.class, Color.Surface.class), "background-color"),
            new Tint("primaryTextColor", of(Body.class, Color.Ink.class), "color"),
            new Tint("primaryBorderColor", of(Primary.class, Color.Surface.class), "background-color"),
            new Tint("secondaryColor", of(Secondary.class, Color.Surface.class), "background-color"),
            new Tint("tertiaryColor", of(Recessed.class, Color.Surface.class), "background-color"),
            new Tint("lineColor", of(Muted.class, Color.Ink.class), "color"),
            new Tint("textColor", of(Body.class, Color.Ink.class), "color"),
            new Tint("noteBkgColor", of(Warning.class, Color.Surface.class), "background-color"),
            new Tint("noteTextColor", of(Warning.class, Color.Ink.class), "color"),
            new Tint("fontFamily", of(Body.class, Type.Face.class), "font-family"));

    /** The pairs, each once: what a drawing reads. */
    public static List<DesignClass<?>> pairs() { return TINTS.stream().<DesignClass<?>>map(Tint::pair).distinct().toList(); }
}
