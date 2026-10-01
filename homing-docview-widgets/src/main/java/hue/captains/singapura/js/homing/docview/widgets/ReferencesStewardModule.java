package hue.captains.singapura.js.homing.docview.widgets;

import hue.captains.singapura.js.homing.core.EsModule;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.Exportable;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.workspace.content.ContentParamsModule;

import java.util.List;

/** The steward of a doc's references: {@code new ReferencesSteward(tell)} - read from the doc's payload, already resolved. */
public record ReferencesStewardModule() implements EsModule<ReferencesStewardModule> {

    public static final ReferencesStewardModule INSTANCE = new ReferencesStewardModule();

    public record ReferencesSteward() implements Exportable._Class<ReferencesStewardModule> {}

    @Override
    public ImportsFor<ReferencesStewardModule> imports() {
        return ImportsFor.<ReferencesStewardModule>builder()
                .add(new ModuleImports<>(List.of(new ContentParamsModule.ContentParams()), ContentParamsModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new DocSourcesModule.DocSources()), DocSourcesModule.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<ReferencesStewardModule> exports() { return new ExportsOf<>(INSTANCE, List.of(new ReferencesSteward())); }
}
