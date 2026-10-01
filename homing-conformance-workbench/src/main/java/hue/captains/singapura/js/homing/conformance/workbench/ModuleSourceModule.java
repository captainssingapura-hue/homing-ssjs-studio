package hue.captains.singapura.js.homing.conformance.workbench;

import hue.captains.singapura.js.homing.component.keyboard.KeyBinding;
import hue.captains.singapura.js.homing.component.keyboard.NeedKeyboard;
import hue.captains.singapura.js.homing.core.DomModule;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.workspace.widgets.SelfContainedWidget;

import java.util.List;

/** {@code ModuleSource}: a module opened, shown as it is served - the very artifact the browser loads. */
public record ModuleSourceModule() implements DomModule<ModuleSourceModule> {

    public static final ModuleSourceModule INSTANCE = new ModuleSourceModule();

    public record ModuleSource() implements SelfContainedWidget<ModuleSourceModule>, NeedKeyboard {
        @Override public String summary() { return "A module opened in the navigator, shown verbatim as it is served."; }
        @Override public List<KeyBinding> keys() { return List.of(WorkbenchKeys.GIVE_BACK); }
    }

    @Override
    public ImportsFor<ModuleSourceModule> imports() {
        return ImportsFor.<ModuleSourceModule>builder()
                .add(new ModuleImports<>(List.of(new WorkbenchPaneModule.WorkbenchPane()), WorkbenchPaneModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new WorkbenchFeedsModule.WorkbenchFeeds()), WorkbenchFeedsModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new WorkbenchStyles.wb_card(), new WorkbenchStyles.wb_kicker(), new WorkbenchStyles.wb_title(),
                        new WorkbenchStyles.wb_code(), new WorkbenchStyles.wb_source()), WorkbenchStyles.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<ModuleSourceModule> exports() { return new ExportsOf<>(INSTANCE, List.of(new ModuleSource())); }
}
