package hue.captains.singapura.js.homing.catalogue.demo.notes;

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
 * A note, as a page: its title, when it was written, and its text, a paragraph
 * to each blank line. Self-contained: what it shows is its params, stamped
 * into the page by whichever site's MPA made it.
 */
public record NoteApp() implements AppModule<NoteApp.Params, NoteApp> {

    public static final NoteApp INSTANCE = new NoteApp();

    /** The note: its title, when, and its text. */
    public record Params(String title, String when, String text) implements AppModule._Param {
        public Params {
            Objects.requireNonNull(title, "Params.title");
            if (when == null) when = "";
            if (text == null) text = "";
        }
    }

    record appMain() implements AppModule._AppMain<Params, NoteApp> {}

    public static final ParamCodec<Params> CODEC = new ParamCodec<>() {
        @Override public Decoded<Params> from(Map<String, List<String>> query) {
            String title = QueryString.first(query, "title");
            return Decoded.ok(new Params(title == null ? "" : title, QueryString.first(query, "when"), QueryString.first(query, "text")));
        }
        @Override public Map<String, List<String>> to(Params params) {
            var out = QueryString.params();
            QueryString.put(out, "title", params.title());
            QueryString.put(out, "when", params.when());
            QueryString.put(out, "text", params.text());
            return out;
        }
    };

    @Override public String title()      { return "Note"; }
    @Override public String simpleName() { return "note"; }
    @Override public Class<Params> paramsType() { return Params.class; }
    @Override public ParamCodec<Params> paramCodec() { return CODEC; }

    @Override
    public ImportsFor<NoteApp> imports() {
        return ImportsFor.<NoteApp>builder()
                .add(new ModuleImports<>(List.of(new domOpsParty()), DomOpsPartyModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new NoteStyles.nt_root(), new NoteStyles.nt_title(), new NoteStyles.nt_when(),
                        new NoteStyles.nt_para()), NoteStyles.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<NoteApp> exports() { return new ExportsOf<>(INSTANCE, List.of(new appMain())); }
}
