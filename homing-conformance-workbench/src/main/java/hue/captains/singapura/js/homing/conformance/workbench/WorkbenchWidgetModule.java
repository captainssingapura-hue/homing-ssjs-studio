package hue.captains.singapura.js.homing.conformance.workbench;

import hue.captains.singapura.js.homing.component.keyboard.FocusPartyModule;
import hue.captains.singapura.js.homing.component.keyboard.KeysModule;
import hue.captains.singapura.js.homing.component.keyboard.focusParties;
import hue.captains.singapura.js.homing.core.DomModule;
import hue.captains.singapura.js.homing.core.Exportable;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.core.js.DomOpsPartyModule;
import hue.captains.singapura.js.homing.core.js.domOpsParties;
import hue.captains.singapura.js.homing.workspace.widgets.WidgetStyles;

import java.util.List;

/**
 * What every workbench widget is to its host, once: {@code WorkbenchWidget}, the class a
 * workbench widget extends - its own roots, the workbench party joined when given, a pick and an
 * asking to open told.
 */
public record WorkbenchWidgetModule() implements DomModule<WorkbenchWidgetModule> {

    public static final WorkbenchWidgetModule INSTANCE = new WorkbenchWidgetModule();

    public record WorkbenchWidget() implements Exportable._Class<WorkbenchWidgetModule> {}

    @Override
    public ImportsFor<WorkbenchWidgetModule> imports() {
        return ImportsFor.<WorkbenchWidgetModule>builder()
                .add(new ModuleImports<>(List.of(new domOpsParties()), DomOpsPartyModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new focusParties()), FocusPartyModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new KeysModule.Keys()), KeysModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new WorkbenchChoiceModule.WORKBENCH()), WorkbenchChoiceModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new WidgetStyles.wg_fill()), WidgetStyles.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<WorkbenchWidgetModule> exports() { return new ExportsOf<>(INSTANCE, List.of(new WorkbenchWidget())); }
}
