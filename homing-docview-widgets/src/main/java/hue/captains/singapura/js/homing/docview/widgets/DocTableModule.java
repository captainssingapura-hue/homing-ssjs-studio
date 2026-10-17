package hue.captains.singapura.js.homing.docview.widgets;

import hue.captains.singapura.js.homing.conformance.rules.ConformanceDebt;
import hue.captains.singapura.js.homing.conformance.rules.WidgetDrawsOnlyComponentsRule;
import hue.captains.singapura.js.homing.core.DomModule;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.relgrid.prose.DocTableRelationModule;
import hue.captains.singapura.js.homing.workspace.widgets.SelfContainedWidget;

import java.util.List;

/** A doc's table part: {@code new DocTable(container, params)} - columns and rows, every cell markdown text. */
@ConformanceDebt(rule = WidgetDrawsOnlyComponentsRule.class, findings = 1,
        reason = "It places its table: it waits for the widget slot to show what it makes.")
public record DocTableModule() implements DomModule<DocTableModule> {

    public static final DocTableModule INSTANCE = new DocTableModule();

    public record DocTable() implements SelfContainedWidget<DocTableModule> {
        @Override public String summary() { return "A doc's table part: its caption, columns and rows, each cell's markdown drawn inline, aligned, spanned and badged as it says."; }
    }

    @Override
    public ImportsFor<DocTableModule> imports() {
        return ImportsFor.<DocTableModule>builder()
                .add(new ModuleImports<>(List.of(new ContentWidgetModule.ContentWidget()), ContentWidgetModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new MarkdownDomModule.MarkdownDom()), MarkdownDomModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new TableContentModule.TABLE()), TableContentModule.INSTANCE))
                // what its table says, judged when made; MarkdownDom draws it as a document's table
                .add(new ModuleImports<>(List.of(new DocTableRelationModule.DocTableRelation()), DocTableRelationModule.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<DocTableModule> exports() { return new ExportsOf<>(INSTANCE, List.of(new DocTable())); }
}
