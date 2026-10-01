package hue.captains.singapura.js.homing.planview.widgets;

import hue.captains.singapura.js.homing.core.CssClass;
import hue.captains.singapura.js.homing.core.CssGroup;
import hue.captains.singapura.js.homing.core.CssVar;
import hue.captains.singapura.js.homing.core.Wearable;

import java.util.List;
import java.util.Set;

import static hue.captains.singapura.js.homing.design.DesignClass.of;
import static hue.captains.singapura.js.homing.design.Emphasis.Muted;
import static hue.captains.singapura.js.homing.design.Feedback.Success;
import static hue.captains.singapura.js.homing.design.Layer.Recessed;
import static hue.captains.singapura.js.homing.design.Structure.Hairline;
import static hue.captains.singapura.js.homing.design.Target.Color;
import static hue.captains.singapura.js.homing.design.Target.Type;
import static hue.captains.singapura.js.homing.design.Text.Caption;
import static hue.captains.singapura.js.homing.design.Text.Code;
import static hue.captains.singapura.js.homing.design.Text.Label;

/**
 * The sheet of a plan's widgets, in the design's words - beside the doc's, whose paragraphs,
 * links, badges and tables they wear too: a head's kicker, progress and counts; a list's rows and
 * their marks; a phase's head, the button its details are asked for by, and the details.
 */
public record PlanStyles() implements CssGroup<PlanStyles> {

    public static final PlanStyles INSTANCE = new PlanStyles();

    /** Above the plan's lede: what the plan is - small, set back, in capitals. */
    public record pl_kicker() implements CssClass<PlanStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Caption.class, Type.Scale.class), of(Label.class, Type.Weight.class), of(Muted.class, Color.Ink.class)); }
        @Override public String body() { return "margin: 0;\ntext-transform: uppercase;\nletter-spacing: 0.08em;\n"; }
    }

    /** A row: a bar, and what it says beside it. */
    public record pl_progress() implements CssClass<PlanStyles> {
        @Override public String body() { return "display: flex;\nalign-items: center;\ngap: 10px;\n"; }
    }

    /** Progress, as a bar: its track, sunk into the page. */
    public record pl_bar() implements CssClass<PlanStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Recessed.class, Color.Surface.class)); }
        @Override public String body() { return "flex: 0 1 280px;\nmin-width: 80px;\nheight: 8px;\nborder-radius: 999px;\noverflow: hidden;\n"; }
    }

    /** What is done of it: as far along the track as its share, which is data on it. */
    public record pl_bar_done() implements CssClass<PlanStyles> {
        @Override public Set<CssVar> runtimeVars() { return Set.of(new CssVar("--pl-done")); }
        @Override public List<? extends Wearable> wears() { return List.of(of(Success.class, Color.Ink.class)); }
        @Override public String body() { return "height: 100%;\nwidth: var(--pl-done, 0%);\nbackground-color: currentColor;\n"; }
    }

    /** A figure said beside a bar, or after a list: small, set back. */
    public record pl_figure() implements CssClass<PlanStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Caption.class, Type.Scale.class), of(Muted.class, Color.Ink.class)); }
        @Override public String body() { return "margin: 0;\nwhite-space: nowrap;\n"; }
    }

    /** The plan's counts, in a row that wraps. */
    public record pl_facts() implements CssClass<PlanStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Caption.class, Type.Scale.class), of(Muted.class, Color.Ink.class)); }
        @Override public String body() { return "list-style: none;\nmargin: 0;\npadding: 0;\ndisplay: flex;\nflex-wrap: wrap;\ngap: 4px 18px;\n"; }
    }

    /** A count's figure, set strong. */
    public record pl_count() implements CssClass<PlanStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Label.class, Type.Weight.class)); }
        @Override public String body() { return ""; }
    }

    /** The docs a plan names, each its role and where it goes. */
    public record pl_docs() implements CssClass<PlanStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Caption.class, Type.Scale.class)); }
        @Override public String body() { return "margin: 0;\ndisplay: flex;\nflex-wrap: wrap;\nalign-items: baseline;\ngap: 4px 16px;\n"; }
    }

    /** A plan's list: no markers, a row under the one before. */
    public record pl_rows() implements CssClass<PlanStyles> {
        @Override public String body() { return "list-style: none;\nmargin: 0;\npadding: 0;\ndisplay: flex;\nflex-direction: column;\n"; }
    }

    /** A row: its mark, then what it says; a hairline above it. */
    public record pl_row() implements CssClass<PlanStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Hairline.class, Color.Edge.class)); }
        @Override public String body() { return "display: flex;\nalign-items: flex-start;\ngap: 10px;\npadding: 8px 0;\nborder-top-width: 1px;\nborder-top-style: solid;\n"; }
    }

    /** A task: its mark, then the task - no hairline, close under the one before. */
    public record pl_task() implements CssClass<PlanStyles> {
        @Override public String body() { return "display: flex;\nalign-items: flex-start;\ngap: 8px;\npadding: 2px 0;\nline-height: 1.5;\n"; }
    }

    /** What a row says: its head, its text, more about it. */
    public record pl_row_body() implements CssClass<PlanStyles> {
        @Override public String body() { return "flex: 1 1 auto;\nmin-width: 0;\ndisplay: flex;\nflex-direction: column;\ngap: 4px;\n"; }
    }

    /** A row's head: its id, its title, its mark's word. */
    public record pl_row_head() implements CssClass<PlanStyles> {
        @Override public String body() { return "display: flex;\nflex-wrap: wrap;\nalign-items: baseline;\ngap: 8px;\n"; }
    }

    /** A row's title, set strong. */
    public record pl_row_title() implements CssClass<PlanStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Label.class, Type.Weight.class)); }
        @Override public String body() { return "line-height: 1.5;\n"; }
    }

    /** An id, as code is written: small, set back. */
    public record pl_id() implements CssClass<PlanStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Code.class, Type.Face.class), of(Caption.class, Type.Scale.class), of(Muted.class, Color.Ink.class)); }
        @Override public String body() { return ""; }
    }

    /** More about a row: its label, then its text. */
    public record pl_more() implements CssClass<PlanStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Muted.class, Color.Ink.class)); }
        @Override public String body() { return "margin: 0;\nline-height: 1.5;\n"; }
    }

    /** A label in the text: what follows is its. */
    public record pl_label() implements CssClass<PlanStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Label.class, Type.Weight.class)); }
        @Override public String body() { return "margin-right: 6px;\n"; }
    }

    /** Whether a thing is done: a ring, in the text's set-back colour. */
    public record pl_check() implements CssClass<PlanStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Muted.class, Color.Ink.class)); }
        @Override public String body() {
            return "flex: none;\nbox-sizing: border-box;\nwidth: 0.8em;\nheight: 0.8em;\nmargin-top: 0.35em;\nborder-radius: 50%;\nborder: 1.5px solid currentColor;\n";
        }
    }

    /** Done: the ring filled, in the colour of what went well. */
    public record pl_check_done() implements CssClass<PlanStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Success.class, Color.Ink.class)); }
        @Override public String body() { return "background-color: currentColor;\n"; }
    }

    /** A status not yet started: a badge with no colour of its own, its outline the page's. */
    public record pl_quiet() implements CssClass<PlanStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Hairline.class, Color.Edge.class), of(Muted.class, Color.Ink.class)); }
        @Override public String body() { return "border-width: 1px;\nborder-style: solid;\n"; }
    }

    /** A phase's head: its status, its progress, its effort - a row that wraps. */
    public record pl_phase_head() implements CssClass<PlanStyles> {
        @Override public String body() { return "display: flex;\nflex-wrap: wrap;\nalign-items: center;\ngap: 8px 14px;\n"; }
    }

    /** The button a phase's details are asked for by: its own width, at the start of the column. */
    public record pl_toggle() implements CssClass<PlanStyles> {
        @Override public String body() { return "align-self: flex-start;\n"; }
    }

    /** A phase's details: set in, a line down their side, a block under the one before. */
    public record pl_details() implements CssClass<PlanStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Hairline.class, Color.Edge.class)); }
        @Override public String body() { return "display: flex;\nflex-direction: column;\ngap: 14px;\npadding: 2px 0 2px 14px;\nborder-left-width: 2px;\nborder-left-style: solid;\n"; }
    }

    /** One block of the details: its heading, then what it holds. */
    public record pl_block() implements CssClass<PlanStyles> {
        @Override public String body() { return "display: flex;\nflex-direction: column;\ngap: 6px;\nmin-width: 0;\n"; }
    }

    /** A block's heading: small, strong, set back, in capitals. */
    public record pl_block_head() implements CssClass<PlanStyles> {
        @Override public List<? extends Wearable> wears() { return List.of(of(Caption.class, Type.Scale.class), of(Label.class, Type.Weight.class), of(Muted.class, Color.Ink.class)); }
        @Override public String body() { return "margin: 0;\ntext-transform: uppercase;\nletter-spacing: 0.06em;\n"; }
    }

    /** What is said away: last of the sheet, so what is hidden stays hidden whatever else it says of how it lays out. */
    public record pl_hidden() implements CssClass<PlanStyles> {
        @Override public String body() { return "display: none;\n"; }
    }

    @Override
    public List<CssClass<PlanStyles>> cssClasses() {
        return List.of(new pl_kicker(), new pl_progress(), new pl_bar(), new pl_bar_done(), new pl_figure(), new pl_facts(), new pl_count(), new pl_docs(),
                new pl_rows(), new pl_row(), new pl_task(), new pl_row_body(), new pl_row_head(), new pl_row_title(), new pl_id(), new pl_more(), new pl_label(),
                new pl_check(), new pl_check_done(), new pl_quiet(), new pl_phase_head(), new pl_toggle(), new pl_details(), new pl_block(), new pl_block_head(),
                // last, so what is hidden stays hidden
                new pl_hidden());
    }
}
