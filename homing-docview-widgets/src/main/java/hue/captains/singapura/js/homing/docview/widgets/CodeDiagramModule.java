package hue.captains.singapura.js.homing.docview.widgets;

import hue.captains.singapura.js.homing.core.DomModule;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.Exportable;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.ui.panzoom.PanZoomBarModule;
import hue.captains.singapura.js.homing.ui.panzoom.SvgPanZoomModule;

import java.util.List;

/**
 * A diagram's code part, drawn: {@code CodeDiagram.draw(content, branch, into, ask)} - the renderer
 * DocCode hands a diagram's language to. A selector on top, the diagram or its source; the diagram
 * asked of the diagram party with the widget's own params, drawn when the steward says it is ready.
 */
public record CodeDiagramModule() implements DomModule<CodeDiagramModule> {

    public static final CodeDiagramModule INSTANCE = new CodeDiagramModule();

    public record CodeDiagram() implements Exportable._Class<CodeDiagramModule> {}

    @Override
    public ImportsFor<CodeDiagramModule> imports() {
        return ImportsFor.<CodeDiagramModule>builder()
                .add(new ModuleImports<>(List.of(new DiagramContentModule.DIAGRAM()), DiagramContentModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new SvgMarkupModule.SvgMarkup()), SvgMarkupModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new SvgPanZoomModule.SvgPanZoom()), SvgPanZoomModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new PanZoomBarModule.PanZoomBar()), PanZoomBarModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new DocWidgetStyles.dw_views(), new DocWidgetStyles.dw_view(), new DocWidgetStyles.dw_lang(),
                        new DocWidgetStyles.dw_plate(), new DocWidgetStyles.dw_drawing(), new DocWidgetStyles.dw_diagram(), new DocWidgetStyles.dw_push(),
                        new DocWidgetStyles.dw_note(), new DocWidgetStyles.dw_hidden(),
                        new DocWidgetStyles.dw_pre()), DocWidgetStyles.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<CodeDiagramModule> exports() { return new ExportsOf<>(INSTANCE, List.of(new CodeDiagram())); }
}
