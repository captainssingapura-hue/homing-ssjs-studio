package hue.captains.singapura.js.homing.docview.widgets;

import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.workspace.content.ContentParty;
import hue.captains.singapura.js.homing.workspace.content.Item;
import hue.captains.singapura.js.homing.workspace.content.Param;
import hue.captains.singapura.js.homing.workspace.parties.PartyType;

import java.util.List;

/**
 * A diagram, a content type: a code part whose language is a diagram's, drawn - SVG markup, worked
 * out by the steward in the background, the diagram's engine loaded only when one is wanted. What a
 * code widget shows beside the source.
 */
public sealed interface DiagramContent {

    /** A diagram: the language it was written in, and the SVG it was drawn as. */
    record Diagram(String language, String svg) {}

    record Wanted(List<Param> params) implements DiagramContent {}
    record Fetch(List<Item> items) implements DiagramContent {}
    record Loaded(List<Param> params, Diagram content) implements DiagramContent {}
    record Failed(List<Param> params, String why) implements DiagramContent {}
    record Content(List<Param> params, Diagram content) implements DiagramContent {}
    record Unavailable(List<Param> params, String why) implements DiagramContent {}

    /** The type: {@code diagram}, served as {@code DIAGRAM}; its steward draws each diagram from the doc's source. */
    PartyType<DiagramContent> TYPE = ContentParty.type("diagram", DiagramContent.class)
            .servedFrom(new ModuleImports<>(List.of(new DiagramContentModule.DIAGRAM()), DiagramContentModule.INSTANCE))
            .withSteward(new ModuleImports<>(List.of(new DiagramStewardModule.DiagramSteward()), DiagramStewardModule.INSTANCE));
}
