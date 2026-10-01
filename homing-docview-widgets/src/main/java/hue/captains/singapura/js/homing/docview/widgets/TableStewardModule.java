package hue.captains.singapura.js.homing.docview.widgets;

import hue.captains.singapura.js.homing.core.EsModule;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.Exportable;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;

import java.util.List;

/** The steward of a doc's table parts: {@code new TableSteward(tell)} - a {@code DocPartSteward} of the type table. */
public record TableStewardModule() implements EsModule<TableStewardModule> {

    public static final TableStewardModule INSTANCE = new TableStewardModule();

    public record TableSteward() implements Exportable._Class<TableStewardModule> {}

    @Override
    public ImportsFor<TableStewardModule> imports() {
        return ImportsFor.<TableStewardModule>builder()
                .add(new ModuleImports<>(List.of(new DocPartStewardModule.DocPartSteward()), DocPartStewardModule.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<TableStewardModule> exports() { return new ExportsOf<>(INSTANCE, List.of(new TableSteward())); }
}
