package hue.captains.singapura.js.homing.catalogue.widgets;

import hue.captains.singapura.js.homing.catalogue.widgets.CatalogueWidgetModule.CatalogueWidget;
import hue.captains.singapura.js.homing.component.keyboard.KeyBinding;
import hue.captains.singapura.js.homing.component.keyboard.NeedKeyboard;
import hue.captains.singapura.js.homing.core.DomModule;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.ui.elements.Elements;
import hue.captains.singapura.js.homing.workspace.widgets.SelfContainedWidget;
import hue.captains.singapura.js.homing.workspace.widgets.WidgetStyles;

import java.util.List;

/** The entry that is picked, as a card: {@code new CatalogueDetails(container, params)} - its name, badge, summary, and the way to it. */
public record CatalogueDetailsModule() implements DomModule<CatalogueDetailsModule> {

    public static final CatalogueDetailsModule INSTANCE = new CatalogueDetailsModule();

    public record CatalogueDetails() implements SelfContainedWidget<CatalogueDetailsModule>, NeedKeyboard {
        @Override public String summary() { return "The entry that is picked, as a card: its name, its badge, its summary, and the way to it."; }
        @Override public List<KeyBinding> keys() { return List.of(CatalogueKeys.GIVE_BACK); }
    }

    @Override
    public ImportsFor<CatalogueDetailsModule> imports() {
        return ImportsFor.<CatalogueDetailsModule>builder()
                .add(new ModuleImports<>(List.of(new CatalogueWidget()), CatalogueWidgetModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new Elements.CardBuilder()), Elements.INSTANCE))
                .add(new ModuleImports<>(List.of(new WidgetStyles.wg_scroll()), WidgetStyles.INSTANCE))
                .add(new ModuleImports<>(List.of(new CatalogueStyles.cw_details(), new CatalogueStyles.cw_hint(),
                        new CatalogueStyles.cw_failed(), new CatalogueStyles.cw_hidden()), CatalogueStyles.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<CatalogueDetailsModule> exports() { return new ExportsOf<>(INSTANCE, List.of(new CatalogueDetails())); }
}
