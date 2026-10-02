package hue.captains.singapura.js.homing.conformance.workbench;

import hue.captains.singapura.js.homing.core.DomModule;
import hue.captains.singapura.js.homing.core.Exportable;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;

import java.util.List;

/**
 * A workbench pane, once: {@code WorkbenchPane}, the class a pane extends - a box it fills, a hint
 * until there is something to show, what it shows on a branch of its own in place of the one
 * before; a verdict, a section, facts, each drawn the one way.
 */
public record WorkbenchPaneModule() implements DomModule<WorkbenchPaneModule> {

    public static final WorkbenchPaneModule INSTANCE = new WorkbenchPaneModule();

    public record WorkbenchPane() implements Exportable._Class<WorkbenchPaneModule> {}

    @Override
    public ImportsFor<WorkbenchPaneModule> imports() {
        return ImportsFor.<WorkbenchPaneModule>builder()
                .add(new ModuleImports<>(List.of(new WorkbenchWidgetModule.WorkbenchWidget()), WorkbenchWidgetModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new WorkbenchStyles.wb_pane(), new WorkbenchStyles.wb_hint(), new WorkbenchStyles.wb_failed(),
                        new WorkbenchStyles.wb_hidden(), new WorkbenchStyles.wb_verdict(), new WorkbenchStyles.wb_success(), new WorkbenchStyles.wb_danger(),
                        new WorkbenchStyles.wb_section(), new WorkbenchStyles.wb_line(), new WorkbenchStyles.wb_facts(), new WorkbenchStyles.wb_key(),
                        new WorkbenchStyles.wb_value()), WorkbenchStyles.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<WorkbenchPaneModule> exports() { return new ExportsOf<>(INSTANCE, List.of(new WorkbenchPane())); }
}
