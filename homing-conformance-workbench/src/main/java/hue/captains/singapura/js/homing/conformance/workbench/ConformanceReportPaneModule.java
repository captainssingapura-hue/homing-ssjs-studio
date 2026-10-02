package hue.captains.singapura.js.homing.conformance.workbench;

import hue.captains.singapura.js.homing.component.keyboard.KeyBinding;
import hue.captains.singapura.js.homing.component.keyboard.NeedKeyboard;
import hue.captains.singapura.js.homing.core.DomModule;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.workspace.widgets.SelfContainedWidget;

import java.util.List;

/** {@code ConformanceReportPane}: the report the build exported, decoded, a section per module type. */
public record ConformanceReportPaneModule() implements DomModule<ConformanceReportPaneModule> {

    public static final ConformanceReportPaneModule INSTANCE = new ConformanceReportPaneModule();

    public record ConformanceReportPane() implements SelfContainedWidget<ConformanceReportPaneModule>, NeedKeyboard {
        @Override public String summary() { return "The report the build exported, decoded through its codecs: its verdict, then a section per module type, every module and its findings."; }
        @Override public List<KeyBinding> keys() { return List.of(WorkbenchKeys.GIVE_BACK); }
    }

    @Override
    public ImportsFor<ConformanceReportPaneModule> imports() {
        return ImportsFor.<ConformanceReportPaneModule>builder()
                .add(new ModuleImports<>(List.of(new WorkbenchPaneModule.WorkbenchPane()), WorkbenchPaneModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new WorkbenchFeedsModule.WorkbenchFeeds()), WorkbenchFeedsModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new ReportCodecsModule.ConformanceReportCodec(), new ReportCodecsModule.ModuleResultCodec()), ReportCodecsModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new WorkbenchStyles.wb_card(), new WorkbenchStyles.wb_verdict(), new WorkbenchStyles.wb_danger(),
                        new WorkbenchStyles.wb_warning(), new WorkbenchStyles.wb_success(), new WorkbenchStyles.wb_info(), new WorkbenchStyles.wb_hint(),
                        new WorkbenchStyles.wb_type(), new WorkbenchStyles.wb_section(), new WorkbenchStyles.wb_rules(), new WorkbenchStyles.wb_rule(),
                        new WorkbenchStyles.wb_line(), new WorkbenchStyles.wb_finding()), WorkbenchStyles.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<ConformanceReportPaneModule> exports() { return new ExportsOf<>(INSTANCE, List.of(new ConformanceReportPane())); }
}
