package hue.captains.singapura.js.homing.docview.widgets;

import java.util.List;

/**
 * The colours a mermaid diagram is drawn in: each of the variables of mermaid's base theme, and the CSS
 * variable on the page it takes its value from. {@link MermaidLibraryModule} names them for the engine,
 * which reads each off the page and themes the diagram when it finds a ground to draw on.
 *
 * <p>None yet. The old designs' variables are gone with the old design stack, and the house's look
 * colours its components through their classes, never through variables on the page - so until the
 * drawing reads its colours off the house's look, a diagram is drawn in mermaid's own theme.</p>
 */
public final class MermaidPalette {

    private MermaidPalette() {}

    /** One of mermaid's theme variables, and the CSS variable on the page whose value it takes. */
    public record Tint(String variable, String cssVariable) {}

    public static final List<Tint> TINTS = List.of();
}
