package hue.captains.singapura.js.homing.docview.widgets;

import hue.captains.singapura.js.homing.core.EsModule;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.Exportable;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.workspace.content.ContentParamsModule;

import java.util.List;

/**
 * What the stewards of a doc's plain parts have in common: {@code DocPartSteward} - sent for a
 * part, it reads the doc's payload, once for the doc, and tells what the part is - and, the first
 * time a doc's payload comes, tells every part of its type there, so what is wanted next is
 * answered at once.
 */
public record DocPartStewardModule() implements EsModule<DocPartStewardModule> {

    public static final DocPartStewardModule INSTANCE = new DocPartStewardModule();

    public record DocPartSteward() implements Exportable._Class<DocPartStewardModule> {}

    @Override
    public ImportsFor<DocPartStewardModule> imports() {
        return ImportsFor.<DocPartStewardModule>builder()
                .add(new ModuleImports<>(List.of(new ContentParamsModule.ContentParams()), ContentParamsModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new DocSourcesModule.DocSources()), DocSourcesModule.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<DocPartStewardModule> exports() { return new ExportsOf<>(INSTANCE, List.of(new DocPartSteward())); }
}
