package hue.captains.singapura.js.homing.docview.widgets;

import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.workspace.parties.PartyType;

import java.util.List;

/**
 * The reading party's words: where the reader is reading, said by the widgets that have no keys
 * of their own. Pressed, such a widget says so - by its own name, the one its placement keeps it
 * by - and the steward, the host's, takes the reader there in the contents: the keyboard's place in
 * a doc reader is the contents, unless a widget has a designed use for the keys.
 */
public sealed interface ReadingParty {

    /** A member: the reader pressed me, a widget with no keys of my own. */
    record ReadHere(String widget) implements ReadingParty {}

    /**
     * The type: {@code reading}, served as {@code READING}, with its secretary. Its steward is the
     * host's to hire - only the host knows the placement and the contents.
     */
    PartyType<ReadingParty> TYPE = new PartyType<>("reading", ReadingParty.class)
            .servedFrom(new ModuleImports<>(List.of(new ReadingPartyModule.READING()), ReadingPartyModule.INSTANCE))
            .withSecretary(new ModuleImports<>(List.of(new ReadingSecretaryModule.ReadingSecretary()), ReadingSecretaryModule.INSTANCE));
}
