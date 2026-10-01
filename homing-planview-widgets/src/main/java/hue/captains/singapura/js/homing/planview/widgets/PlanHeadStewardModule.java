package hue.captains.singapura.js.homing.planview.widgets;

import hue.captains.singapura.js.homing.core.EsModule;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.Exportable;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.docview.widgets.DocPartStewardModule;

import java.util.List;

/** The steward of a plan's plan-head parts: {@code new PlanHeadSteward(tell)} - a {@code DocPartSteward} of the type plan-head, reading the plan's payload. */
public record PlanHeadStewardModule() implements EsModule<PlanHeadStewardModule> {

    public static final PlanHeadStewardModule INSTANCE = new PlanHeadStewardModule();

    public record PlanHeadSteward() implements Exportable._Class<PlanHeadStewardModule> {}

    @Override
    public ImportsFor<PlanHeadStewardModule> imports() {
        return ImportsFor.<PlanHeadStewardModule>builder()
                .add(new ModuleImports<>(List.of(new DocPartStewardModule.DocPartSteward()), DocPartStewardModule.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<PlanHeadStewardModule> exports() { return new ExportsOf<>(INSTANCE, List.of(new PlanHeadSteward())); }
}
