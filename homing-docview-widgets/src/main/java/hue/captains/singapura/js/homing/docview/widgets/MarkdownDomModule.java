package hue.captains.singapura.js.homing.docview.widgets;

import hue.captains.singapura.js.homing.core.DomModule;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.Exportable;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.libs.MarkedJs;
import hue.captains.singapura.js.homing.server.HrefManager;

import java.util.List;

/**
 * Markdown made into elements: {@code new MarkdownDom(branch)} - from marked's tokens, every
 * element through the branch it is given, never a string of markup; links through the href
 * manager.
 */
public record MarkdownDomModule() implements DomModule<MarkdownDomModule> {

    public static final MarkdownDomModule INSTANCE = new MarkdownDomModule();

    public record MarkdownDom() implements Exportable._Class<MarkdownDomModule> {}

    @Override
    public ImportsFor<MarkdownDomModule> imports() {
        return ImportsFor.<MarkdownDomModule>builder()
                .add(new ModuleImports<>(List.of(new MarkedJs.marked()), MarkedJs.INSTANCE))
                .add(new ModuleImports<>(List.of(new HrefManager.HrefManagerInstance()), HrefManager.INSTANCE))
                .add(new ModuleImports<>(List.of(new DocWidgetStyles.dw_para(), new DocWidgetStyles.dw_heading(), new DocWidgetStyles.dw_list(),
                        new DocWidgetStyles.dw_quote(), new DocWidgetStyles.dw_pre(), new DocWidgetStyles.dw_table_box(), new DocWidgetStyles.dw_table(),
                        new DocWidgetStyles.dw_th(), new DocWidgetStyles.dw_td(), new DocWidgetStyles.dw_left(), new DocWidgetStyles.dw_center(),
                        new DocWidgetStyles.dw_right(), new DocWidgetStyles.dw_rule(), new DocWidgetStyles.dw_code_span(), new DocWidgetStyles.dw_link(),
                        new DocWidgetStyles.dw_cite()), DocWidgetStyles.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<MarkdownDomModule> exports() { return new ExportsOf<>(INSTANCE, List.of(new MarkdownDom())); }
}
