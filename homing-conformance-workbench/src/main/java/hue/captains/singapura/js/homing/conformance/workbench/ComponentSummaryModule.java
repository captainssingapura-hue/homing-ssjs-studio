package hue.captains.singapura.js.homing.conformance.workbench;

import hue.captains.singapura.js.homing.component.keyboard.KeyBinding;
import hue.captains.singapura.js.homing.component.keyboard.NeedKeyboard;
import hue.captains.singapura.js.homing.core.DomModule;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.workspace.widgets.SelfContainedWidget;

import java.util.List;

/** {@code ComponentSummary}: the component node picked - what it is, and for a component its shape, tag, module and crate. */
public record ComponentSummaryModule() implements DomModule<ComponentSummaryModule> {

    public static final ComponentSummaryModule INSTANCE = new ComponentSummaryModule();

    public record ComponentSummary() implements SelfContainedWidget<ComponentSummaryModule>, NeedKeyboard {
        @Override public String summary() { return "The component node picked: a composition, a vehicle, a catalogue, or a component with its shape, its tag, its module and its crate."; }
        @Override public List<KeyBinding> keys() { return List.of(WorkbenchKeys.GIVE_BACK); }
    }

    @Override
    public ImportsFor<ComponentSummaryModule> imports() {
        return ImportsFor.<ComponentSummaryModule>builder()
                .add(new ModuleImports<>(List.of(new WorkbenchPaneModule.WorkbenchPane()), WorkbenchPaneModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new WorkbenchFeedsModule.WorkbenchFeeds()), WorkbenchFeedsModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new WorkbenchStyles.wb_card(), new WorkbenchStyles.wb_kicker(), new WorkbenchStyles.wb_title(),
                        new WorkbenchStyles.wb_hint()), WorkbenchStyles.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<ComponentSummaryModule> exports() { return new ExportsOf<>(INSTANCE, List.of(new ComponentSummary())); }
}
