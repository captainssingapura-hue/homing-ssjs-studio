package hue.captains.singapura.js.homing.docview.widgets;

import hue.captains.singapura.js.homing.core.EsModule;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.Exportable;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.workspace.content.ContentParamsModule;

import java.util.List;

/** The steward of a doc's images: {@code new ImageSteward(tell)} - each image fetched by its key, when it is wanted. */
public record ImageStewardModule() implements EsModule<ImageStewardModule> {

    public static final ImageStewardModule INSTANCE = new ImageStewardModule();

    public record ImageSteward() implements Exportable._Class<ImageStewardModule> {}

    @Override
    public ImportsFor<ImageStewardModule> imports() {
        return ImportsFor.<ImageStewardModule>builder()
                .add(new ModuleImports<>(List.of(new ContentParamsModule.ContentParams()), ContentParamsModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new DocSourcesModule.DocSources()), DocSourcesModule.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<ImageStewardModule> exports() { return new ExportsOf<>(INSTANCE, List.of(new ImageSteward())); }
}
