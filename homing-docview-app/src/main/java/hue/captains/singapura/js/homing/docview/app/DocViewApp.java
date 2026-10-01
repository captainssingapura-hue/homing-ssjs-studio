package hue.captains.singapura.js.homing.docview.app;

import hue.captains.singapura.js.homing.component.keyboard.FocusPartyModule;
import hue.captains.singapura.js.homing.component.keyboard.focusParty;
import hue.captains.singapura.js.homing.core.AppModule;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.core.ParamCodec;
import hue.captains.singapura.js.homing.core.QueryString;
import hue.captains.singapura.js.homing.core.js.DomOpsPartyModule;
import hue.captains.singapura.js.homing.core.js.domOpsParty;
import hue.captains.singapura.js.homing.docview.site.DocArrangementModule;
import hue.captains.singapura.js.homing.docview.widgets.CodeContentModule;
import hue.captains.singapura.js.homing.docview.widgets.CodeStewardModule;
import hue.captains.singapura.js.homing.docview.widgets.DiagramContentModule;
import hue.captains.singapura.js.homing.docview.widgets.DiagramStewardModule;
import hue.captains.singapura.js.homing.docview.widgets.DocReferencesModule;
import hue.captains.singapura.js.homing.docview.widgets.ReferencesContentModule;
import hue.captains.singapura.js.homing.docview.widgets.ReferencesStewardModule;
import hue.captains.singapura.js.homing.workspace.stage.StagePartyModule;
import hue.captains.singapura.js.homing.workspace.stage.StageSecretaryModule;
import hue.captains.singapura.js.homing.workspace.stage.StageStewardModule;
import hue.captains.singapura.js.homing.docview.widgets.DocCodeModule;
import hue.captains.singapura.js.homing.docview.widgets.DocImageModule;
import hue.captains.singapura.js.homing.docview.widgets.DocProseModule;
import hue.captains.singapura.js.homing.docview.widgets.DocSourcesModule;
import hue.captains.singapura.js.homing.docview.widgets.DocTableModule;
import hue.captains.singapura.js.homing.docview.widgets.ImageContentModule;
import hue.captains.singapura.js.homing.docview.widgets.ImageStewardModule;
import hue.captains.singapura.js.homing.docview.widgets.ProseContentModule;
import hue.captains.singapura.js.homing.docview.widgets.ProseStewardModule;
import hue.captains.singapura.js.homing.docview.widgets.TableContentModule;
import hue.captains.singapura.js.homing.docview.widgets.TableStewardModule;
import hue.captains.singapura.js.homing.server.HrefManager;
import hue.captains.singapura.js.homing.ui.splitgrid.SplitGridModule;
import hue.captains.singapura.js.homing.workspace.content.ContentSecretaryModule;
import hue.captains.singapura.js.homing.workspace.parties.MessagingPartyModule;
import hue.captains.singapura.js.homing.workspace.tree.TreeLayoutModule;
import hue.captains.singapura.js.homing.workspace.tree.TreeTocModule;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * A doc, viewed, as a page: the same app for every doc, told which by its params - the doc's
 * authentic path. It reads the doc's payload, makes the arrangement from its tree, and lays out the
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
                // the doc, read once; its arrangement, made from its tree
                .add(new ModuleImports<>(List.of(new DocSourcesModule.DocSources()), DocSourcesModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new DocArrangementModule.DocArrangement()), DocArrangementModule.INSTANCE))
                // the content parties: the runtime, their one secretary, their types and stewards
                .add(new ModuleImports<>(List.of(new MessagingPartyModule.MessagingParty()), MessagingPartyModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new ContentSecretaryModule.ContentSecretary()), ContentSecretaryModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new ProseContentModule.PROSE()), ProseContentModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new CodeContentModule.CODE()), CodeContentModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new TableContentModule.TABLE()), TableContentModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new ImageContentModule.IMAGE()), ImageContentModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new ProseStewardModule.ProseSteward()), ProseStewardModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new CodeStewardModule.CodeSteward()), CodeStewardModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new TableStewardModule.TableSteward()), TableStewardModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new ImageStewardModule.ImageSteward()), ImageStewardModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new DiagramContentModule.DIAGRAM()), DiagramContentModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new DiagramStewardModule.DiagramSteward()), DiagramStewardModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new ReferencesContentModule.REFERENCES()), ReferencesContentModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new ReferencesStewardModule.ReferencesSteward()), ReferencesStewardModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new DocReferencesModule.DocReferences()), DocReferencesModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new StagePartyModule.STAGE()), StagePartyModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new StageSecretaryModule.StageSecretary()), StageSecretaryModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new StageStewardModule.StageSteward()), StageStewardModule.INSTANCE))
                // the primitives the tree places
                .add(new ModuleImports<>(List.of(new DocProseModule.DocProse()), DocProseModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new DocCodeModule.DocCode()), DocCodeModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new DocTableModule.DocTable()), DocTableModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new DocImageModule.DocImage()), DocImageModule.INSTANCE))
                // the layout: the tree placement's engine and its contents, in a split grid
                .add(new ModuleImports<>(List.of(new TreeLayoutModule.TreeLayout()), TreeLayoutModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new TreeTocModule.TreeToc()), TreeTocModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new SplitGridModule.SplitGrid()), SplitGridModule.INSTANCE))
                // the fragment, the page's parties, the sheet
                .add(new ModuleImports<>(List.of(new HrefManager.HrefManagerInstance()), HrefManager.INSTANCE))
                .add(new ModuleImports<>(List.of(new domOpsParty()), DomOpsPartyModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new focusParty()), FocusPartyModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new DocViewStyles.dv_page(), new DocViewStyles.dv_shell(), new DocViewStyles.dv_cell(),
                        new DocViewStyles.dv_status(), new DocViewStyles.dv_hidden()), DocViewStyles.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<DocViewApp> exports() { return new ExportsOf<>(INSTANCE, List.of(new appMain())); }
}
