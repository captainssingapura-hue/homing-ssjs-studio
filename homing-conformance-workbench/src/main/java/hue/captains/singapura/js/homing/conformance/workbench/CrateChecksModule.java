package hue.captains.singapura.js.homing.conformance.workbench;

import hue.captains.singapura.js.homing.component.keyboard.KeyBinding;
import hue.captains.singapura.js.homing.component.keyboard.NeedKeyboard;
import hue.captains.singapura.js.homing.core.DomModule;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.workspace.widgets.SelfContainedWidget;

import java.util.List;

/** {@code CrateChecks}: the crate of the node picked, and how it stands - orphans, illegal imports, rule violations. */
public record CrateChecksModule() implements DomModule<CrateChecksModule> {

    public static final CrateChecksModule INSTANCE = new CrateChecksModule();

    public record CrateChecks() implements SelfContainedWidget<CrateChecksModule>, NeedKeyboard {
        @Override public String summary() { return "The crate of the node picked and how it stands: its orphans, its illegal imports, its modules' rule violations."; }
        @Override public List<KeyBinding> keys() { return List.of(WorkbenchKeys.GIVE_BACK); }
    }

    @Override
    public ImportsFor<CrateChecksModule> imports() {
        return ImportsFor.<CrateChecksModule>builder()
                .add(new ModuleImports<>(List.of(new WorkbenchPaneModule.WorkbenchPane()), WorkbenchPaneModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new WorkbenchFeedsModule.WorkbenchFeeds()), WorkbenchFeedsModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new WorkbenchStyles.wb_card(), new WorkbenchStyles.wb_hint()), WorkbenchStyles.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<CrateChecksModule> exports() { return new ExportsOf<>(INSTANCE, List.of(new CrateChecks())); }
}
