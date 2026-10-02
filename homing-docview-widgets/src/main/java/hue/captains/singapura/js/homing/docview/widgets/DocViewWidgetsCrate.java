package hue.captains.singapura.js.homing.docview.widgets;

import hue.captains.singapura.js.homing.core.Crate;
import hue.captains.singapura.js.homing.core.CrateEntry;
import hue.captains.singapura.js.homing.core.StandardJsModuleType;
import hue.captains.singapura.js.homing.core.js.CoreJsCrate;
import hue.captains.singapura.js.homing.design.DesignCrate;
import hue.captains.singapura.js.homing.libs.LibsCrate;
import hue.captains.singapura.js.homing.server.ServerCrate;
import hue.captains.singapura.js.homing.ui.panzoom.UiPanZoomCrate;
import hue.captains.singapura.js.homing.workspace.content.WorkspaceContentCrate;
import hue.captains.singapura.js.homing.workspace.stage.WorkspaceStageCrate;

import java.util.List;

/**
 * A doc's primitives, as widgets: prose, code - a diagram's drawn beside its source - a table, an
 * image - each made from its type and params alone, asking its content party; their content types,
 * their stewards, and the doc they read from; markdown made into elements; their sheet.
 */
public final class DocViewWidgetsCrate implements Crate {

    public static final DocViewWidgetsCrate INSTANCE = new DocViewWidgetsCrate();

    private DocViewWidgetsCrate() {}

    @Override public String name() { return "homing-docview-widgets"; }

    @Override public List<Crate> requires() {
        return List.of(
                // the DomOpsParty the widgets mint their own from
                CoreJsCrate.INSTANCE,
                // the focus party, the css manager, the href manager
                ServerCrate.INSTANCE,
                // the design words the sheet wears
                DesignCrate.INSTANCE,
                // the content parties' params and secretary
                WorkspaceContentCrate.INSTANCE,
                // marked: markdown's tokens
                LibsCrate.INSTANCE,
                // zoom and pan, for a drawing in place
                UiPanZoomCrate.INSTANCE,
                // the stage: the button a widget offers itself by
                WorkspaceStageCrate.INSTANCE);
    }

    @Override
    public List<CrateEntry> entries() {
        return List.of(
                CrateEntry.of(DocWidgetStyles.INSTANCE),
                // the content types, as the page has them
                CrateEntry.of(ProseContentModule.INSTANCE, StandardJsModuleType.PURE_LOGIC),
                CrateEntry.of(CodeContentModule.INSTANCE, StandardJsModuleType.PURE_LOGIC),
                CrateEntry.of(TableContentModule.INSTANCE, StandardJsModuleType.PURE_LOGIC),
                CrateEntry.of(ImageContentModule.INSTANCE, StandardJsModuleType.PURE_LOGIC),
                CrateEntry.of(DiagramContentModule.INSTANCE, StandardJsModuleType.PURE_LOGIC),
                CrateEntry.of(ReferencesContentModule.INSTANCE, StandardJsModuleType.PURE_LOGIC),
                // the doc they read from, and their stewards
                CrateEntry.of(DocSourcesModule.INSTANCE, StandardJsModuleType.PURE_LOGIC),
                CrateEntry.of(DocPartStewardModule.INSTANCE, StandardJsModuleType.PURE_LOGIC),
                CrateEntry.of(ProseStewardModule.INSTANCE, StandardJsModuleType.PURE_LOGIC),
                CrateEntry.of(CodeStewardModule.INSTANCE, StandardJsModuleType.PURE_LOGIC),
                CrateEntry.of(TableStewardModule.INSTANCE, StandardJsModuleType.PURE_LOGIC),
                CrateEntry.of(ImageStewardModule.INSTANCE, StandardJsModuleType.PURE_LOGIC),
                CrateEntry.of(ReferencesStewardModule.INSTANCE, StandardJsModuleType.PURE_LOGIC),
                // diagrams: where mermaid is, its engine - the library loaded only when a diagram is wanted - and their steward, which draws
                CrateEntry.of(MermaidLibraryModule.INSTANCE, StandardJsModuleType.PURE_LOGIC),
                CrateEntry.of(MermaidEngineModule.INSTANCE, StandardJsModuleType.CONSUMER),
                CrateEntry.of(DiagramStewardModule.INSTANCE, StandardJsModuleType.CONSUMER),
                // markdown made into elements, the base of every primitive, and the primitives
                CrateEntry.of(MarkdownDomModule.INSTANCE, StandardJsModuleType.CONSUMER),
                CrateEntry.of(SvgMarkupModule.INSTANCE, StandardJsModuleType.CONSUMER),
                CrateEntry.of(CodeDiagramModule.INSTANCE, StandardJsModuleType.CONSUMER),
                CrateEntry.of(ContentWidgetModule.INSTANCE, StandardJsModuleType.CONSUMER),
                CrateEntry.of(DocProseModule.INSTANCE, StandardJsModuleType.CONSUMER),
                CrateEntry.of(DocCodeModule.INSTANCE, StandardJsModuleType.CONSUMER),
                CrateEntry.of(DocTableModule.INSTANCE, StandardJsModuleType.CONSUMER),
                CrateEntry.of(DocImageModule.INSTANCE, StandardJsModuleType.CONSUMER),
                // the doc's references, as its last section lists them
                CrateEntry.of(DocReferencesModule.INSTANCE, StandardJsModuleType.CONSUMER));
    }
}
