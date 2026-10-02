package hue.captains.singapura.js.homing.planview.widgets;

import hue.captains.singapura.js.homing.core.EsModule;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.Exportable;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.docview.widgets.DocPartStewardModule;

import java.util.List;

/** The steward of a plan's plan-phase parts: {@code new PlanPhaseSteward(tell)} - a {@code DocPartSteward} of the type plan-phase, reading the plan's payload. */
public record PlanPhaseStewardModule() implements EsModule<PlanPhaseStewardModule> {

    public static final PlanPhaseStewardModule INSTANCE = new PlanPhaseStewardModule();

    public record PlanPhaseSteward() implements Exportable._Class<PlanPhaseStewardModule> {}

    @Override
    public ImportsFor<PlanPhaseStewardModule> imports() {
        return ImportsFor.<PlanPhaseStewardModule>builder()
                .add(new ModuleImports<>(List.of(new DocPartStewardModule.DocPartSteward()), DocPartStewardModule.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<PlanPhaseStewardModule> exports() { return new ExportsOf<>(INSTANCE, List.of(new PlanPhaseSteward())); }
}
