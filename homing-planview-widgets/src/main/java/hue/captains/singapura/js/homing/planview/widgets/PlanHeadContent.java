package hue.captains.singapura.js.homing.planview.widgets;

import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.workspace.content.ContentParty;
import hue.captains.singapura.js.homing.workspace.content.Item;
import hue.captains.singapura.js.homing.workspace.content.Param;
import hue.captains.singapura.js.homing.workspace.parties.PartyType;

import java.util.List;

/**
 * A plan's head, a content type: where the plan stands, at a glance. What a plan head widget shows.
 */
public sealed interface PlanHeadContent {

    /**
     * The plan at a glance: its kicker and lede - markdown inline; its progress, 0 to 100, over
     * every task; how many phases are done, decisions open and acceptance met, each of how many;
     * and the docs it names, each where the site reads it.
     */
    record Head(String kicker, String lede, int progress, Count phases, Count decisions, Count acceptance, List<Named> docs) {}

    /** So many, of how many. */
    record Count(int of, int total) {}

    /**
     * A doc the plan names: its role - execution plan, dossier - and, as a reference is, its kind
     * ({@code doc}, or {@code unplaced} when no catalogue places it), title, summary and where it goes.
     */
    record Named(String role, String kind, String title, String summary, String to) {}

    record Wanted(List<Param> params) implements PlanHeadContent {}
    record Fetch(List<Item> items) implements PlanHeadContent {}
    record Loaded(List<Param> params, Head content) implements PlanHeadContent {}
    record Failed(List<Param> params, String why) implements PlanHeadContent {}
    record Content(List<Param> params, Head content) implements PlanHeadContent {}
    record Unavailable(List<Param> params, String why) implements PlanHeadContent {}

    /** The type: {@code plan-head}, served as {@code PLAN_HEAD}; its steward reads it from the plan's payload. */
    PartyType<PlanHeadContent> TYPE = ContentParty.type("plan-head", PlanHeadContent.class)
            .servedFrom(new ModuleImports<>(List.of(new PlanHeadContentModule.PLAN_HEAD()), PlanHeadContentModule.INSTANCE))
            .withSteward(new ModuleImports<>(List.of(new PlanHeadStewardModule.PlanHeadSteward()), PlanHeadStewardModule.INSTANCE));
}
