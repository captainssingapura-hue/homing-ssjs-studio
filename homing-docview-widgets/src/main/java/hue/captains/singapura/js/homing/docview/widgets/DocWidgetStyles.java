package hue.captains.singapura.js.homing.docview.widgets;

import hue.captains.singapura.js.homing.core.CssClass;
import hue.captains.singapura.js.homing.core.CssGroup;
import hue.captains.singapura.js.homing.core.Wearable;
import hue.captains.singapura.js.homing.design.DesignClass;

import java.util.List;

import static hue.captains.singapura.js.homing.design.DesignClass.of;
import static hue.captains.singapura.js.homing.design.Box.Control;
import static hue.captains.singapura.js.homing.design.Interaction.Selectable;
import static hue.captains.singapura.js.homing.design.Target.Affordance;
import static hue.captains.singapura.js.homing.design.Target.Motion;
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
 * quotes, code spans, links, rules; a code block and its language; a diagram, its plate and the
 * views picked between; a table - its cells, their
 * alignment, badges and emphasis; a figure and its caption.
 */
public record DocWidgetStyles() implements CssGroup<DocWidgetStyles> {

    public static final DocWidgetStyles INSTANCE = new DocWidgetStyles();

    /** The ground a diagram is drawn on - its plate's surface - which its engine draws on, in the design's colours (MermaidPalette). */
    public static final DesignClass<?> PLATE_GROUND = of(Base.class, Color.Surface.class);

    /**
     * A widget's root: a column, its content as tall as it needs in the flow of a doc - and growing
     * into a box its host sizes, as a stage's seat is: the widget never knows which it is in.
     */
    public record dw_widget() implements CssClass<DocWidgetStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Body.class, Type.Face.class), of(Body.class, Color.Ink.class)); }
        @Override public String body() { return "display: flex;\nflex-direction: column;\ngap: 10px;\nmin-width: 0;\nflex: 1 1 auto;\nmin-height: 0;\n"; }
    }

    /** A part of a widget that takes what room a sized box gives - a diagram's view, its source: a column of its own, scrolling what it cannot fit. */
    public record dw_fill() implements CssClass<DocWidgetStyles> {
        @Override public String body() { return "flex: 1 1 auto;\nmin-height: 0;\ndisplay: flex;\nflex-direction: column;\noverflow: auto;\n"; }
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

    /** A citation: a link to the reference it cites - what following it means is the page's. */
    public record dw_cite() implements CssClass<DocWidgetStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Link.class, Color.Ink.class), of(Link.class, Type.Decoration.class)); }
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
        @Override public String body() { return "margin: 0;\ndisplay: flex;\nflex-direction: column;\ngap: 6px;\nmin-width: 0;\nflex: 1 1 auto;\nmin-height: 0;\n"; }
    }

    /** An SVG drawn inline: as wide as it may be, in the text's colour. */
    public record dw_svg() implements CssClass<DocWidgetStyles> {
        @Override public String body() { return "display: block;\nmax-width: 100%;\nheight: auto;\n"; }
    }


    /** A part's views, side by side - a diagram, its source - and then what the part is. */
    public record dw_views() implements CssClass<DocWidgetStyles> {
        @Override public String body() { return "display: flex;\nalign-items: center;\ngap: 4px;\n"; }
    }

    /**
     * One of a part's views, to pick: a control's rule and corners, a tab's colours - seen at rest,
     * the design's when picked ({@code aria-selected}) - and the ring a control wears on focus.
     */
    public record dw_view() implements CssClass<DocWidgetStyles> {
        @Override public List<? extends Wearable> wears() {
            return List.of(of(Control.class, Shape.Rule.class), of(Control.class, Shape.Corner.class), of(Control.class, Color.Edge.class),
                    of(Selectable.Tab.class, Color.Surface.class), of(Selectable.Tab.class, Color.Ink.class), of(Selectable.Tab.class, Color.Edge.class),
                    of(Selectable.class, Motion.Ease.class), of(Selectable.class, Affordance.Cursor.class),
                    of(Body.class, Type.Face.class), of(Caption.class, Type.Scale.class));
        }
        @Override public String body() { return "margin: 0;\npadding: 2px 10px;\nborder-style: solid;\n"; }
    }

    /** A diagram's plate while there is no drawing: what it says, on the drawing's ground, framed as the drawing will be. */
    public record dw_plate() implements CssClass<DocWidgetStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(PLATE_GROUND, of(Hairline.class, Color.Edge.class), of(Code.class, Shape.Corner.class)); }
        @Override public String body() { return "padding: 12px;\nborder-width: 1px;\nborder-style: solid;\n"; }
    }

    /** A drawing's view, as a doc dresses it: on the plate's ground, its corners the code's - the frame and the ring are the view's own. */
    public record dw_drawing() implements CssClass<DocWidgetStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(PLATE_GROUND, of(Code.class, Shape.Corner.class)); }
        /** The design's colours a diagram is drawn in - read by the engine from the page: so every design binds them wherever a drawing is. */
        @Override public List<? extends Wearable> reads() { return MermaidPalette.pairs(); }
        @Override public String body() { return "padding: 12px;\nflex: 1 1 auto;\nmin-height: 0;\n"; }
    }

    /** A diagram drawn: centred in its view, as wide as its engine drew it and no wider than the view. */
    public record dw_diagram() implements CssClass<DocWidgetStyles> {
        @Override public String body() { return "display: block;\nmargin: 0 auto;\nmax-width: 100%;\nheight: auto;\n"; }
    }

    /** What sits at the far end of a row: a zoom bar after the views. */
    public record dw_push() implements CssClass<DocWidgetStyles> {
        @Override public String body() { return "margin-left: auto;\n"; }
    }

    /** A figure's zoom bar: above the drawing, at its far end. */
    public record dw_figure_bar() implements CssClass<DocWidgetStyles> {
        @Override public String body() { return "align-self: flex-end;\n"; }
    }

    /** A doc's references: the whole of the box it is lent - a column of the doc's page - a column of its own: its head, its table. */
    public record dw_refs() implements CssClass<DocWidgetStyles> {
        @Override public String body() { return "position: absolute;\ninset: 0;\nbox-sizing: border-box;\npadding: 12px;\noverflow: hidden;\n"; }
    }

    /** The references' head: what they are, and how many. */
    public record dw_refs_head() implements CssClass<DocWidgetStyles> {
        @Override public String body() { return "flex: none;\ndisplay: flex;\nalign-items: baseline;\ngap: 8px;\n"; }
    }

    public record dw_refs_title() implements CssClass<DocWidgetStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Heading.class, Type.Face.class), of(Heading.class, Type.Weight.class), of(Heading.class, Color.Ink.class)); }
        @Override public String body() { return "margin: 0;\nfont-size: inherit;\n"; }
    }

    public record dw_refs_count() implements CssClass<DocWidgetStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Caption.class, Type.Scale.class), of(Muted.class, Color.Ink.class)); }
        @Override public String body() { return ""; }
    }

    /** The table's box: the rest of the column, scrolling what it cannot hold. */
    public record dw_refs_box() implements CssClass<DocWidgetStyles> {
        @Override public String body() { return "flex: 1 1 auto;\nmin-height: 0;\noverflow: auto;\n"; }
    }

    /** A table's or a figure's caption. */
    public record dw_caption() implements CssClass<DocWidgetStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Caption.class, Type.Scale.class), of(Muted.class, Color.Ink.class)); }
        @Override public String body() { return "margin: 0;\ntext-align: left;\n"; }
    }

    @Override
    public List<CssClass<DocWidgetStyles>> cssClasses() {
        return List.of(new dw_widget(), new dw_note(), new dw_para(), new dw_heading(), new dw_list(), new dw_quote(), new dw_code_span(),
                new dw_link(), new dw_cite(), new dw_rule(), new dw_pre(), new dw_lang(), new dw_table_box(), new dw_table(), new dw_th(), new dw_td(),
                new dw_left(), new dw_center(), new dw_right(), new dw_strong(), new dw_dim(), new dw_badge(), new dw_success(), new dw_warning(),
                new dw_danger(), new dw_figure(), new dw_svg(), new dw_views(), new dw_view(), new dw_plate(), new dw_drawing(), new dw_diagram(), new dw_push(),
                new dw_figure_bar(), new dw_fill(), new dw_refs(), new dw_refs_head(), new dw_refs_title(), new dw_refs_count(), new dw_refs_box(),
                new dw_caption(),
                // last, so what is hidden stays hidden whatever else in the sheet says how it lays out
                new dw_hidden());
    }
}
