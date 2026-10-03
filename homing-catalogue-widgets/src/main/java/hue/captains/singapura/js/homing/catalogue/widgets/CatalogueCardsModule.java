package hue.captains.singapura.js.homing.catalogue.widgets;

import hue.captains.singapura.js.homing.catalogue.widgets.CatalogueWidgetModule.CatalogueWidget;
import hue.captains.singapura.js.homing.component.keyboard.KeyBinding;
import hue.captains.singapura.js.homing.core.DomModule;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.server.HrefManager;
import hue.captains.singapura.js.homing.workspace.widgets.SelfContainedWidget;
import hue.captains.singapura.js.homing.workspace.widgets.WidgetStyles;

import java.util.List;

/**
 * One catalogue as cards: {@code new CatalogueCards(container, params)} - its head, then
 * its catalogues and its pages as tiles, each a real link to its authentic path.
 */
public record CatalogueCardsModule() implements DomModule<CatalogueCardsModule> {

    public static final CatalogueCardsModule INSTANCE = new CatalogueCardsModule();

    /** No keys of its own: a press on its head says the reader is reading the catalogue shown. */
    public record CatalogueCards() implements SelfContainedWidget<CatalogueCardsModule> {
        @Override public String summary() { return "One catalogue as cards: its head, then its catalogues and pages as tiles, each opened as its app says."; }
    }

    @Override
    public ImportsFor<CatalogueCardsModule> imports() {
        return ImportsFor.<CatalogueCardsModule>builder()
                .add(new ModuleImports<>(List.of(new CatalogueWidget()), CatalogueWidgetModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new HrefManager.HrefManagerInstance()), HrefManager.INSTANCE))
                .add(new ModuleImports<>(List.of(new WidgetStyles.wg_scroll()), WidgetStyles.INSTANCE))
                .add(new ModuleImports<>(List.of(new CatalogueStyles.cw_cards(), new CatalogueStyles.cw_kicker(),
                        new CatalogueStyles.cw_title(), new CatalogueStyles.cw_summary(), new CatalogueStyles.cw_hint(),
                        new CatalogueStyles.cw_failed(), new CatalogueStyles.cw_hidden(), new CatalogueStyles.cw_section_title(),
                        new CatalogueStyles.cw_grid(), new CatalogueStyles.cw_tile(), new CatalogueStyles.cw_tile_name(),
                        new CatalogueStyles.cw_tile_badge(), new CatalogueStyles.cw_tile_summary()), CatalogueStyles.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<CatalogueCardsModule> exports() { return new ExportsOf<>(INSTANCE, List.of(new CatalogueCards())); }
}
