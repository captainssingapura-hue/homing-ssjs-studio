package hue.captains.singapura.js.homing.planview.widgets;

import hue.captains.singapura.js.homing.core.EsModule;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.Exportable;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.docview.widgets.DocPartStewardModule;

import java.util.List;

/** The steward of a plan's plan-list parts: {@code new PlanListSteward(tell)} - a {@code DocPartSteward} of the type plan-list, reading the plan's payload. */
public record PlanListStewardModule() implements EsModule<PlanListStewardModule> {

    public static final PlanListStewardModule INSTANCE = new PlanListStewardModule();

    public record PlanListSteward() implements Exportable._Class<PlanListStewardModule> {}

    @Override
    public ImportsFor<PlanListStewardModule> imports() {
        return ImportsFor.<PlanListStewardModule>builder()
                .add(new ModuleImports<>(List.of(new DocPartStewardModule.DocPartSteward()), DocPartStewardModule.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<PlanListStewardModule> exports() { return new ExportsOf<>(INSTANCE, List.of(new PlanListSteward())); }
}
