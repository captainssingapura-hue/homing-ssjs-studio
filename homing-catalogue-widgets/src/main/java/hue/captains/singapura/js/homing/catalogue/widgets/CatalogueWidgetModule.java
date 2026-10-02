package hue.captains.singapura.js.homing.catalogue.widgets;

import hue.captains.singapura.js.homing.component.keyboard.FocusPartyModule;
import hue.captains.singapura.js.homing.component.keyboard.KeysModule;
import hue.captains.singapura.js.homing.component.keyboard.focusParties;
import hue.captains.singapura.js.homing.core.DomModule;
import hue.captains.singapura.js.homing.core.Exportable;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.core.js.DomOpsPartyModule;
import hue.captains.singapura.js.homing.core.js.domOpsParties;
import hue.captains.singapura.js.homing.server.HrefManager;
import hue.captains.singapura.js.homing.workspace.widgets.WidgetStyles;

import java.util.List;

/**
 * What every catalogue widget is to its host, once: {@code CatalogueWidget}, the class a
 * catalogue widget extends - its own roots, the catalogue party joined when given, a
 * pick and an opening told; alone, an entry opened as its app says.
 */
public record CatalogueWidgetModule() implements DomModule<CatalogueWidgetModule> {

    public static final CatalogueWidgetModule INSTANCE = new CatalogueWidgetModule();

    public record CatalogueWidget() implements Exportable._Class<CatalogueWidgetModule> {}

    @Override
    public ImportsFor<CatalogueWidgetModule> imports() {
        return ImportsFor.<CatalogueWidgetModule>builder()
                .add(new ModuleImports<>(List.of(new domOpsParties()), DomOpsPartyModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new focusParties()), FocusPartyModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new KeysModule.Keys()), KeysModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new HrefManager.HrefManagerInstance()), HrefManager.INSTANCE))
                .add(new ModuleImports<>(List.of(new CatalogueChoiceModule.CATALOGUE()), CatalogueChoiceModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new CatalogueEntriesModule.CatalogueEntries()), CatalogueEntriesModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new WidgetStyles.wg_fill()), WidgetStyles.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<CatalogueWidgetModule> exports() { return new ExportsOf<>(INSTANCE, List.of(new CatalogueWidget())); }
}
