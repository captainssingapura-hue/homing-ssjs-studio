package hue.captains.singapura.js.homing.docview.site;

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
 * A doc's first stage, seen whole, as a page: its tree - every heading, its name in the path,
 * the parts of its leaf - its arrangement - every widget, its type and its params - and the
 * payload a page loads, as it comes. It asks for the doc by the address it is at.
 */
public record DocInspectorApp() implements AppModule<DocInspectorApp.Params, DocInspectorApp> {

    public static final DocInspectorApp INSTANCE = new DocInspectorApp();

    /**
     * The doc to inspect: its authentic path - where the page is - and its title.
     *
     * @param doc   the authentic path, empty when the page is reached without one
     * @param title the doc's title
     */
    public record Params(String doc, String title) implements AppModule._Param {
        public Params {
            if (doc == null) doc = "";
            Objects.requireNonNull(title, "Params.title");
        }
    }

    record appMain() implements AppModule._AppMain<Params, DocInspectorApp> {}

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

    @Override public String title()      { return "Doc inspector"; }
    @Override public String simpleName() { return "doc-inspector"; }
    @Override public Class<Params> paramsType() { return Params.class; }
    @Override public ParamCodec<Params> paramCodec() { return CODEC; }

    @Override
    public ImportsFor<DocInspectorApp> imports() {
        return ImportsFor.<DocInspectorApp>builder()
                .add(new ModuleImports<>(List.of(new domOpsParty()), DomOpsPartyModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new InspectorStyles.di_page(), new InspectorStyles.di_column(), new InspectorStyles.di_title(), new InspectorStyles.di_muted(),
                        new InspectorStyles.di_code(), new InspectorStyles.di_sections(), new InspectorStyles.di_section(), new InspectorStyles.di_heading(),
                        new InspectorStyles.di_tree(), new InspectorStyles.di_node(), new InspectorStyles.di_chips(), new InspectorStyles.di_chip(),
                        new InspectorStyles.di_table(), new InspectorStyles.di_cell(), new InspectorStyles.di_pre(), new InspectorStyles.di_failed()),
                        InspectorStyles.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<DocInspectorApp> exports() { return new ExportsOf<>(INSTANCE, List.of(new appMain())); }
}
