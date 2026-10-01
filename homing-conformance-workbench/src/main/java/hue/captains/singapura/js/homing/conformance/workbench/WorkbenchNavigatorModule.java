package hue.captains.singapura.js.homing.conformance.workbench;

import hue.captains.singapura.js.homing.catalogue.widgets.CatalogueRowCellModule;
import hue.captains.singapura.js.homing.core.DomModule;
import hue.captains.singapura.js.homing.core.Exportable;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.relgrid.protocol.RelGridProtocolModule;
import hue.captains.singapura.js.homing.reltree.RelTreeModule;
import hue.captains.singapura.js.homing.workspace.widgets.WidgetStyles;

import java.util.List;

/**
 * A workbench's navigator, once: {@code WorkbenchNavigator}, the class a navigator extends - a
 * tree a feed gives, as the relation tree, its root first and open; the cursor the pick, Enter
 * the asking to open. A subclass says what it reads and how a row looks.
 */
public record WorkbenchNavigatorModule() implements DomModule<WorkbenchNavigatorModule> {

    public static final WorkbenchNavigatorModule INSTANCE = new WorkbenchNavigatorModule();

    public record WorkbenchNavigator() implements Exportable._Class<WorkbenchNavigatorModule> {}

    @Override
    public ImportsFor<WorkbenchNavigatorModule> imports() {
        return ImportsFor.<WorkbenchNavigatorModule>builder()
                .add(new ModuleImports<>(List.of(new WorkbenchWidgetModule.WorkbenchWidget()), WorkbenchWidgetModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new CatalogueRowCellModule.CatalogueRowCell()), CatalogueRowCellModule.INSTANCE))
                // the relation tree and its questions
                .add(new ModuleImports<>(List.of(new RelTreeModule.RelTree()), RelTreeModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new RelGridProtocolModule.RelTreeView(), new RelGridProtocolModule.RelTreeUnfold(),
                        new RelGridProtocolModule.RelTreeFold(), new RelGridProtocolModule.RelTreeViewChanged()), RelGridProtocolModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new WidgetStyles.wg_scroll()), WidgetStyles.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<WorkbenchNavigatorModule> exports() { return new ExportsOf<>(INSTANCE, List.of(new WorkbenchNavigator())); }
}
