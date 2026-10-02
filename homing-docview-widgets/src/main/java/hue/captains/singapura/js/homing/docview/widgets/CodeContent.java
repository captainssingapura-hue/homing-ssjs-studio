package hue.captains.singapura.js.homing.docview.widgets;

import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.workspace.content.ContentParty;
import hue.captains.singapura.js.homing.workspace.content.Item;
import hue.captains.singapura.js.homing.workspace.content.Param;
import hue.captains.singapura.js.homing.workspace.parties.PartyType;

import java.util.List;

/**
 * Code, a content type: a language and its source, shown by the renderer registered for the language. What a code widget shows.
 */
public sealed interface CodeContent {

    /** Source in a language: lower case, empty when none is said. */
    record Code(String language, String source) {}

    record Wanted(List<Param> params) implements CodeContent {}
    record Fetch(List<Item> items) implements CodeContent {}
    record Loaded(List<Param> params, Code content) implements CodeContent {}
    record Failed(List<Param> params, String why) implements CodeContent {}
    record Content(List<Param> params, Code content) implements CodeContent {}
    record Unavailable(List<Param> params, String why) implements CodeContent {}

    /** The type: {@code code}, served as {@code CODE}; its steward DocView's, which reads it from the doc. */
    PartyType<CodeContent> TYPE = ContentParty.type("code", CodeContent.class)
            .servedFrom(new ModuleImports<>(List.of(new CodeContentModule.CODE()), CodeContentModule.INSTANCE))
            .withSteward(new ModuleImports<>(List.of(new CodeStewardModule.CodeSteward()), CodeStewardModule.INSTANCE));
}
