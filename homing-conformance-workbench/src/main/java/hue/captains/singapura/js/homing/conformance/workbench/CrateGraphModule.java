package hue.captains.singapura.js.homing.conformance.workbench;

import hue.captains.singapura.js.homing.component.keyboard.KeyBinding;
import hue.captains.singapura.js.homing.component.keyboard.NeedKeyboard;
import hue.captains.singapura.js.homing.core.DomModule;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.docview.widgets.MermaidEngineModule;
import hue.captains.singapura.js.homing.docview.widgets.SvgMarkupModule;
import hue.captains.singapura.js.homing.ui.panzoom.PanZoomBarModule;
import hue.captains.singapura.js.homing.ui.panzoom.SvgPanZoomModule;
import hue.captains.singapura.js.homing.workspace.widgets.SelfContainedWidget;

import java.util.List;

/** {@code CrateGraph}: the crates and what each requires, drawn by DocView's mermaid engine, in a view that zooms and pans. */
public record CrateGraphModule() implements DomModule<CrateGraphModule> {

    public static final CrateGraphModule INSTANCE = new CrateGraphModule();

    public record CrateGraph() implements SelfContainedWidget<CrateGraphModule>, NeedKeyboard {
        @Override public String summary() { return "Every crate of the studio's closure and the crates each requires, drawn in the design's colours, zooming and panning."; }
        @Override public List<KeyBinding> keys() { return List.of(WorkbenchKeys.GIVE_BACK); }
    }

    @Override
    public ImportsFor<CrateGraphModule> imports() {
        return ImportsFor.<CrateGraphModule>builder()
                .add(new ModuleImports<>(List.of(new WorkbenchWidgetModule.WorkbenchWidget()), WorkbenchWidgetModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new WorkbenchFeedsModule.WorkbenchFeeds()), WorkbenchFeedsModule.INSTANCE))
                // drawn as DocView draws a diagram, read in safely, shown in a view that zooms
                .add(new ModuleImports<>(List.of(new MermaidEngineModule.MermaidEngine()), MermaidEngineModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new SvgMarkupModule.SvgMarkup()), SvgMarkupModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new SvgPanZoomModule.SvgPanZoom()), SvgPanZoomModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new PanZoomBarModule.PanZoomBar()), PanZoomBarModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new WorkbenchStyles.wb_graph(), new WorkbenchStyles.wb_hint(), new WorkbenchStyles.wb_graph_bar(),
                        new WorkbenchStyles.wb_graph_view(), new WorkbenchStyles.wb_hidden(), new WorkbenchStyles.wb_failed()), WorkbenchStyles.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<CrateGraphModule> exports() { return new ExportsOf<>(INSTANCE, List.of(new CrateGraph())); }
}
