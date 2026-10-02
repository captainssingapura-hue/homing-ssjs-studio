package hue.captains.singapura.js.homing.catalogue.demo.recipes;

import hue.captains.singapura.js.homing.site.catalogue.Catalogue;
import hue.captains.singapura.js.homing.site.catalogue.L0_Catalogue;
import hue.captains.singapura.js.homing.site.catalogue.L1_Catalogue;
import hue.captains.singapura.js.homing.site.catalogue.Leaf;
import hue.captains.singapura.js.homing.site.mpa.Mpa;

import java.util.List;

/**
 * The recipes app's tree, written alone: soups and noodles. It knows no site -
 * a site grafts it wherever it wants it, may show it under another name, and
 * hands in its MPA; the recipes are made pages of that site with it.
 */
public record RecipesCatalogue() implements L0_Catalogue<RecipesCatalogue> {

    public static final RecipesCatalogue INSTANCE = new RecipesCatalogue();

    @Override public String name() { return "Recipes"; }
    @Override public String summary() { return "Soups and noodles"; }
    @Override public String icon() { return "🍜"; }

    /** A tool beside the recipes: its app opens it beside, not in place of the recipe being read. */
    @Override public List<Leaf<RecipesCatalogue>> leaves(Mpa mpa) {
        return List.of(Leaf.of(this, "Kitchen timer", "Counts a few minutes down, beside the recipe", mpa.page(TimerApp.INSTANCE, new TimerApp.Params("Kitchen timer", 5)))
                .badge("TOOL").icon("⏲️").opens(Leaf.Opening.NEW_TAB));
    }

    @Override public List<? extends L1_Catalogue<RecipesCatalogue, ?>> subCatalogues() {
        return List.of(SoupsCatalogue.INSTANCE, NoodlesCatalogue.INSTANCE);
    }

    /** Soups. */
    public record SoupsCatalogue() implements L1_Catalogue<RecipesCatalogue, SoupsCatalogue> {
        public static final SoupsCatalogue INSTANCE = new SoupsCatalogue();
        @Override public RecipesCatalogue parent() { return RecipesCatalogue.INSTANCE; }
        @Override public String name() { return "Soups"; }
        @Override public List<Leaf<SoupsCatalogue>> leaves(Mpa mpa) {
            return List.of(
                    recipe(this, mpa, "Laksa", 4, List.of("rice noodles", "laksa paste", "coconut milk", "prawns", "tofu puffs"),
                            List.of("Fry the paste until fragrant", "Add stock and coconut milk", "Pour over noodles and toppings")),
                    recipe(this, mpa, "Tom yum", 2, List.of("prawns", "lemongrass", "galangal", "lime leaves", "chilli paste"),
                            List.of("Simmer the aromatics", "Add the prawns", "Season with lime and fish sauce")));
        }
    }

    /** Noodles. */
    public record NoodlesCatalogue() implements L1_Catalogue<RecipesCatalogue, NoodlesCatalogue> {
        public static final NoodlesCatalogue INSTANCE = new NoodlesCatalogue();
        @Override public RecipesCatalogue parent() { return RecipesCatalogue.INSTANCE; }
        @Override public String name() { return "Noodles"; }
        @Override public List<Leaf<NoodlesCatalogue>> leaves(Mpa mpa) {
            return List.of(
                    recipe(this, mpa, "Char kway teow", 2, List.of("flat rice noodles", "cockles", "Chinese sausage", "bean sprouts", "dark soy"),
                            List.of("Heat the wok until it smokes", "Fry the noodles with the sauce", "Toss in the rest, briefly")));
        }
    }

    /** A recipe, placed in {@code host}: its page the recipe app made with the site's {@code mpa}. */
    static <C extends Catalogue<C>> Leaf<C> recipe(C host, Mpa mpa, String name, int serves, List<String> ingredients, List<String> steps) {
        return Leaf.of(host, name, "Serves " + serves, mpa.page(RecipeApp.INSTANCE, new RecipeApp.Params(name, serves, ingredients, steps)))
                   .badge("RECIPE").icon("🥣");
    }
}
