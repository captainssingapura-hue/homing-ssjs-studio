package hue.captains.singapura.js.homing.conformance.workbench;

import hue.captains.singapura.js.homing.core.CssClass;
import hue.captains.singapura.js.homing.core.CssGroup;
import hue.captains.singapura.js.homing.core.Wearable;

import java.util.List;

import static hue.captains.singapura.js.homing.design.DesignClass.of;
import static hue.captains.singapura.js.homing.design.Emphasis.Muted;
import static hue.captains.singapura.js.homing.design.Feedback.Danger;
import static hue.captains.singapura.js.homing.design.Feedback.Info;
import static hue.captains.singapura.js.homing.design.Feedback.Success;
import static hue.captains.singapura.js.homing.design.Feedback.Warning;
import static hue.captains.singapura.js.homing.design.Layer.Base;
import static hue.captains.singapura.js.homing.design.Structure.Hairline;
import static hue.captains.singapura.js.homing.design.Target.Color;
import static hue.captains.singapura.js.homing.design.Target.Shape;
import static hue.captains.singapura.js.homing.design.Target.Type;
import static hue.captains.singapura.js.homing.design.Text.Body;
import static hue.captains.singapura.js.homing.design.Text.Code;
import static hue.captains.singapura.js.homing.design.Text.Heading;
import static hue.captains.singapura.js.homing.design.Text.Kicker;
import static hue.captains.singapura.js.homing.design.Text.Label;

/**
 * The workbench's sheet: the bodies lay out, the design's words colour, set and shape. A pane
 * and its card; what a node is (its kicker, its name, its facts); the findings, each said in the
 * feedback word its state is - an error danger, a debt warning, an allowance info, clean success;
 * a module's source; the report's sections; the graph's view.
 */
public record WorkbenchStyles() implements CssGroup<WorkbenchStyles> {

    public static final WorkbenchStyles INSTANCE = new WorkbenchStyles();

    // ── the pane ────────────────────────────────────────────────────────────

    /** A pane: the box a widget fills, scrolling on its own, in the body's face and ink. */
    public record wb_pane() implements CssClass<WorkbenchStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Body.class, Type.Face.class), of(Body.class, Color.Ink.class)); }
        @Override public String body() { return """
            height: 100%;
            overflow: auto;
            padding: 16px 20px;
            box-sizing: border-box;
            """;
        }
    }

    /** What a pane says of one thing: a column with room between, the width that reads well. */
    public record wb_card() implements CssClass<WorkbenchStyles> {
        @Override public String body() { return """
            display: flex;
            flex-direction: column;
            align-items: flex-start;
            gap: 8px;
            max-width: 80ch;
            """;
        }
    }

    /** What a node is, above its name: crate, package, its form. */
    public record wb_kicker() implements CssClass<WorkbenchStyles> {
        @Override public List<? extends Wearable> wears() {
            return List.of(of(Kicker.class, Type.Face.class), of(Kicker.class, Type.Scale.class), of(Kicker.class, Type.Weight.class),
                           of(Kicker.class, Type.Treatment.class), of(Kicker.class, Color.Ink.class));
        }
        @Override public String body() { return "margin: 0;\n"; }
    }

    /** A node's name. */
    public record wb_title() implements CssClass<WorkbenchStyles> {
        @Override public List<? extends Wearable> wears() {
            return List.of(of(Heading.class, Type.Face.class), of(Heading.class, Type.Scale.class), of(Heading.class, Type.Weight.class), of(Heading.class, Color.Ink.class));
        }
        @Override public String body() { return "margin: 0;\nword-break: break-word;\n"; }
    }

    /** A name as code: a module's class, quietly. */
    public record wb_code() implements CssClass<WorkbenchStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Code.class, Type.Face.class), of(Label.class, Type.Scale.class), of(Muted.class, Color.Ink.class)); }
        @Override public String body() { return "margin: 0;\nword-break: break-all;\n"; }
    }

    /** Nothing picked yet, or nothing to say - said quietly. */
    public record wb_hint() implements CssClass<WorkbenchStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Muted.class, Color.Ink.class)); }
        @Override public String body() { return "margin: 0;\n"; }
    }

    /** What could not be read, said where it would have been. */
    public record wb_failed() implements CssClass<WorkbenchStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Danger.class, Color.Ink.class)); }
        @Override public String body() { return "margin: 0;\n"; }
    }

    /** A node's facts: a key, then its value, a row each. */
    public record wb_facts() implements CssClass<WorkbenchStyles> {
        @Override public String body() { return """
            display: grid;
            grid-template-columns: max-content 1fr;
            gap: 4px 16px;
            margin: 4px 0 0;
            """;
        }
    }

    /** A fact's key. */
    public record wb_key() implements CssClass<WorkbenchStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Label.class, Type.Scale.class), of(Label.class, Type.Weight.class), of(Muted.class, Color.Ink.class)); }
        @Override public String body() { return "margin: 0;\n"; }
    }

    /** A fact's value. */
    public record wb_value() implements CssClass<WorkbenchStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Code.class, Type.Face.class), of(Label.class, Type.Scale.class)); }
        @Override public String body() { return "margin: 0;\nword-break: break-all;\n"; }
    }

    // ── conformance ─────────────────────────────────────────────────────────

    /** A verdict, at the head of a pane: the crate's or the module's, the whole report's. */
    public record wb_verdict() implements CssClass<WorkbenchStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Heading.class, Type.Face.class), of(Heading.class, Type.Weight.class)); }
        @Override public String body() { return "margin: 0;\nword-break: break-word;\n"; }
    }

    /** A section's heading: Orphan modules, Findings. */
    public record wb_section() implements CssClass<WorkbenchStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Label.class, Type.Scale.class), of(Label.class, Type.Weight.class), of(Muted.class, Color.Ink.class)); }
        @Override public String body() { return "margin: 12px 0 2px;\n"; }
    }

    /** One finding, or one module of the report: a line of code. */
    public record wb_line() implements CssClass<WorkbenchStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Code.class, Type.Face.class), of(Label.class, Type.Scale.class)); }
        @Override public String body() { return "margin: 1px 0;\nline-height: 1.45;\nword-break: break-word;\n"; }
    }

    /** A finding under its module, set in. */
    public record wb_finding() implements CssClass<WorkbenchStyles> {
        @Override public String body() { return "margin-left: 18px;\n"; }
    }

    /** An error: what fails the gate. */
    public record wb_danger() implements CssClass<WorkbenchStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Danger.class, Color.Ink.class)); }
        @Override public String body() { return ""; }
    }

    /** A debt: what the baseline lets pass, for now. */
    public record wb_warning() implements CssClass<WorkbenchStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Warning.class, Color.Ink.class)); }
        @Override public String body() { return ""; }
    }

    /** An allowance: what is let pass on purpose. */
    public record wb_info() implements CssClass<WorkbenchStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Info.class, Color.Ink.class)); }
        @Override public String body() { return ""; }
    }

    /** Clean. */
    public record wb_success() implements CssClass<WorkbenchStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Success.class, Color.Ink.class)); }
        @Override public String body() { return ""; }
    }

    /** A module type's section of the report: a rule at its side, the modules of that type in it. */
    public record wb_type() implements CssClass<WorkbenchStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Hairline.class, Color.Edge.class)); }
        @Override public String body() { return """
            margin-top: 14px;
            padding-left: 12px;
            border-left-width: 3px;
            border-left-style: solid;
            """;
        }
    }

    /** The rules a type's modules are held to, folded until asked for. */
    public record wb_rules() implements CssClass<WorkbenchStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Label.class, Type.Scale.class), of(Muted.class, Color.Ink.class)); }
        @Override public String body() { return "margin: 2px 0 4px;\n"; }
    }

    /** A rule: its id, then what it is for. */
    public record wb_rule() implements CssClass<WorkbenchStyles> {
        @Override public String body() { return "margin: 1px 0 1px 16px;\nline-height: 1.5;\n"; }
    }

    // ── a module's source ───────────────────────────────────────────────────

    /** The module as it is served: verbatim, on the plate code is set on. */
    public record wb_source() implements CssClass<WorkbenchStyles> {
        @Override public List<? extends Wearable> wears() {
            return List.of(of(Code.class, Type.Face.class), of(Code.class, Color.Ink.class), of(Base.class, Color.Surface.class), of(Hairline.class, Color.Edge.class),
                    of(Code.class, Shape.Corner.class));
        }
        @Override public String body() { return """
            margin: 4px 0 0;
            padding: 10px 12px;
            border-width: 1px;
            border-style: solid;
            overflow-x: auto;
            white-space: pre;
            line-height: 1.5;
            align-self: stretch;
            """;
        }
    }

    // ── the graph ───────────────────────────────────────────────────────────

    /** The graph's pane: its zoom bar, then the view, which takes the rest. */
    public record wb_graph() implements CssClass<WorkbenchStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Body.class, Type.Face.class), of(Body.class, Color.Ink.class)); }
        @Override public String body() { return """
            height: 100%;
            display: flex;
            flex-direction: column;
            gap: 8px;
            padding: 12px;
            box-sizing: border-box;
            """;
        }
    }

    /** The graph's zoom bar. */
    public record wb_graph_bar() implements CssClass<WorkbenchStyles> {
        @Override public String body() { return "display: flex;\nflex: 0 0 auto;\njustify-content: flex-end;\n"; }
    }

    /** The graph's view: the rest of the pane. */
    public record wb_graph_view() implements CssClass<WorkbenchStyles> {
        @Override public String body() { return "position: relative;\nflex: 1 1 auto;\nmin-height: 0;\ndisplay: flex;\n"; }
    }

    /** What is hidden until it is filled. */
    public record wb_hidden() implements CssClass<WorkbenchStyles> {
        @Override public String body() { return "display: none;\n"; }
    }

    @Override
    public List<CssClass<WorkbenchStyles>> cssClasses() {
        return List.of(new wb_pane(), new wb_card(), new wb_kicker(), new wb_title(), new wb_code(), new wb_hint(), new wb_failed(),
                new wb_facts(), new wb_key(), new wb_value(), new wb_verdict(), new wb_section(), new wb_line(), new wb_finding(),
                new wb_danger(), new wb_warning(), new wb_info(), new wb_success(), new wb_type(), new wb_rules(), new wb_rule(),
                new wb_source(), new wb_graph(), new wb_graph_bar(), new wb_graph_view(), new wb_hidden());
    }
}
