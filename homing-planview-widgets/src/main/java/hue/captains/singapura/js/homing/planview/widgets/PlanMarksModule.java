package hue.captains.singapura.js.homing.planview.widgets;

import hue.captains.singapura.js.homing.core.DomModule;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.Exportable;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.docview.widgets.DocWidgetStyles;

import java.util.List;

/**
 * The marks a plan's widgets set beside what they say: {@code PlanMarks.badge}, a status in the
 * colour of how it stands; {@code PlanMarks.check}, whether a thing is done; {@code PlanMarks.bar},
 * progress as a bar.
 */
public record PlanMarksModule() implements DomModule<PlanMarksModule> {

    public static final PlanMarksModule INSTANCE = new PlanMarksModule();

    public record PlanMarks() implements Exportable._Class<PlanMarksModule> {}

    @Override
    public ImportsFor<PlanMarksModule> imports() {
        return ImportsFor.<PlanMarksModule>builder()
                .add(new ModuleImports<>(List.of(new DocWidgetStyles.dw_badge(), new DocWidgetStyles.dw_success(), new DocWidgetStyles.dw_warning(),
                        new DocWidgetStyles.dw_danger()), DocWidgetStyles.INSTANCE))
                .add(new ModuleImports<>(List.of(new PlanStyles.pl_quiet(), new PlanStyles.pl_check(), new PlanStyles.pl_check_done(),
                        new PlanStyles.pl_bar(), new PlanStyles.pl_bar_done()), PlanStyles.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<PlanMarksModule> exports() { return new ExportsOf<>(INSTANCE, List.of(new PlanMarks())); }
}
