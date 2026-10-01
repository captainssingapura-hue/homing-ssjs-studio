package hue.captains.singapura.js.homing.conformance.workbench;

import hue.captains.singapura.js.homing.component.keyboard.KeyBinding;
import hue.captains.singapura.js.homing.component.keyboard.NeedKeyboard;
import hue.captains.singapura.js.homing.core.DomModule;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.workspace.widgets.SelfContainedWidget;

import java.util.List;

/** {@code ComponentNavigator}: the components the studio's crates deliver - root, vehicle, family, component - the cursor the pick. */
public record ComponentNavigatorModule() implements DomModule<ComponentNavigatorModule> {

    public static final ComponentNavigatorModule INSTANCE = new ComponentNavigatorModule();

    public record ComponentNavigator() implements SelfContainedWidget<ComponentNavigatorModule>, NeedKeyboard {
        @Override public String summary() { return "The components the studio's crates deliver, composed from their closure: root, vehicle, family, component; the cursor the pick."; }
        @Override public List<KeyBinding> keys() { return List.of(WorkbenchKeys.GIVE_BACK); }
    }

    @Override
    public ImportsFor<ComponentNavigatorModule> imports() {
        return ImportsFor.<ComponentNavigatorModule>builder()
                .add(new ModuleImports<>(List.of(new WorkbenchNavigatorModule.WorkbenchNavigator()), WorkbenchNavigatorModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new WorkbenchFeedsModule.WorkbenchFeeds()), WorkbenchFeedsModule.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<ComponentNavigatorModule> exports() { return new ExportsOf<>(INSTANCE, List.of(new ComponentNavigator())); }
}
