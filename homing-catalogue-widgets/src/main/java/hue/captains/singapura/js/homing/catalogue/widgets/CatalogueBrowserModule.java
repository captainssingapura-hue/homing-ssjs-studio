package hue.captains.singapura.js.homing.catalogue.widgets;

import hue.captains.singapura.js.homing.component.keyboard.FocusPartyModule;
import hue.captains.singapura.js.homing.component.keyboard.focusParties;
import hue.captains.singapura.js.homing.core.DomModule;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.core.js.DomOpsPartyModule;
import hue.captains.singapura.js.homing.core.js.domOpsParties;
import hue.captains.singapura.js.homing.workspace.parties.MessagingPartyModule;
import hue.captains.singapura.js.homing.workspace.widgets.SelfContainedWidget;
import hue.captains.singapura.js.homing.workspace.widgets.WidgetStyles;

import java.util.List;

/**
 * A site's catalogue browsed: {@code new CatalogueBrowser(container, params)} - the tree
 * and the details of what is picked, composed: each lent a box and grafted, meeting in a
 * catalogue party of the browser's own, linked to the one it is given.
 */
public record CatalogueBrowserModule() implements DomModule<CatalogueBrowserModule> {

    public static final CatalogueBrowserModule INSTANCE = new CatalogueBrowserModule();

    public record CatalogueBrowser() implements SelfContainedWidget<CatalogueBrowserModule> {
        @Override public String summary() { return "A site's catalogue browsed: the tree and the details of what is picked, side by side, meeting in a scope of its own."; }
    }

    @Override
    public ImportsFor<CatalogueBrowserModule> imports() {
        return ImportsFor.<CatalogueBrowserModule>builder()
                .add(new ModuleImports<>(List.of(new domOpsParties()), DomOpsPartyModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new focusParties()), FocusPartyModule.INSTANCE))
                // its subordinates
                .add(new ModuleImports<>(List.of(new CatalogueTreeModule.CatalogueTree()), CatalogueTreeModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new CatalogueDetailsModule.CatalogueDetails()), CatalogueDetailsModule.INSTANCE))
                // its scope: a catalogue party, and the secretary that keeps its edge
                .add(new ModuleImports<>(List.of(new MessagingPartyModule.MessagingParty()), MessagingPartyModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new CatalogueChoiceModule.CATALOGUE()), CatalogueChoiceModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new CatalogueScopeSecretaryModule.CatalogueScopeSecretary()), CatalogueScopeSecretaryModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new WidgetStyles.wg_fill()), WidgetStyles.INSTANCE))
                .add(new ModuleImports<>(List.of(new CatalogueStyles.cw_browser(), new CatalogueStyles.cw_pane(),
                        new CatalogueStyles.cw_pane_tree(), new CatalogueStyles.cw_pane_details()), CatalogueStyles.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<CatalogueBrowserModule> exports() { return new ExportsOf<>(INSTANCE, List.of(new CatalogueBrowser())); }
}
