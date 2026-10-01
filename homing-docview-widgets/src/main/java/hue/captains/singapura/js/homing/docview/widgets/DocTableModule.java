package hue.captains.singapura.js.homing.docview.widgets;

import hue.captains.singapura.js.homing.core.DomModule;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.workspace.widgets.SelfContainedWidget;

import java.util.List;

/** A doc's table part: {@code new DocTable(container, params)} - columns and rows, every cell markdown text. */
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
                .add(new ModuleImports<>(List.of(new DocWidgetStyles.dw_table_box(), new DocWidgetStyles.dw_table(), new DocWidgetStyles.dw_caption(),
                        new DocWidgetStyles.dw_th(), new DocWidgetStyles.dw_td(), new DocWidgetStyles.dw_left(), new DocWidgetStyles.dw_center(),
                        new DocWidgetStyles.dw_right(), new DocWidgetStyles.dw_strong(), new DocWidgetStyles.dw_dim(), new DocWidgetStyles.dw_badge(),
                        new DocWidgetStyles.dw_success(), new DocWidgetStyles.dw_warning(), new DocWidgetStyles.dw_danger()), DocWidgetStyles.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<DocTableModule> exports() { return new ExportsOf<>(INSTANCE, List.of(new DocTable())); }
}
