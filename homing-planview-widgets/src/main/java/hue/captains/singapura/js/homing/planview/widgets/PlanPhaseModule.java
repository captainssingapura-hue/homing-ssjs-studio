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
 * A plan's phase: {@code new PlanPhase(container, params)} - its status, progress, effort and
 * summary; its details - description, tasks, metrics, what it depends on, verification, rollback,
 * notes - folded until they are asked for.
 */
public record PlanPhaseModule() implements DomModule<PlanPhaseModule> {

    public static final PlanPhaseModule INSTANCE = new PlanPhaseModule();

    public record PlanPhase() implements SelfContainedWidget<PlanPhaseModule> {
        @Override public String summary() { return "A plan's phase: where it stands, and its details folded until they are asked for."; }
    }

    @Override
    public ImportsFor<PlanPhaseModule> imports() {
        return ImportsFor.<PlanPhaseModule>builder()
                .add(new ModuleImports<>(List.of(new ContentWidgetModule.ContentWidget()), ContentWidgetModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new MarkdownDomModule.MarkdownDom()), MarkdownDomModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new PlanPhaseContentModule.PLAN_PHASE()), PlanPhaseContentModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new PlanMarksModule.PlanMarks()), PlanMarksModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new HrefManager.HrefManagerInstance()), HrefManager.INSTANCE))
                .add(new ModuleImports<>(List.of(new DocWidgetStyles.dw_view(), new DocWidgetStyles.dw_link(), new DocWidgetStyles.dw_table_box(),
                        new DocWidgetStyles.dw_table(), new DocWidgetStyles.dw_th(), new DocWidgetStyles.dw_td()), DocWidgetStyles.INSTANCE))
                .add(new ModuleImports<>(List.of(new PlanStyles.pl_phase_head(), new PlanStyles.pl_figure(), new PlanStyles.pl_toggle(), new PlanStyles.pl_hidden(),
                        new PlanStyles.pl_details(), new PlanStyles.pl_block(), new PlanStyles.pl_block_head(), new PlanStyles.pl_rows(), new PlanStyles.pl_task()),
                        PlanStyles.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<PlanPhaseModule> exports() { return new ExportsOf<>(INSTANCE, List.of(new PlanPhase())); }
}
