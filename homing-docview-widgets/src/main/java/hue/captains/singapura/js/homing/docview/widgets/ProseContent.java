package hue.captains.singapura.js.homing.docview.widgets;

import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.workspace.content.ContentParty;
import hue.captains.singapura.js.homing.workspace.content.Item;
import hue.captains.singapura.js.homing.workspace.content.Param;
import hue.captains.singapura.js.homing.workspace.parties.PartyType;

import java.util.List;

/**
 * Prose, a content type: markdown text, rendered in the browser from its tokens. What a prose widget shows.
 */
public sealed interface ProseContent {

    /** Markdown text: paragraphs, lists, quotes, and what is inline in them. */
    record Prose(String text) {}

    record Wanted(List<Param> params) implements ProseContent {}
    record Fetch(List<Item> items) implements ProseContent {}
    record Loaded(List<Param> params, Prose content) implements ProseContent {}
    record Failed(List<Param> params, String why) implements ProseContent {}
    record Content(List<Param> params, Prose content) implements ProseContent {}
    record Unavailable(List<Param> params, String why) implements ProseContent {}

    /** The type: {@code prose}, served as {@code PROSE}; its steward DocView's, which reads it from the doc. */
    PartyType<ProseContent> TYPE = ContentParty.type("prose", ProseContent.class)
            .servedFrom(new ModuleImports<>(List.of(new ProseContentModule.PROSE()), ProseContentModule.INSTANCE))
            .withSteward(new ModuleImports<>(List.of(new ProseStewardModule.ProseSteward()), ProseStewardModule.INSTANCE));
}
