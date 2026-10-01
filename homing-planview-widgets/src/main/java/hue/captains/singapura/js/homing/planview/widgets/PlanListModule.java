package hue.captains.singapura.js.homing.planview.widgets;

import hue.captains.singapura.js.homing.core.DomModule;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.docview.widgets.ContentWidgetModule;
import hue.captains.singapura.js.homing.docview.widgets.MarkdownDomModule;
import hue.captains.singapura.js.homing.workspace.widgets.SelfContainedWidget;

import java.util.List;

/**
 * A list of a plan's: {@code new PlanList(container, params)} - its objectives, its decisions or its
 * acceptance, a row each: its mark, its id and title, its text, more about it.
 */
public record PlanListModule() implements DomModule<PlanListModule> {

    public static final PlanListModule INSTANCE = new PlanListModule();

    public record PlanList() implements SelfContainedWidget<PlanListModule> {
        @Override public String summary() { return "A list of a plan's - objectives, decisions, acceptance: a row each, its mark, its title and text, more about it."; }
    }

    @Override
    public ImportsFor<PlanListModule> imports() {
        return ImportsFor.<PlanListModule>builder()
                .add(new ModuleImports<>(List.of(new ContentWidgetModule.ContentWidget()), ContentWidgetModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new MarkdownDomModule.MarkdownDom()), MarkdownDomModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new PlanListContentModule.PLAN_LIST()), PlanListContentModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new PlanMarksModule.PlanMarks()), PlanMarksModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new PlanStyles.pl_figure(), new PlanStyles.pl_rows(), new PlanStyles.pl_row(), new PlanStyles.pl_row_body(),
                        new PlanStyles.pl_row_head(), new PlanStyles.pl_row_title(), new PlanStyles.pl_id(), new PlanStyles.pl_more(), new PlanStyles.pl_label()),
                        PlanStyles.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<PlanListModule> exports() { return new ExportsOf<>(INSTANCE, List.of(new PlanList())); }
}
