package hue.captains.singapura.js.homing.docview.widgets;

import hue.captains.singapura.js.homing.conformance.rules.ConformanceDebt;
import hue.captains.singapura.js.homing.conformance.rules.WidgetDrawsOnlyComponentsRule;
import hue.captains.singapura.js.homing.core.DomModule;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.workspace.widgets.SelfContainedWidget;

import java.util.List;

/** A doc's code part: {@code new DocCode(container, params)} - one widget for every language, its renderer by the language. */
@ConformanceDebt(rule = WidgetDrawsOnlyComponentsRule.class, findings = 10,
        reason = "It makes its own elements, places elements, wears classes of its own and writes text of its own: it waits for the designed containers, the widget slot and the content components to draw only components.")
public record DocCodeModule() implements DomModule<DocCodeModule> {

    public static final DocCodeModule INSTANCE = new DocCodeModule();

    public record DocCode() implements SelfContainedWidget<DocCodeModule> {
        @Override public String summary() { return "A doc's code part: a language and its source, drawn by the renderer registered for the language, or as its source."; }
    }

    @Override
    public ImportsFor<DocCodeModule> imports() {
        return ImportsFor.<DocCodeModule>builder()
                .add(new ModuleImports<>(List.of(new ContentWidgetModule.ContentWidget()), ContentWidgetModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new CodeContentModule.CODE()), CodeContentModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new CodeDiagramModule.CodeDiagram()), CodeDiagramModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new DocWidgetStyles.dw_lang(), new DocWidgetStyles.dw_pre()), DocWidgetStyles.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<DocCodeModule> exports() { return new ExportsOf<>(INSTANCE, List.of(new DocCode())); }
}
