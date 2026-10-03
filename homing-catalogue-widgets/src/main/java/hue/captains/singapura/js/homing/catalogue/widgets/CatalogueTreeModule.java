package hue.captains.singapura.js.homing.catalogue.widgets;

import hue.captains.singapura.js.homing.catalogue.widgets.CatalogueWidgetModule.CatalogueWidget;
import hue.captains.singapura.js.homing.component.keyboard.KeyBinding;
import hue.captains.singapura.js.homing.component.keyboard.KeysModule;
import hue.captains.singapura.js.homing.component.keyboard.NeedKeyboard;
import hue.captains.singapura.js.homing.core.DomModule;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.relgrid.protocol.RelGridProtocolModule;
import hue.captains.singapura.js.homing.reltree.RelTreeModule;
import hue.captains.singapura.js.homing.workspace.widgets.SelfContainedWidget;
import hue.captains.singapura.js.homing.workspace.widgets.WidgetStyles;

import java.util.List;

/**
 * A site's catalogue as a tree: {@code new CatalogueTree(container, params)} - from a
 * catalogue down, its catalogues and pages as rows, read from the site as they are
 * unfolded; the cursor the pick, Enter the asking to open.
 */
public record CatalogueTreeModule() implements DomModule<CatalogueTreeModule> {

    public static final CatalogueTreeModule INSTANCE = new CatalogueTreeModule();

    public record CatalogueTree() implements SelfContainedWidget<CatalogueTreeModule>, NeedKeyboard {
        @Override public String summary() { return "A site's catalogue as a tree: its catalogues and pages as rows; the cursor the pick, Enter the asking to open."; }
        @Override public List<KeyBinding> keys() { return List.of(CatalogueKeys.GIVE_BACK); }
    }

    @Override
    public ImportsFor<CatalogueTreeModule> imports() {
        return ImportsFor.<CatalogueTreeModule>builder()
                .add(new ModuleImports<>(List.of(new CatalogueWidget()), CatalogueWidgetModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new KeysModule.Keys()), KeysModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new CatalogueEntriesModule.CatalogueEntries()), CatalogueEntriesModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new CatalogueRowCellModule.CatalogueRowCell()), CatalogueRowCellModule.INSTANCE))
                // the relation tree and its questions
                .add(new ModuleImports<>(List.of(new RelTreeModule.RelTree()), RelTreeModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new RelGridProtocolModule.RelTreeView(), new RelGridProtocolModule.RelTreeUnfold(),
                        new RelGridProtocolModule.RelTreeFold(), new RelGridProtocolModule.RelTreeViewChanged()), RelGridProtocolModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new WidgetStyles.wg_scroll()), WidgetStyles.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<CatalogueTreeModule> exports() { return new ExportsOf<>(INSTANCE, List.of(new CatalogueTree())); }
}
