package hue.captains.singapura.js.homing.catalogue.demo.recipes;

import hue.captains.singapura.js.homing.core.AppModule;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.core.ParamCodec;
import hue.captains.singapura.js.homing.core.QueryString;
import hue.captains.singapura.js.homing.core.js.DomOpsPartyModule;
import hue.captains.singapura.js.homing.core.js.domOpsParty;
import hue.captains.singapura.js.homing.ui.elements.Elements;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * A kitchen timer, as a page: a few minutes counted down, started, paused and reset. A
 * tool that is used beside a recipe, not instead of it - so its leaf says it opens beside.
 */
public record TimerApp() implements AppModule<TimerApp.Params, TimerApp> {

    public static final TimerApp INSTANCE = new TimerApp();

    /** What the timer is for, and how many minutes it counts. */
    public record Params(String label, int minutes) implements AppModule._Param {
        public Params {
            Objects.requireNonNull(label, "Params.label");
            if (minutes < 1) throw new IllegalArgumentException("Params.minutes " + minutes + ": at least one");
        }
    }

    record appMain() implements AppModule._AppMain<Params, TimerApp> {}

    public static final ParamCodec<Params> CODEC = new ParamCodec<>() {
        @Override public Decoded<Params> from(Map<String, List<String>> query) {
            String label = QueryString.first(query, "label"), minutes = QueryString.first(query, "minutes");
            int n;
            try { n = minutes == null ? 5 : Integer.parseInt(minutes); } catch (NumberFormatException e) { n = 5; }
            return Decoded.ok(new Params(label == null ? "Timer" : label, Math.max(1, n)));
        }
        @Override public Map<String, List<String>> to(Params params) {
            var out = QueryString.params();
            QueryString.put(out, "label", params.label());
            QueryString.put(out, "minutes", String.valueOf(params.minutes()));
            return out;
        }
    };

    @Override public String title()      { return "Timer"; }
    @Override public String simpleName() { return "timer"; }
    @Override public Class<Params> paramsType() { return Params.class; }
    @Override public ParamCodec<Params> paramCodec() { return CODEC; }

    @Override
    public ImportsFor<TimerApp> imports() {
        return ImportsFor.<TimerApp>builder()
                .add(new ModuleImports<>(List.of(new domOpsParty()), DomOpsPartyModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new Elements.ButtonBuilder()), Elements.INSTANCE))
                .add(new ModuleImports<>(List.of(new RecipeStyles.rc_root(), new RecipeStyles.rc_title(), new RecipeStyles.rc_serves(),
                        new RecipeStyles.rc_clock(), new RecipeStyles.rc_row()), RecipeStyles.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<TimerApp> exports() { return new ExportsOf<>(INSTANCE, List.of(new appMain())); }
}
