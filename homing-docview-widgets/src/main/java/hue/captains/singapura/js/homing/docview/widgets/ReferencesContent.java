package hue.captains.singapura.js.homing.docview.widgets;

import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.workspace.content.ContentParty;
import hue.captains.singapura.js.homing.workspace.content.Item;
import hue.captains.singapura.js.homing.workspace.content.Param;
import hue.captains.singapura.js.homing.workspace.parties.PartyType;

import java.util.List;

/**
 * A doc's references, a content type: every reference the doc declares, resolved by the site - a
 * doc's by its authentic path, an external one by its address - with the sections that cite it.
 * Asked by the doc alone ({@code { doc }}). What a references widget shows.
 */
public sealed interface ReferencesContent {

    /**
     * A reference: its name, as the doc cites it; its kind - doc, unplaced, external, image; the
     * title and summary of what it names; where it goes - an authentic path, an address, or empty;
     * and the sections that cite it, by path, the root's "".
     */
    record Ref(String name, String kind, String title, String summary, String to, List<String> citedIn) {}

    /** The doc's references, in the order it declares them - none, when it declares none. */
    record References(List<Ref> rows) {}

    record Wanted(List<Param> params) implements ReferencesContent {}
    record Fetch(List<Item> items) implements ReferencesContent {}
    record Loaded(List<Param> params, References content) implements ReferencesContent {}
    record Failed(List<Param> params, String why) implements ReferencesContent {}
    record Content(List<Param> params, References content) implements ReferencesContent {}
    record Unavailable(List<Param> params, String why) implements ReferencesContent {}

    /** The type: {@code references}, served as {@code REFERENCES}; its steward reads them from the doc's payload. */
    PartyType<ReferencesContent> TYPE = ContentParty.type("references", ReferencesContent.class)
            .servedFrom(new ModuleImports<>(List.of(new ReferencesContentModule.REFERENCES()), ReferencesContentModule.INSTANCE))
            .withSteward(new ModuleImports<>(List.of(new ReferencesStewardModule.ReferencesSteward()), ReferencesStewardModule.INSTANCE));
}
