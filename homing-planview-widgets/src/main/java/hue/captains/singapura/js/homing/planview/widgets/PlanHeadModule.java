package hue.captains.singapura.js.homing.planview.widgets;

import hue.captains.singapura.js.homing.core.DomModule;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.docview.widgets.ContentWidgetModule;
import hue.captains.singapura.js.homing.docview.widgets.DocWidgetStyles;
import hue.captains.singapura.js.homing.docview.widgets.MarkdownDomModule;
import hue.captains.singapura.js.homing.server.HrefManager;
import hue.captains.singapura.js.homing.workspace.widgets.SelfContainedWidget;

import java.util.List;

/**
 * A plan's head: {@code new PlanHead(container, params)} - what the plan is and its lede; its
 * progress, a bar; its counts; the docs it names, each where the site reads it.
 */
public record PlanHeadModule() implements DomModule<PlanHeadModule> {

    public static final PlanHeadModule INSTANCE = new PlanHeadModule();

    public record PlanHead() implements SelfContainedWidget<PlanHeadModule> {
        @Override public String summary() { return "A plan's head: its kicker and lede, its progress as a bar, its counts, the docs it names."; }
    }

    @Override
    public ImportsFor<PlanHeadModule> imports() {
        return ImportsFor.<PlanHeadModule>builder()
                .add(new ModuleImports<>(List.of(new ContentWidgetModule.ContentWidget()), ContentWidgetModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new MarkdownDomModule.MarkdownDom()), MarkdownDomModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new PlanHeadContentModule.PLAN_HEAD()), PlanHeadContentModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new PlanMarksModule.PlanMarks()), PlanMarksModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new HrefManager.HrefManagerInstance()), HrefManager.INSTANCE))
                .add(new ModuleImports<>(List.of(new DocWidgetStyles.dw_para(), new DocWidgetStyles.dw_link(), new DocWidgetStyles.dw_badge(),
                        new DocWidgetStyles.dw_warning()), DocWidgetStyles.INSTANCE))
                .add(new ModuleImports<>(List.of(new PlanStyles.pl_kicker(), new PlanStyles.pl_progress(), new PlanStyles.pl_figure(), new PlanStyles.pl_facts(),
                        new PlanStyles.pl_count(), new PlanStyles.pl_docs(), new PlanStyles.pl_label()), PlanStyles.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<PlanHeadModule> exports() { return new ExportsOf<>(INSTANCE, List.of(new PlanHead())); }
}
