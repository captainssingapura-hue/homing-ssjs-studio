package hue.captains.singapura.js.homing.catalogue.widgets;

import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.workspace.parties.PartyType;
import hue.captains.singapura.js.homing.workspace.widgets.NoParams;
import hue.captains.singapura.js.homing.workspace.widgets.WidgetDeclaration;
import hue.captains.singapura.js.homing.workspace.widgets.WidgetQuery;

import java.util.List;

/**
 * The catalogue widgets, declared: each joins the catalogue party - the browser for its
 * subordinates, whose party it is linked to. Any workspace may offer them among its kinds.
 */
public final class CatalogueWidgetDeclarations {

    private CatalogueWidgetDeclarations() {}

    private static final List<PartyType<?>> CATALOGUE = List.of(CatalogueChoice.TYPE);

    /** {@code catalogue-tree}: from a catalogue down, as a tree. */
    public record Tree() implements WidgetDeclaration<CatalogueAt> {
        public static final Tree INSTANCE = new Tree();
        @Override public String kind() { return "catalogue-tree"; }
        @Override public Class<CatalogueAt> paramsType() { return CatalogueAt.class; }
        @Override public WidgetQuery<CatalogueAt> query() { return new CatalogueAt.Query(); }
        @Override public List<PartyType<?>> parties() { return CATALOGUE; }
        @Override public ModuleImports<?> constructs() {
            return new ModuleImports<>(List.of(new CatalogueTreeModule.CatalogueTree()), CatalogueTreeModule.INSTANCE);
        }
    }

    /** {@code catalogue-details}: the entry picked, as a card. */
    public record Details() implements WidgetDeclaration<NoParams> {
        public static final Details INSTANCE = new Details();
        @Override public String kind() { return "catalogue-details"; }
        @Override public Class<NoParams> paramsType() { return NoParams.class; }
        @Override public WidgetQuery<NoParams> query() { return new NoParams.Query(); }
        @Override public List<PartyType<?>> parties() { return CATALOGUE; }
        @Override public ModuleImports<?> constructs() {
            return new ModuleImports<>(List.of(new CatalogueDetailsModule.CatalogueDetails()), CatalogueDetailsModule.INSTANCE);
        }
    }

    /** {@code catalogue-cards}: one catalogue as cards, drilling in as catalogues are picked. */
    public record Cards() implements WidgetDeclaration<CatalogueAt> {
        public static final Cards INSTANCE = new Cards();
        @Override public String kind() { return "catalogue-cards"; }
        @Override public Class<CatalogueAt> paramsType() { return CatalogueAt.class; }
        @Override public WidgetQuery<CatalogueAt> query() { return new CatalogueAt.Query(); }
        @Override public List<PartyType<?>> parties() { return CATALOGUE; }
        @Override public ModuleImports<?> constructs() {
            return new ModuleImports<>(List.of(new CatalogueCardsModule.CatalogueCards()), CatalogueCardsModule.INSTANCE);
        }
    }

    /** {@code catalogue-browser}: the tree and the details composed; its parties its subordinates'. */
    public record Browser() implements WidgetDeclaration<CatalogueAt> {
        public static final Browser INSTANCE = new Browser();
        @Override public String kind() { return "catalogue-browser"; }
        @Override public Class<CatalogueAt> paramsType() { return CatalogueAt.class; }
        @Override public WidgetQuery<CatalogueAt> query() { return new CatalogueAt.Query(); }
        @Override public List<PartyType<?>> parties() {
            return java.util.stream.Stream.of(Tree.INSTANCE.parties(), Details.INSTANCE.parties()).flatMap(List::stream).distinct().toList();
        }
        @Override public ModuleImports<?> constructs() {
            return new ModuleImports<>(List.of(new CatalogueBrowserModule.CatalogueBrowser()), CatalogueBrowserModule.INSTANCE);
        }
    }

    /** Every catalogue widget kind, for a workspace that offers them. */
    public static final List<WidgetDeclaration<?>> KINDS = List.of(Tree.INSTANCE, Details.INSTANCE, Cards.INSTANCE, Browser.INSTANCE);
}
