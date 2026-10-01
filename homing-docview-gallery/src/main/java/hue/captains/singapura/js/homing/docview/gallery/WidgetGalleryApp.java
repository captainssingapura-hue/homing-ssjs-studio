package hue.captains.singapura.js.homing.docview.gallery;

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
import hue.captains.singapura.js.homing.docview.widgets.CodeContentModule;
import hue.captains.singapura.js.homing.docview.widgets.CodeStewardModule;
import hue.captains.singapura.js.homing.docview.widgets.DiagramContentModule;
import hue.captains.singapura.js.homing.docview.widgets.DiagramStewardModule;
import hue.captains.singapura.js.homing.docview.widgets.DocCodeModule;
import hue.captains.singapura.js.homing.docview.widgets.DocImageModule;
import hue.captains.singapura.js.homing.docview.widgets.DocProseModule;
import hue.captains.singapura.js.homing.docview.widgets.DocTableModule;
import hue.captains.singapura.js.homing.docview.widgets.ImageContentModule;
import hue.captains.singapura.js.homing.docview.widgets.ImageStewardModule;
import hue.captains.singapura.js.homing.docview.widgets.ProseContentModule;
import hue.captains.singapura.js.homing.docview.widgets.ProseStewardModule;
import hue.captains.singapura.js.homing.docview.widgets.TableContentModule;
import hue.captains.singapura.js.homing.docview.widgets.TableStewardModule;
import hue.captains.singapura.js.homing.workspace.content.ContentSecretaryModule;
import hue.captains.singapura.js.homing.workspace.parties.MessagingPartyModule;

import java.util.List;
import java.util.Map;

/**
 * A primitive's gallery, as a page: its specimens, each a widget made from its type and its
 * params alone - the doc's address and the part's key - joined to the page's content parties,
 * whose stewards read the docs. {@code all} shows every primitive's.
 */
public record WidgetGalleryApp() implements AppModule<WidgetGalleryApp.Params, WidgetGalleryApp> {

    public static final WidgetGalleryApp INSTANCE = new WidgetGalleryApp();

    /** Which primitive's specimens: prose, code, table, image - or all. */
    public record Params(String kind) implements AppModule._Param {
        public Params { if (kind == null || kind.isBlank()) kind = "all"; }
    }

    record appMain() implements AppModule._AppMain<Params, WidgetGalleryApp> {}

    public static final ParamCodec<Params> CODEC = new ParamCodec<>() {
        @Override public Decoded<Params> from(Map<String, List<String>> query) { return Decoded.ok(new Params(QueryString.first(query, "kind"))); }
        @Override public Map<String, List<String>> to(Params params) {
            var out = QueryString.params();
            QueryString.put(out, "kind", params.kind());
            return out;
        }
    };

    @Override public String title()      { return "DocView widgets"; }
    @Override public String simpleName() { return "docview-widget-gallery"; }
    @Override public Class<Params> paramsType() { return Params.class; }
    @Override public ParamCodec<Params> paramCodec() { return CODEC; }

    @Override
    public ImportsFor<WidgetGalleryApp> imports() {
        return ImportsFor.<WidgetGalleryApp>builder()
                // the page's content parties: the runtime, their one secretary, their types and stewards
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
                // the primitives, and what they are shown with
                .add(new ModuleImports<>(List.of(new DocProseModule.DocProse()), DocProseModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new DocCodeModule.DocCode()), DocCodeModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new DocTableModule.DocTable()), DocTableModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new DocImageModule.DocImage()), DocImageModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new SpecimensModule.GALLERY_SPECIMENS()), SpecimensModule.INSTANCE))
                // the page's own parties, where the widgets are grafted
                .add(new ModuleImports<>(List.of(new domOpsParty()), DomOpsPartyModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new focusParty()), FocusPartyModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new GalleryStyles.gl_page(), new GalleryStyles.gl_intro(), new GalleryStyles.gl_kind(),
                        new GalleryStyles.gl_kind_title(), new GalleryStyles.gl_card(), new GalleryStyles.gl_title(), new GalleryStyles.gl_params(),
                        new GalleryStyles.gl_note()), GalleryStyles.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<WidgetGalleryApp> exports() { return new ExportsOf<>(INSTANCE, List.of(new appMain())); }
}
