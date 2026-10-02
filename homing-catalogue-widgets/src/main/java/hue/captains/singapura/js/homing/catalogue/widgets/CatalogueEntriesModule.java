package hue.captains.singapura.js.homing.catalogue.widgets;

import hue.captains.singapura.js.homing.core.EsModule;
import hue.captains.singapura.js.homing.core.Exportable;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;

import java.util.List;

/**
 * The site's catalogue, as the widgets read it: {@code CatalogueEntries} - an entry by
 * its authentic path, from the site's route, read once and kept; and what holds an
 * address, read off the address itself.
 */
public record CatalogueEntriesModule() implements EsModule<CatalogueEntriesModule> {

    public static final CatalogueEntriesModule INSTANCE = new CatalogueEntriesModule();

    /** Where the widgets read an entry from; the site serves it (homing-catalogue-site's EntryGetAction). */
    public static final String ROUTE = "/catalogue/entry";

    public record CatalogueEntries() implements Exportable._Class<CatalogueEntriesModule> {}

    @Override public ImportsFor<CatalogueEntriesModule> imports() { return ImportsFor.noImports(); }

    @Override
    public ExportsOf<CatalogueEntriesModule> exports() { return new ExportsOf<>(INSTANCE, List.of(new CatalogueEntries())); }
}
