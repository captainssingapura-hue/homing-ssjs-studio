package hue.captains.singapura.js.homing.conformance.workbench;

import hue.captains.singapura.js.homing.component.keyboard.KeyBinding;
import hue.captains.singapura.js.homing.component.keyboard.NeedKeyboard;
import hue.captains.singapura.js.homing.core.DomModule;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.workspace.widgets.SelfContainedWidget;

import java.util.List;

/** {@code NodeSummary}: the node picked - root, crate, package or module - its kind, its name, its facts. */
public record NodeSummaryModule() implements DomModule<NodeSummaryModule> {

    public static final NodeSummaryModule INSTANCE = new NodeSummaryModule();

    public record NodeSummary() implements SelfContainedWidget<NodeSummaryModule>, NeedKeyboard {
        @Override public String summary() { return "The node picked in the navigator: its kind, its name and its facts."; }
        @Override public List<KeyBinding> keys() { return List.of(WorkbenchKeys.GIVE_BACK); }
    }

    @Override
    public ImportsFor<NodeSummaryModule> imports() {
        return ImportsFor.<NodeSummaryModule>builder()
                .add(new ModuleImports<>(List.of(new WorkbenchPaneModule.WorkbenchPane()), WorkbenchPaneModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new WorkbenchFeedsModule.WorkbenchFeeds()), WorkbenchFeedsModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new WorkbenchStyles.wb_card(), new WorkbenchStyles.wb_kicker(), new WorkbenchStyles.wb_title(),
                        new WorkbenchStyles.wb_code()), WorkbenchStyles.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<NodeSummaryModule> exports() { return new ExportsOf<>(INSTANCE, List.of(new NodeSummary())); }
}
