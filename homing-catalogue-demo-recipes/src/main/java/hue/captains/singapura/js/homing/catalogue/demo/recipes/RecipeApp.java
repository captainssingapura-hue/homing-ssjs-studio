package hue.captains.singapura.js.homing.catalogue.demo.recipes;

import hue.captains.singapura.js.homing.core.AppModule;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.core.ParamCodec;
import hue.captains.singapura.js.homing.core.QueryString;
import hue.captains.singapura.js.homing.core.js.DomOpsPartyModule;
import hue.captains.singapura.js.homing.core.js.domOpsParty;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * A recipe, as a page: its name, how many it serves, what goes in, and how -
 * self-contained, its params stamped into the page by whichever site's MPA
 * made it.
 */
public record RecipeApp() implements AppModule<RecipeApp.Params, RecipeApp> {

    public static final RecipeApp INSTANCE = new RecipeApp();

    /** The recipe. */
    public record Params(String name, int serves, List<String> ingredients, List<String> steps) implements AppModule._Param {
        public Params {
            Objects.requireNonNull(name, "Params.name");
            ingredients = List.copyOf(ingredients == null ? List.of() : ingredients);
            steps = List.copyOf(steps == null ? List.of() : steps);
        }
    }

    record appMain() implements AppModule._AppMain<Params, RecipeApp> {}

    public static final ParamCodec<Params> CODEC = new ParamCodec<>() {
        @Override public Decoded<Params> from(Map<String, List<String>> query) {
            String name = QueryString.first(query, "name"), serves = QueryString.first(query, "serves");
            int n;
            try { n = serves == null ? 0 : Integer.parseInt(serves); } catch (NumberFormatException e) { n = 0; }
            return Decoded.ok(new Params(name == null ? "" : name, n, query.getOrDefault("ingredient", List.of()), query.getOrDefault("step", List.of())));
        }
        @Override public Map<String, List<String>> to(Params params) {
            var out = QueryString.params();
            QueryString.put(out, "name", params.name());
            QueryString.put(out, "serves", String.valueOf(params.serves()));
            params.ingredients().forEach(i -> QueryString.put(out, "ingredient", i));
            params.steps().forEach(s -> QueryString.put(out, "step", s));
            return out;
        }
    };

    @Override public String title()      { return "Recipe"; }
    @Override public String simpleName() { return "recipe"; }
    @Override public Class<Params> paramsType() { return Params.class; }
    @Override public ParamCodec<Params> paramCodec() { return CODEC; }

    @Override
    public ImportsFor<RecipeApp> imports() {
        return ImportsFor.<RecipeApp>builder()
                .add(new ModuleImports<>(List.of(new domOpsParty()), DomOpsPartyModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new RecipeStyles.rc_root(), new RecipeStyles.rc_title(), new RecipeStyles.rc_serves(),
                        new RecipeStyles.rc_heading(), new RecipeStyles.rc_list()), RecipeStyles.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<RecipeApp> exports() { return new ExportsOf<>(INSTANCE, List.of(new appMain())); }
}
