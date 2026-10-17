package hue.captains.singapura.js.homing.conformance.workbench;

import hue.captains.singapura.js.homing.component.keyboard.KeyBinding;
import hue.captains.singapura.js.homing.component.keyboard.NeedKeyboard;
import hue.captains.singapura.js.homing.conformance.rules.ConformanceDebt;
import hue.captains.singapura.js.homing.conformance.rules.WidgetDrawsOnlyComponentsRule;
import hue.captains.singapura.js.homing.core.DomModule;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.workspace.widgets.SelfContainedWidget;

import java.util.List;

/** {@code CrateNavigator}: the studio's crates, their packages, their modules - the cursor the workbench's pick. */
@ConformanceDebt(rule = WidgetDrawsOnlyComponentsRule.class, findings = 7,
        reason = "Its kind filter is a native select of its own, which it makes, fills and places: it waits for a designed select, and a container to hold it above the tree.")
public record CrateNavigatorModule() implements DomModule<CrateNavigatorModule> {

    public static final CrateNavigatorModule INSTANCE = new CrateNavigatorModule();

    public record CrateNavigator() implements SelfContainedWidget<CrateNavigatorModule>, NeedKeyboard {
        @Override public String summary() { return "The studio's crates under one root, each crate's modules by their packages; the cursor the pick, Enter the asking to open."; }
        @Override public List<KeyBinding> keys() { return List.of(WorkbenchKeys.GIVE_BACK); }
    }

    @Override
    public ImportsFor<CrateNavigatorModule> imports() {
        return ImportsFor.<CrateNavigatorModule>builder()
                .add(new ModuleImports<>(List.of(new WorkbenchNavigatorModule.WorkbenchNavigator()), WorkbenchNavigatorModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new WorkbenchFeedsModule.WorkbenchFeeds()), WorkbenchFeedsModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new WorkbenchStyles.wb_filter()), WorkbenchStyles.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<CrateNavigatorModule> exports() { return new ExportsOf<>(INSTANCE, List.of(new CrateNavigator())); }
}
