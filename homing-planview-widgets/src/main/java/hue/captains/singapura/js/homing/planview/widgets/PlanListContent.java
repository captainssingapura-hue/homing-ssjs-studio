package hue.captains.singapura.js.homing.planview.widgets;

import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.workspace.content.ContentParty;
import hue.captains.singapura.js.homing.workspace.content.Item;
import hue.captains.singapura.js.homing.workspace.content.Param;
import hue.captains.singapura.js.homing.workspace.parties.PartyType;

import java.util.List;

/**
 * A plan's list, a content type: its objectives, its decisions, or its acceptance - one row each.
 * What a plan list widget shows.
 */
public sealed interface PlanListContent {

    /** A list of a plan's, and what it says of itself: {@code 2 of 5 met}, or empty. */
    record Rows(String note, List<Row> rows) {}

    /**
     * A row: its id, when it has one; its mark - {@code met}, {@code unmet}, a decision's status
     * ({@code open}, {@code resolved}) or empty - and the mark's word; its title and its text,
     * markdown; and more about it, each labelled: what was chosen, why, notes.
     */
    record Row(String id, String mark, String label, String title, String text, List<More> more) {}

    /** More about a row: a label, and its text, markdown. */
    record More(String label, String text) {}

    record Wanted(List<Param> params) implements PlanListContent {}
    record Fetch(List<Item> items) implements PlanListContent {}
    record Loaded(List<Param> params, Rows content) implements PlanListContent {}
    record Failed(List<Param> params, String why) implements PlanListContent {}
    record Content(List<Param> params, Rows content) implements PlanListContent {}
    record Unavailable(List<Param> params, String why) implements PlanListContent {}

    /** The type: {@code plan-list}, served as {@code PLAN_LIST}; its steward reads it from the plan's payload. */
    PartyType<PlanListContent> TYPE = ContentParty.type("plan-list", PlanListContent.class)
            .servedFrom(new ModuleImports<>(List.of(new PlanListContentModule.PLAN_LIST()), PlanListContentModule.INSTANCE))
            .withSteward(new ModuleImports<>(List.of(new PlanListStewardModule.PlanListSteward()), PlanListStewardModule.INSTANCE));
}
