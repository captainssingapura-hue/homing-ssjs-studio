package hue.captains.singapura.js.homing.docview.widgets;

import hue.captains.singapura.js.homing.core.EsModule;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.Exportable;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;

import java.util.List;

/** The steward of a doc's code parts: {@code new CodeSteward(tell)} - a {@code DocPartSteward} of the type code. */
public record CodeStewardModule() implements EsModule<CodeStewardModule> {

    public static final CodeStewardModule INSTANCE = new CodeStewardModule();

    public record CodeSteward() implements Exportable._Class<CodeStewardModule> {}

    @Override
    public ImportsFor<CodeStewardModule> imports() {
        return ImportsFor.<CodeStewardModule>builder()
                .add(new ModuleImports<>(List.of(new DocPartStewardModule.DocPartSteward()), DocPartStewardModule.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<CodeStewardModule> exports() { return new ExportsOf<>(INSTANCE, List.of(new CodeSteward())); }
}
