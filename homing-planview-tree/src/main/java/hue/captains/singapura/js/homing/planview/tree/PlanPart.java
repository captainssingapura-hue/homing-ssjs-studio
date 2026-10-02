package hue.captains.singapura.js.homing.planview.tree;

import hue.captains.singapura.js.homing.studio.base.tracker.Acceptance;
import hue.captains.singapura.js.homing.studio.base.tracker.Decision;
import hue.captains.singapura.js.homing.studio.base.tracker.Objective;
import hue.captains.singapura.js.homing.studio.base.tracker.Phase;
import hue.captains.singapura.js.homing.studio.base.tracker.Plan;

import java.util.List;
import java.util.Objects;

/**
 * A part of a plan's tree: one widget's worth of the plan - a slice of the model as it is, the
 * shape of its content the payload's to make ({@link PlanPayload}). Each is a widget type, and the
 * content type it is asked of.
 */
public sealed interface PlanPart permits PlanPart.Head, PlanPart.Objectives, PlanPart.Decisions, PlanPart.Acceptances, PlanPart.Step {

    /** The plan's head: where it stands, at a glance. */
    String HEAD = "plan-head";

    /** A list of a plan's: its objectives, its decisions, its acceptance. */
    String LIST = "plan-list";

    /** A phase: where it stands, its details folded. */
    String PHASE = "plan-phase";

    /** The widget type that shows it, and the content type it is asked of. */
    String type();

    /** The plan as its head shows it: its kicker and lede, its progress, its counts, the docs it names. */
    record Head(Plan plan) implements PlanPart {
        public Head { Objects.requireNonNull(plan, "Head.plan"); }
        @Override public String type() { return HEAD; }
    }

    record Objectives(List<Objective> items) implements PlanPart {
        public Objectives { items = List.copyOf(Objects.requireNonNull(items, "Objectives.items")); }
        @Override public String type() { return LIST; }
    }

    record Decisions(List<Decision> items) implements PlanPart {
        public Decisions { items = List.copyOf(Objects.requireNonNull(items, "Decisions.items")); }
        @Override public String type() { return LIST; }
    }

    record Acceptances(List<Acceptance> items) implements PlanPart {
        public Acceptances { items = List.copyOf(Objects.requireNonNull(items, "Acceptances.items")); }
        @Override public String type() { return LIST; }
    }

    /** A phase, with the phases it depends on found in the tree. */
    record Step(Phase phase, List<After> after) implements PlanPart {
        public Step {
            Objects.requireNonNull(phase, "Step.phase");
            after = List.copyOf(Objects.requireNonNull(after, "Step.after"));
        }
        @Override public String type() { return PHASE; }
    }

    /**
     * A phase a phase depends on, as the tree has it.
     *
     * @param phase  its id, as the dependency names it
     * @param path   its section's path - empty when the plan has no phase by that id
     * @param label  its label, or empty
     * @param reason why it is depended on
     */
    record After(String phase, String path, String label, String reason) {
        public After {
            Objects.requireNonNull(phase, "After.phase");
            Objects.requireNonNull(path, "After.path");
            Objects.requireNonNull(label, "After.label");
            Objects.requireNonNull(reason, "After.reason");
        }
    }
}
