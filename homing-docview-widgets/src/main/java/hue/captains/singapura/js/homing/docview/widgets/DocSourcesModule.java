package hue.captains.singapura.js.homing.docview.widgets;

import hue.captains.singapura.js.homing.core.EsModule;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.Exportable;
import hue.captains.singapura.js.homing.core.ImportsFor;

import java.util.List;

/**
 * A doc as the page reads it: {@code DocSources.payload(doc)} - the doc's payload, fetched once
 * however many stewards read it - and {@code DocSources.part(doc, key)}, one part fetched by its
 * key. The one place a page reaches DocView's routes.
 */
public record DocSourcesModule() implements EsModule<DocSourcesModule> {

    public static final DocSourcesModule INSTANCE = new DocSourcesModule();

    public record DocSources() implements Exportable._Class<DocSourcesModule> {}

    @Override public ImportsFor<DocSourcesModule> imports() { return ImportsFor.noImports(); }

    @Override
    public ExportsOf<DocSourcesModule> exports() { return new ExportsOf<>(INSTANCE, List.of(new DocSources())); }
}
