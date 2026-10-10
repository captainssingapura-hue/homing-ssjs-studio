package hue.captains.singapura.js.homing.docview.widgets;

import hue.captains.singapura.js.homing.conformance.rules.ConformanceDebt;
import hue.captains.singapura.js.homing.conformance.rules.WidgetDrawsOnlyComponentsRule;
import hue.captains.singapura.js.homing.core.DomModule;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.ui.panzoom.PanZoomBarModule;
import hue.captains.singapura.js.homing.ui.panzoom.SvgPanZoomModule;
import hue.captains.singapura.js.homing.workspace.widgets.SelfContainedWidget;

import java.util.List;

/** A doc's image part: {@code new DocImage(container, params)} - a figure, SVG drawn inline, its caption under it. */
@ConformanceDebt(rule = WidgetDrawsOnlyComponentsRule.class, findings = 22,
        reason = "It makes its own elements, places elements, wears classes of its own, writes attributes and writes text of its own: it waits for the designed containers, the widget slot and the content components to draw only components.")
public record DocImageModule() implements DomModule<DocImageModule> {

    public static final DocImageModule INSTANCE = new DocImageModule();

    public record DocImage() implements SelfContainedWidget<DocImageModule> {
        @Override public String summary() { return "A doc's image part: a figure - SVG drawn inline in the theme's colours, or a raster - its alt text its name, its caption under it."; }
    }

    @Override
    public ImportsFor<DocImageModule> imports() {
        return ImportsFor.<DocImageModule>builder()
                .add(new ModuleImports<>(List.of(new ContentWidgetModule.ContentWidget()), ContentWidgetModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new ImageContentModule.IMAGE()), ImageContentModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new SvgMarkupModule.SvgMarkup()), SvgMarkupModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new SvgPanZoomModule.SvgPanZoom()), SvgPanZoomModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new PanZoomBarModule.PanZoomBar()), PanZoomBarModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new DocWidgetStyles.dw_figure(), new DocWidgetStyles.dw_svg(), new DocWidgetStyles.dw_caption(),
                        new DocWidgetStyles.dw_drawing(), new DocWidgetStyles.dw_figure_bar(), new DocWidgetStyles.dw_views()),
                        DocWidgetStyles.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<DocImageModule> exports() { return new ExportsOf<>(INSTANCE, List.of(new DocImage())); }
}
