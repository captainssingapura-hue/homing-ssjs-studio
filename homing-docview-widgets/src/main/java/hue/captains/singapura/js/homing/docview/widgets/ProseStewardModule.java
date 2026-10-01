package hue.captains.singapura.js.homing.docview.widgets;

import hue.captains.singapura.js.homing.core.EsModule;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.Exportable;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;

import java.util.List;

/** The steward of a doc's prose parts: {@code new ProseSteward(tell)} - a {@code DocPartSteward} of the type prose. */
public record ProseStewardModule() implements EsModule<ProseStewardModule> {

    public static final ProseStewardModule INSTANCE = new ProseStewardModule();

    public record ProseSteward() implements Exportable._Class<ProseStewardModule> {}

    @Override
    public ImportsFor<ProseStewardModule> imports() {
        return ImportsFor.<ProseStewardModule>builder()
                .add(new ModuleImports<>(List.of(new DocPartStewardModule.DocPartSteward()), DocPartStewardModule.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<ProseStewardModule> exports() { return new ExportsOf<>(INSTANCE, List.of(new ProseSteward())); }
}
