package hue.captains.singapura.js.homing.conformance.workbench;

import hue.captains.singapura.js.homing.component.keyboard.KeyBinding;
import hue.captains.singapura.js.homing.component.keyboard.NeedKeyboard;
import hue.captains.singapura.js.homing.core.DomModule;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.workspace.widgets.SelfContainedWidget;

import java.util.List;

/** {@code ModuleChecks}: the module picked and its own findings - its layering's and its rules'. */
public record ModuleChecksModule() implements DomModule<ModuleChecksModule> {

    public static final ModuleChecksModule INSTANCE = new ModuleChecksModule();

    public record ModuleChecks() implements SelfContainedWidget<ModuleChecksModule>, NeedKeyboard {
        @Override public String summary() { return "The module picked and its own findings: its layering's and its rules', as the build's report has them."; }
        @Override public List<KeyBinding> keys() { return List.of(WorkbenchKeys.GIVE_BACK); }
    }

    @Override
    public ImportsFor<ModuleChecksModule> imports() {
        return ImportsFor.<ModuleChecksModule>builder()
                .add(new ModuleImports<>(List.of(new WorkbenchPaneModule.WorkbenchPane()), WorkbenchPaneModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new WorkbenchFeedsModule.WorkbenchFeeds()), WorkbenchFeedsModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new WorkbenchStyles.wb_card(), new WorkbenchStyles.wb_hint(), new WorkbenchStyles.wb_code()), WorkbenchStyles.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<ModuleChecksModule> exports() { return new ExportsOf<>(INSTANCE, List.of(new ModuleChecks())); }
}
