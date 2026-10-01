package hue.captains.singapura.js.homing.docview.app;

import hue.captains.singapura.js.homing.core.AppModule;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.core.ParamCodec;
import hue.captains.singapura.js.homing.core.QueryString;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * A doc, viewed, as a page: the same app for every doc, told which by its params - the doc's
 * authentic path. The desk reads the doc's payload, makes the arrangement from its tree, and lays out the
 * contents beside the doc, every part's widget mounted at once and asking its content party.
 */
public record DocViewApp() implements AppModule<DocViewApp.Params, DocViewApp> {

    public static final DocViewApp INSTANCE = new DocViewApp();

    /**
     * The doc to view: its authentic path, and its title.
     *
     * @param doc   the authentic path, empty when the page is reached without one
     * @param title the doc's title, said while it is read
     */
    public record Params(String doc, String title) implements AppModule._Param {
        public Params {
            if (doc == null) doc = "";
            Objects.requireNonNull(title, "Params.title");
        }
    }

    record appMain() implements AppModule._AppMain<Params, DocViewApp> {}

    public static final ParamCodec<Params> CODEC = new ParamCodec<>() {
        @Override public Decoded<Params> from(Map<String, List<String>> query) {
            String title = QueryString.first(query, "title");
            return Decoded.ok(new Params(QueryString.first(query, "doc"), title == null ? "" : title));
        }
        @Override public Map<String, List<String>> to(Params params) {
            var out = QueryString.params();
            QueryString.put(out, "doc", params.doc());
            QueryString.put(out, "title", params.title());
            return out;
        }
    };

    @Override public String title()      { return "DocView"; }
    @Override public String simpleName() { return "doc-view"; }
    @Override public Class<Params> paramsType() { return Params.class; }
    @Override public ParamCodec<Params> paramCodec() { return CODEC; }

    @Override
    public ImportsFor<DocViewApp> imports() {
        return ImportsFor.<DocViewApp>builder()
                // the desk, which reads the doc and lays it out
                .add(new ModuleImports<>(List.of(new DocDeskModule.DocDesk()), DocDeskModule.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<DocViewApp> exports() { return new ExportsOf<>(INSTANCE, List.of(new appMain())); }
}
