package hue.captains.singapura.js.homing.docview.widgets;

import hue.captains.singapura.js.homing.core.DomModule;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.workspace.widgets.SelfContainedWidget;

import java.util.List;

/** A doc's image part: {@code new DocImage(container, params)} - a figure, SVG drawn inline, its caption under it. */
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
                .add(new ModuleImports<>(List.of(new DocWidgetStyles.dw_figure(), new DocWidgetStyles.dw_svg(), new DocWidgetStyles.dw_caption()),
                        DocWidgetStyles.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<DocImageModule> exports() { return new ExportsOf<>(INSTANCE, List.of(new DocImage())); }
}
