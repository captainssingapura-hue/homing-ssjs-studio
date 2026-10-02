package hue.captains.singapura.js.homing.catalogue.widgets;

import hue.captains.singapura.js.homing.core.Crate;
import hue.captains.singapura.js.homing.core.CrateEntry;
import hue.captains.singapura.js.homing.core.StandardJsModuleType;
import hue.captains.singapura.js.homing.core.js.CoreJsCrate;
import hue.captains.singapura.js.homing.design.DesignCrate;
import hue.captains.singapura.js.homing.relgrid.protocol.RelGridProtocolCrate;
import hue.captains.singapura.js.homing.reltree.RelTreeCrate;
import hue.captains.singapura.js.homing.server.ServerCrate;
import hue.captains.singapura.js.homing.ui.elements.UiElementsCrate;
import hue.captains.singapura.js.homing.workspace.parties.WorkspacePartiesCrate;
import hue.captains.singapura.js.homing.workspace.widgets.WorkspaceWidgetsCrate;

import java.util.List;

/**
 * The catalogue widgets: a site's catalogue as a tree, the entry picked as a card, a
 * catalogue as cards, and the tree and the card composed - and the party they meet in.
 * They read a site's catalogue from its entry route, and know no app it places.
 */
public final class CatalogueWidgetsCrate implements Crate {

    public static final CatalogueWidgetsCrate INSTANCE = new CatalogueWidgetsCrate();

    private CatalogueWidgetsCrate() {}

    @Override public String name() { return "homing-catalogue-widgets"; }

    @Override public List<Crate> requires() {
        return List.of(
                // the DomOpsParty a widget mints its root from
                CoreJsCrate.INSTANCE,
                // the focus party, the keys' convention, the css and href managers
                ServerCrate.INSTANCE,
                // the design words the sheet wears
                DesignCrate.INSTANCE,
                // the tree's relation tree, and its questions
                RelTreeCrate.INSTANCE, RelGridProtocolCrate.INSTANCE,
                // the details' card
                UiElementsCrate.INSTANCE,
                // the messaging parties' runtime: the browser's own scope
                WorkspacePartiesCrate.INSTANCE,
                // what a widget is: the sheet it fills its container by
                WorkspaceWidgetsCrate.INSTANCE);
    }

    @Override
    public List<CrateEntry> entries() {
        return List.of(
                // the catalogue party: its type, generated from Java, and its secretaries
                CrateEntry.of(CatalogueChoiceModule.INSTANCE, StandardJsModuleType.PURE_LOGIC),
                CrateEntry.of(CatalogueChoiceSecretaryModule.INSTANCE, StandardJsModuleType.SECRETARY),
                CrateEntry.of(CatalogueScopeSecretaryModule.INSTANCE, StandardJsModuleType.SECRETARY),
                // a site's catalogue, read entry by entry
                CrateEntry.of(CatalogueEntriesModule.INSTANCE, StandardJsModuleType.PURE_LOGIC),
                CrateEntry.of(CatalogueStyles.INSTANCE),
                // what every catalogue widget is to its host
                CrateEntry.of(CatalogueWidgetModule.INSTANCE),
                // the widgets
                CrateEntry.of(CatalogueRowCellModule.INSTANCE),
                CrateEntry.of(CatalogueTreeModule.INSTANCE),
                CrateEntry.of(CatalogueDetailsModule.INSTANCE),
                CrateEntry.of(CatalogueCardsModule.INSTANCE),
                CrateEntry.of(CatalogueBrowserModule.INSTANCE));
    }
}
