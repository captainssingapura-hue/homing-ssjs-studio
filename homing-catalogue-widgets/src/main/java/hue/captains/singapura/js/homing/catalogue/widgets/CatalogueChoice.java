package hue.captains.singapura.js.homing.catalogue.widgets;

import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.workspace.parties.PartyType;

import java.util.List;

/**
 * What a catalogue party carries: which entry of a site's catalogue the widgets
 * that meet in it are about, and the asking to open one. An entry travels as its
 * authentic path - the address it is reached at - so {@code to} is always one.
 *
 * <p>A member does - {@link Pick}, {@link CurrentRequested}, {@link Open}, {@link Read};
 * the party says - {@link Picked}, {@link Opening}, {@link Reading}. How an entry opens is its app's to say
 * ({@code opens}: {@code in-place} or {@code new-tab}, as its leaf states); what
 * opening means is the host's - a site navigates, a workspace opens a tab.</p>
 */
public sealed interface CatalogueChoice {

    /** A member picked this entry. */
    record Pick(String to) implements CatalogueChoice {}

    /** A member asks which entry is picked - one that joins late - and is answered alone, when one is. */
    record CurrentRequested() implements CatalogueChoice {}

    /** A person asked to open this entry, which opens as its app says. */
    record Open(String to, String opens) implements CatalogueChoice {}

    /**
     * The reader is reading this entry: pressed in a widget that has no keys of its own - the keys
     * of a catalogue view are its lead's, its tree's, unless a widget has a designed use for them.
     */
    record Read(String to) implements CatalogueChoice {}

    /** The party says: the reader is reading this entry - the widget that leads the keys takes the reader there, and keeps them. */
    record Reading(String to) implements CatalogueChoice {}

    /** The party says: this entry is picked. */
    record Picked(String to) implements CatalogueChoice {}

    /** The party says: open this entry, as its app says - the host's to do. */
    record Opening(String to, String opens) implements CatalogueChoice {}

    /**
     * The type: {@code catalogue}, its identity on a page - its constant served as
     * {@code CATALOGUE}, and a root instance's secretary, unless a host puts its own,
     * {@code CatalogueChoiceSecretary}.
     */
    PartyType<CatalogueChoice> TYPE = new PartyType<>("catalogue", CatalogueChoice.class)
            .servedFrom(new ModuleImports<>(List.of(new CatalogueChoiceModule.CATALOGUE()), CatalogueChoiceModule.INSTANCE))
            .withSecretary(new ModuleImports<>(List.of(new CatalogueChoiceSecretaryModule.CatalogueChoiceSecretary()), CatalogueChoiceSecretaryModule.INSTANCE));
}
