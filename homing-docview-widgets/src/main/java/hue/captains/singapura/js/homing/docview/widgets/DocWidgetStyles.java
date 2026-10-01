package hue.captains.singapura.js.homing.docview.widgets;

import hue.captains.singapura.js.homing.core.CssClass;
import hue.captains.singapura.js.homing.core.CssGroup;
import hue.captains.singapura.js.homing.core.Wearable;

import java.util.List;

import static hue.captains.singapura.js.homing.design.DesignClass.of;
import static hue.captains.singapura.js.homing.design.Emphasis.Muted;
import static hue.captains.singapura.js.homing.design.Feedback.Danger;
import static hue.captains.singapura.js.homing.design.Feedback.Success;
import static hue.captains.singapura.js.homing.design.Feedback.Warning;
import static hue.captains.singapura.js.homing.design.Layer.Base;
import static hue.captains.singapura.js.homing.design.Structure.Hairline;
import static hue.captains.singapura.js.homing.design.Target.Color;
import static hue.captains.singapura.js.homing.design.Target.Shape;
import static hue.captains.singapura.js.homing.design.Target.Type;
import static hue.captains.singapura.js.homing.design.Text.Body;
import static hue.captains.singapura.js.homing.design.Text.Caption;
import static hue.captains.singapura.js.homing.design.Text.Code;
import static hue.captains.singapura.js.homing.design.Text.Heading;
import static hue.captains.singapura.js.homing.design.Text.Label;
import static hue.captains.singapura.js.homing.design.Text.Link;

/**
 * The sheet of a doc's primitives, in the design's words and nothing of their own: a widget's
 * column and what it says while it has nothing to show; prose - paragraphs, headings, lists,
 * quotes, code spans, links, rules; a code block and its language; a table - its cells, their
 * alignment, badges and emphasis; a figure and its caption.
 */
public record DocWidgetStyles() implements CssGroup<DocWidgetStyles> {

    public static final DocWidgetStyles INSTANCE = new DocWidgetStyles();

    /** A widget's root: a column, its content as tall as it needs. */
    public record dw_widget() implements CssClass<DocWidgetStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Body.class, Type.Face.class), of(Body.class, Color.Ink.class)); }
        @Override public String body() { return "display: flex;\nflex-direction: column;\ngap: 10px;\nmin-width: 0;\n"; }
    }

    /** What a widget says while it has nothing to show: waiting, unavailable, alone. */
    public record dw_note() implements CssClass<DocWidgetStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Muted.class, Color.Ink.class)); }
        @Override public String body() { return "margin: 0;\nfont-style: italic;\n"; }
    }

    /** What is said away. */
    public record dw_hidden() implements CssClass<DocWidgetStyles> {
        @Override public String body() { return "display: none;\n"; }
    }

    /** A paragraph. */
    public record dw_para() implements CssClass<DocWidgetStyles> {
        @Override public String body() { return "margin: 0;\nline-height: 1.6;\n"; }
    }

    /** A heading inside prose - a composed doc's segment keeps its own. */
    public record dw_heading() implements CssClass<DocWidgetStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Heading.class, Type.Face.class), of(Heading.class, Type.Weight.class), of(Heading.class, Color.Ink.class)); }
        @Override public String body() { return "margin: 4px 0 0;\n"; }
    }

    /** A list, ordered or not; its items a column. */
    public record dw_list() implements CssClass<DocWidgetStyles> {
        @Override public String body() { return "margin: 0;\npadding-left: 1.5em;\ndisplay: flex;\nflex-direction: column;\ngap: 4px;\nline-height: 1.6;\n"; }
    }

    /** A quote: set in, a line down its side. */
    public record dw_quote() implements CssClass<DocWidgetStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Hairline.class, Color.Edge.class), of(Muted.class, Color.Ink.class)); }
        @Override public String body() { return "margin: 0;\npadding: 2px 0 2px 12px;\nborder-left-width: 3px;\nborder-left-style: solid;\ndisplay: flex;\nflex-direction: column;\ngap: 8px;\n"; }
    }

    /** Code inline in the text. */
    public record dw_code_span() implements CssClass<DocWidgetStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Code.class, Type.Face.class), of(Code.class, Color.Ink.class), of(Base.class, Color.Surface.class), of(Code.class, Shape.Corner.class)); }
        @Override public String body() { return "padding: 0 4px;\n"; }
    }

    /** A link. */
    public record dw_link() implements CssClass<DocWidgetStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Link.class, Color.Ink.class), of(Link.class, Type.Decoration.class)); }
        @Override public String body() { return ""; }
    }

    /** A citation, until the references say what it names. */
    public record dw_cite() implements CssClass<DocWidgetStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Link.class, Color.Ink.class)); }
        @Override public String body() { return ""; }
    }

    /** A rule across the text. */
    public record dw_rule() implements CssClass<DocWidgetStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Hairline.class, Color.Edge.class)); }
        @Override public String body() { return "margin: 4px 0;\nborder-width: 1px 0 0;\nborder-style: solid;\n"; }
    }

    /** A block of code: its source as it is, scrolling across when it must. */
    public record dw_pre() implements CssClass<DocWidgetStyles> {
        @Override public List<? extends Wearable> wears() {
            return List.of(of(Code.class, Type.Face.class), of(Code.class, Color.Ink.class), of(Base.class, Color.Surface.class), of(Hairline.class, Color.Edge.class),
                    of(Code.class, Shape.Corner.class));
        }
        @Override public String body() { return "margin: 0;\npadding: 10px 12px;\nborder-width: 1px;\nborder-style: solid;\noverflow-x: auto;\nwhite-space: pre;\nline-height: 1.5;\n"; }
    }

    /** What a code block says it is: its language. */
    public record dw_lang() implements CssClass<DocWidgetStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Code.class, Type.Face.class), of(Label.class, Type.Scale.class), of(Muted.class, Color.Ink.class)); }
        @Override public String body() { return "margin: 0;\n"; }
    }

    /** A table's box: it scrolls across when the table is wider than the column. */
    public record dw_table_box() implements CssClass<DocWidgetStyles> {
        @Override public String body() { return "overflow-x: auto;\nmin-width: 0;\n"; }
    }

    /** A table. */
    public record dw_table() implements CssClass<DocWidgetStyles> {
        @Override public String body() { return "border-collapse: collapse;\n"; }
    }

    /** A column's title. */
    public record dw_th() implements CssClass<DocWidgetStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Label.class, Type.Weight.class), of(Hairline.class, Color.Edge.class)); }
        @Override public String body() { return "padding: 4px 12px 4px 0;\nborder-width: 0 0 2px;\nborder-style: solid;\ntext-align: left;\nvertical-align: bottom;\n"; }
    }

    /** A cell. */
    public record dw_td() implements CssClass<DocWidgetStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Hairline.class, Color.Edge.class)); }
        @Override public String body() { return "padding: 4px 12px 4px 0;\nborder-width: 0 0 1px;\nborder-style: solid;\nvertical-align: top;\nline-height: 1.5;\n"; }
    }

    public record dw_left() implements CssClass<DocWidgetStyles> { @Override public String body() { return "text-align: left;\n"; } }
    public record dw_center() implements CssClass<DocWidgetStyles> { @Override public String body() { return "text-align: center;\n"; } }
    public record dw_right() implements CssClass<DocWidgetStyles> { @Override public String body() { return "text-align: right;\n"; } }

    /** A cell's text, set strong. */
    public record dw_strong() implements CssClass<DocWidgetStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Label.class, Type.Weight.class)); }
        @Override public String body() { return ""; }
    }

    /** A cell's text, set back. */
    public record dw_dim() implements CssClass<DocWidgetStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Muted.class, Color.Ink.class)); }
        @Override public String body() { return ""; }
    }

    /** A badge: a cell's text on its status's colour. */
    public record dw_badge() implements CssClass<DocWidgetStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Label.class, Type.Scale.class)); }
        @Override public String body() { return "display: inline-block;\npadding: 0 6px;\nborder-radius: 999px;\n"; }
    }

    public record dw_success() implements CssClass<DocWidgetStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Success.class, Color.Surface.class), of(Success.class, Color.Ink.class)); }
        @Override public String body() { return ""; }
    }

    public record dw_warning() implements CssClass<DocWidgetStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Warning.class, Color.Surface.class), of(Warning.class, Color.Ink.class)); }
        @Override public String body() { return ""; }
    }

    public record dw_danger() implements CssClass<DocWidgetStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Danger.class, Color.Surface.class), of(Danger.class, Color.Ink.class)); }
        @Override public String body() { return ""; }
    }

    /** A figure: what it shows, then its caption. */
    public record dw_figure() implements CssClass<DocWidgetStyles> {
        @Override public String body() { return "margin: 0;\ndisplay: flex;\nflex-direction: column;\ngap: 6px;\nmin-width: 0;\n"; }
    }

    /** An SVG drawn inline: as wide as it may be, in the text's colour. */
    public record dw_svg() implements CssClass<DocWidgetStyles> {
        @Override public String body() { return "display: block;\nmax-width: 100%;\nheight: auto;\n"; }
    }

    /** A table's or a figure's caption. */
    public record dw_caption() implements CssClass<DocWidgetStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Caption.class, Type.Scale.class), of(Muted.class, Color.Ink.class)); }
        @Override public String body() { return "margin: 0;\ntext-align: left;\n"; }
    }

    @Override
    public List<CssClass<DocWidgetStyles>> cssClasses() {
        return List.of(new dw_widget(), new dw_note(), new dw_hidden(), new dw_para(), new dw_heading(), new dw_list(), new dw_quote(), new dw_code_span(),
                new dw_link(), new dw_cite(), new dw_rule(), new dw_pre(), new dw_lang(), new dw_table_box(), new dw_table(), new dw_th(), new dw_td(),
                new dw_left(), new dw_center(), new dw_right(), new dw_strong(), new dw_dim(), new dw_badge(), new dw_success(), new dw_warning(),
                new dw_danger(), new dw_figure(), new dw_svg(), new dw_caption());
    }
}
