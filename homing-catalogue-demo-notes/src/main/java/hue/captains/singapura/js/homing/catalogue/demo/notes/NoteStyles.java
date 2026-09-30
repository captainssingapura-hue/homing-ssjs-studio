package hue.captains.singapura.js.homing.catalogue.demo.notes;

import hue.captains.singapura.js.homing.core.CssClass;
import hue.captains.singapura.js.homing.core.CssGroup;
import hue.captains.singapura.js.homing.core.Wearable;

import java.util.List;

import static hue.captains.singapura.js.homing.design.DesignClass.of;
import static hue.captains.singapura.js.homing.design.Emphasis.Muted;
import static hue.captains.singapura.js.homing.design.Target.Color;
import static hue.captains.singapura.js.homing.design.Target.Type;
import static hue.captains.singapura.js.homing.design.Text.Body;
import static hue.captains.singapura.js.homing.design.Text.Caption;
import static hue.captains.singapura.js.homing.design.Text.Heading;

/** A note's sheet, in the design's words: a heading, a quiet date, reading text. */
public record NoteStyles() implements CssGroup<NoteStyles> {

    public static final NoteStyles INSTANCE = new NoteStyles();

    /** The note: one column, the width text reads well at. */
    public record nt_root() implements CssClass<NoteStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Body.class, Type.Face.class), of(Body.class, Color.Ink.class)); }
        @Override public String body() { return """
            display: flex;
            flex-direction: column;
            gap: 12px;
            max-width: 42rem;
            padding: 24px 32px;
            box-sizing: border-box;
            """;
        }
    }

    public record nt_title() implements CssClass<NoteStyles> {
        @Override public List<? extends Wearable> wears() {
            return List.of(of(Heading.class, Type.Face.class), of(Heading.class, Type.Scale.class), of(Heading.class, Type.Weight.class), of(Heading.class, Color.Ink.class));
        }
        @Override public String body() { return "margin: 0;\n"; }
    }

    public record nt_when() implements CssClass<NoteStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Caption.class, Type.Scale.class), of(Muted.class, Color.Ink.class)); }
        @Override public String body() { return "margin: 0;\n"; }
    }

    public record nt_para() implements CssClass<NoteStyles> {
        @Override public String body() { return """
            margin: 0;
            line-height: 1.6;
            """;
        }
    }

    @Override
    public List<CssClass<NoteStyles>> cssClasses() { return List.of(new nt_root(), new nt_title(), new nt_when(), new nt_para()); }
}
