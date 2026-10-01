package hue.captains.singapura.js.homing.planview.widgets;

import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.workspace.content.ContentParty;
import hue.captains.singapura.js.homing.workspace.content.Item;
import hue.captains.singapura.js.homing.workspace.content.Param;
import hue.captains.singapura.js.homing.workspace.parties.PartyType;

import java.util.List;

/**
 * A plan's phase, a content type: where it stands, and its details. What a plan phase widget
 * shows - its details folded until they are asked for.
 */
public sealed interface PlanPhaseContent {

    /**
     * A phase: its id; its status - its slug ({@code not-started}, {@code in-progress},
     * {@code blocked}, {@code done}) and its word; its progress over its tasks, 0 to 100; its
     * effort; its summary; and its details - description, tasks, metrics, the phases it depends on,
     * verification, rollback, notes - every text markdown.
     */
    record Step(String id, String status, String statusLabel, int progress, String effort, String summary, String description,
                List<Task> tasks, List<Metric> metrics, List<After> after, String verification, String rollback, String notes) {}

    record Task(String text, boolean done) {}

    record Metric(String label, String before, String after, String delta) {}

    /** A phase it depends on: its id; its section's path - empty when the plan has none by that id; its label; why. */
    record After(String phase, String path, String label, String reason) {}

    record Wanted(List<Param> params) implements PlanPhaseContent {}
    record Fetch(List<Item> items) implements PlanPhaseContent {}
    record Loaded(List<Param> params, Step content) implements PlanPhaseContent {}
    record Failed(List<Param> params, String why) implements PlanPhaseContent {}
    record Content(List<Param> params, Step content) implements PlanPhaseContent {}
    record Unavailable(List<Param> params, String why) implements PlanPhaseContent {}

    /** The type: {@code plan-phase}, served as {@code PLAN_PHASE}; its steward reads it from the plan's payload. */
    PartyType<PlanPhaseContent> TYPE = ContentParty.type("plan-phase", PlanPhaseContent.class)
            .servedFrom(new ModuleImports<>(List.of(new PlanPhaseContentModule.PLAN_PHASE()), PlanPhaseContentModule.INSTANCE))
            .withSteward(new ModuleImports<>(List.of(new PlanPhaseStewardModule.PlanPhaseSteward()), PlanPhaseStewardModule.INSTANCE));
}
