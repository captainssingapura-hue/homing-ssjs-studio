package hue.captains.singapura.js.homing.docview.app;

import hue.captains.singapura.js.homing.component.keyboard.FocusPartyModule;
import hue.captains.singapura.js.homing.component.keyboard.focusParty;
import hue.captains.singapura.js.homing.core.DomModule;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.Exportable;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.core.js.DomOpsPartyModule;
import hue.captains.singapura.js.homing.core.js.domOpsParty;
import hue.captains.singapura.js.homing.docview.site.DocArrangementModule;
import hue.captains.singapura.js.homing.docview.widgets.CodeContentModule;
import hue.captains.singapura.js.homing.docview.widgets.CodeStewardModule;
import hue.captains.singapura.js.homing.docview.widgets.DiagramContentModule;
import hue.captains.singapura.js.homing.docview.widgets.DiagramStewardModule;
import hue.captains.singapura.js.homing.docview.widgets.DocCodeModule;
import hue.captains.singapura.js.homing.docview.widgets.DocImageModule;
import hue.captains.singapura.js.homing.docview.widgets.DocProseModule;
import hue.captains.singapura.js.homing.docview.widgets.DocReferencesModule;
import hue.captains.singapura.js.homing.docview.widgets.DocSourcesModule;
import hue.captains.singapura.js.homing.docview.widgets.DocTableModule;
import hue.captains.singapura.js.homing.docview.widgets.ImageContentModule;
import hue.captains.singapura.js.homing.docview.widgets.ImageStewardModule;
import hue.captains.singapura.js.homing.docview.widgets.ProseContentModule;
import hue.captains.singapura.js.homing.docview.widgets.ProseStewardModule;
import hue.captains.singapura.js.homing.docview.widgets.ReferencesContentModule;
import hue.captains.singapura.js.homing.docview.widgets.ReferencesStewardModule;
import hue.captains.singapura.js.homing.docview.widgets.TableContentModule;
import hue.captains.singapura.js.homing.docview.widgets.TableStewardModule;
import hue.captains.singapura.js.homing.server.HrefManager;
import hue.captains.singapura.js.homing.ui.splitgrid.SplitGridModule;
import hue.captains.singapura.js.homing.workspace.content.ContentSecretaryModule;
import hue.captains.singapura.js.homing.workspace.parties.MessagingPartyModule;
import hue.captains.singapura.js.homing.workspace.stage.StagePartyModule;
import hue.captains.singapura.js.homing.workspace.stage.StageSecretaryModule;
import hue.captains.singapura.js.homing.workspace.stage.StageStewardModule;
import hue.captains.singapura.js.homing.workspace.tree.TreeLayoutModule;
import hue.captains.singapura.js.homing.workspace.tree.TreeTocModule;

import java.util.List;

/**
 * The desk a doc is read on, and a plan: {@code DocDesk.open(el, address, title, extra)} - the
 * payload of what is at the address read, its tree laid out with the contents beside it, every
 * part a widget asking its content party; the references its last section; one stage. It knows a
 * doc's primitives; an app reading other trees offers its own widget types and parties beside them.
 */
public record DocDeskModule() implements DomModule<DocDeskModule> {

    public static final DocDeskModule INSTANCE = new DocDeskModule();

    public record DocDesk() implements Exportable._Class<DocDeskModule> {}

    @Override
    public ImportsFor<DocDeskModule> imports() {
        return ImportsFor.<DocDeskModule>builder()
                // the payload, read once; its arrangement, made from its tree
                .add(new ModuleImports<>(List.of(new DocSourcesModule.DocSources()), DocSourcesModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new DocArrangementModule.DocArrangement()), DocArrangementModule.INSTANCE))
                // the content parties: the runtime, their one secretary, a doc's types and stewards
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
                // the stage: its party, its secretary, its steward
                .add(new ModuleImports<>(List.of(new StagePartyModule.STAGE()), StagePartyModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new StageSecretaryModule.StageSecretary()), StageSecretaryModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new StageStewardModule.StageSteward()), StageStewardModule.INSTANCE))
                // a doc's primitives, and the list of its references
                .add(new ModuleImports<>(List.of(new DocProseModule.DocProse()), DocProseModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new DocCodeModule.DocCode()), DocCodeModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new DocTableModule.DocTable()), DocTableModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new DocImageModule.DocImage()), DocImageModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new DocReferencesModule.DocReferences()), DocReferencesModule.INSTANCE))
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
    public ExportsOf<DocDeskModule> exports() { return new ExportsOf<>(INSTANCE, List.of(new DocDesk())); }
}
