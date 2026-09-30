package hue.captains.singapura.js.homing.docview.site;

import hue.captains.singapura.js.homing.core.CssClass;
import hue.captains.singapura.js.homing.core.CssGroup;
import hue.captains.singapura.js.homing.core.Wearable;

import java.util.List;

import static hue.captains.singapura.js.homing.design.DesignClass.of;
import static hue.captains.singapura.js.homing.design.Emphasis.Muted;
import static hue.captains.singapura.js.homing.design.Layer.Base;
import static hue.captains.singapura.js.homing.design.Layer.Raised;
import static hue.captains.singapura.js.homing.design.Structure.Hairline;
import static hue.captains.singapura.js.homing.design.Target.Color;
import static hue.captains.singapura.js.homing.design.Target.Shape;
import static hue.captains.singapura.js.homing.design.Target.Type;
import static hue.captains.singapura.js.homing.design.Text.Body;
import static hue.captains.singapura.js.homing.design.Text.Heading;
import static hue.captains.singapura.js.homing.design.Text.Label;

/**
 * The inspector's sheet, in the design's words: its page, a column; its title and the address
 * under it; its sections - the tree, the arrangement, the payload - each raised; a node of the
 * tree, its parts as chips; the arrangement's table; the payload as it came.
 */
public record InspectorStyles() implements CssGroup<InspectorStyles> {

    public static final InspectorStyles INSTANCE = new InspectorStyles();

    /** The MPA's slot, as the inspector lays it out: a column that scrolls. */
    public record di_page() implements CssClass<InspectorStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Body.class, Type.Face.class), of(Body.class, Color.Ink.class)); }
        @Override public String body() { return """
            flex: 1 1 auto;
            display: flex;
            flex-direction: column;
            gap: 12px;
            width: auto;
            max-width: none;
            min-height: 0;
            margin: 0;
            padding: 12px 24px 24px;
            box-sizing: border-box;
            overflow: auto;
            """;
        }
    }

    /** The inspector's column: the title, the address, what is said, the sections. */
    public record di_column() implements CssClass<InspectorStyles> {
        @Override public String body() { return """
            display: flex;
            flex-direction: column;
            gap: 12px;
            margin: 0;
            min-width: 0;
            """;
        }
    }

    /** The doc's title. */
    public record di_title() implements CssClass<InspectorStyles> {
        @Override public List<? extends Wearable> wears() {
            return List.of(of(Heading.class, Type.Face.class), of(Heading.class, Type.Scale.class), of(Heading.class, Type.Weight.class), of(Heading.class, Color.Ink.class));
        }
        @Override public String body() { return "margin: 0;\n"; }
    }

    /** What is said quietly: the address, the counts, a node's name. */
    public record di_muted() implements CssClass<InspectorStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Muted.class, Color.Ink.class)); }
        @Override public String body() { return "margin: 0;\n"; }
    }

    /** Code, and what is named as code: a path, a key. */
    public record di_code() implements CssClass<InspectorStyles> {
        @Override public String body() { return "font-family: monospace;\n"; }
    }

    /** The sections, side by side while there is room. */
    public record di_sections() implements CssClass<InspectorStyles> {
        @Override public String body() { return """
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(22rem, 1fr));
            gap: 16px;
            align-items: start;
            """;
        }
    }

    /** A section: raised, its heading, then what it shows. */
    public record di_section() implements CssClass<InspectorStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Raised.class, Color.Surface.class), of(Raised.class, Shape.Corner.class)); }
        @Override public String body() { return """
            display: flex;
            flex-direction: column;
            gap: 8px;
            margin: 0;
            padding: 12px 16px 16px;
            min-width: 0;
            """;
        }
    }

    /** A section's heading. */
    public record di_heading() implements CssClass<InspectorStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Label.class, Type.Scale.class), of(Label.class, Type.Weight.class)); }
        @Override public String body() { return "margin: 0;\n"; }
    }

    /** The tree: a list of nodes, each nested under its parent. */
    public record di_tree() implements CssClass<InspectorStyles> {
        @Override public String body() { return """
            list-style: none;
            margin: 0;
            padding: 0 0 0 14px;
            display: flex;
            flex-direction: column;
            gap: 6px;
            """;
        }
    }

    /** A node: its label, its name, its parts. */
    public record di_node() implements CssClass<InspectorStyles> {
        @Override public String body() { return """
            display: flex;
            flex-direction: column;
            gap: 2px;
            """;
        }
    }

    /** A node's parts, in a row. */
    public record di_chips() implements CssClass<InspectorStyles> {
        @Override public String body() { return """
            display: flex;
            flex-wrap: wrap;
            gap: 4px;
            """;
        }
    }

    /** A part: its type and its key, on the base, edged. */
    public record di_chip() implements CssClass<InspectorStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Base.class, Color.Surface.class), of(Hairline.class, Color.Edge.class), of(Label.class, Type.Scale.class)); }
        @Override public String body() { return """
            padding: 1px 6px;
            border-width: 1px;
            border-style: solid;
            font-family: monospace;
            white-space: nowrap;
            """;
        }
    }

    /** The arrangement's table. */
    public record di_table() implements CssClass<InspectorStyles> {
        @Override public String body() { return """
            border-collapse: collapse;
            width: 100%;
            """;
        }
    }

    /** A cell of it: its line under it. */
    public record di_cell() implements CssClass<InspectorStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Hairline.class, Color.Edge.class), of(Label.class, Type.Scale.class)); }
        @Override public String body() { return """
            padding: 2px 8px 2px 0;
            border-bottom-width: 1px;
            border-bottom-style: solid;
            text-align: left;
            font-family: monospace;
            """;
        }
    }

    /** The payload, as it came: a box of its own height, scrolling. */
    public record di_pre() implements CssClass<InspectorStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Base.class, Color.Surface.class), of(Hairline.class, Color.Edge.class), of(Label.class, Type.Scale.class)); }
        @Override public String body() { return """
            margin: 0;
            padding: 8px 10px;
            max-height: 40rem;
            overflow: auto;
            border-width: 1px;
            border-style: solid;
            font-family: monospace;
            white-space: pre-wrap;
            overflow-wrap: anywhere;
            """;
        }
    }

    /** What is said when the doc could not be read. */
    public record di_failed() implements CssClass<InspectorStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Muted.class, Color.Ink.class)); }
        @Override public String body() { return "margin: 0;\nfont-style: italic;\n"; }
    }

    @Override
    public List<CssClass<InspectorStyles>> cssClasses() {
        return List.of(new di_page(), new di_column(), new di_title(), new di_muted(), new di_code(), new di_sections(), new di_section(), new di_heading(),
                new di_tree(), new di_node(), new di_chips(), new di_chip(), new di_table(), new di_cell(), new di_pre(), new di_failed());
    }
}
