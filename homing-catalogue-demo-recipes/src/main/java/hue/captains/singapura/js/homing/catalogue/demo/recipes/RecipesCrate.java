package hue.captains.singapura.js.homing.catalogue.demo.recipes;

import hue.captains.singapura.js.homing.core.Crate;
import hue.captains.singapura.js.homing.core.CrateEntry;
import hue.captains.singapura.js.homing.core.StandardJsModuleType;
import hue.captains.singapura.js.homing.core.js.CoreJsCrate;
import hue.captains.singapura.js.homing.design.DesignCrate;
import hue.captains.singapura.js.homing.server.ServerCrate;
import hue.captains.singapura.js.homing.ui.elements.UiElementsCrate;

import java.util.List;

/** The recipes app: its page and its sheet. Its tree is Java, and served as the pages it places. */
public final class RecipesCrate implements Crate {

    public static final RecipesCrate INSTANCE = new RecipesCrate();

    private RecipesCrate() {}

    @Override public String name() { return "homing-catalogue-demo-recipes"; }

    @Override public List<Crate> requires() { return List.of(CoreJsCrate.INSTANCE, ServerCrate.INSTANCE, DesignCrate.INSTANCE, UiElementsCrate.INSTANCE); }

    @Override
    public List<CrateEntry> entries() {
        return List.of(CrateEntry.of(RecipeStyles.INSTANCE), CrateEntry.of(RecipeApp.INSTANCE, StandardJsModuleType.CONSUMER),
                CrateEntry.of(TimerApp.INSTANCE, StandardJsModuleType.CONSUMER));
    }
}
