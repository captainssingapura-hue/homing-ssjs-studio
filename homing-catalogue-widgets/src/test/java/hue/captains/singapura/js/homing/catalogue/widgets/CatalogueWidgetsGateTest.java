package hue.captains.singapura.js.homing.catalogue.widgets;

import hue.captains.singapura.js.homing.catalogue.gate.CatalogueGate;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** homing-catalogue-widgets' gate: the widgets, their party and their sheet, held to conformance, strictly. */
class CatalogueWidgetsGateTest {

    private static final CatalogueWidgetsCrate CRATE = CatalogueWidgetsCrate.INSTANCE;

    @Test void theCrateIsStructurallyComplete()             { CatalogueGate.structurallyComplete(CRATE); }
    @Test void everyServedModuleKeepsItsLane_strictly()      { CatalogueGate.strict(CRATE); }
    @Test void theCssGraphKeepsItsLaws()                     { CatalogueGate.cssLaws(CRATE); }
    @Test void everyDesignBindsEveryPairTheSheetWears()      { CatalogueGate.designsBind(CRATE); }
    @Test void keysComeThroughTheParty()                     { CatalogueGate.keysThroughTheParty(CRATE); }
    @Test void everyNameTakenIsImported() throws IOException { CatalogueGate.everyNameTakenIsImported(CRATE); }
    @Test void noPlainModuleImportsADomModule()              { CatalogueGate.noPlainModuleImportsADomModule(CRATE); }
    @Test void theOldStudioIsNowhere()                       { CatalogueGate.noOldStudio(CRATE); }

    @Test
    void theKindsAreDeclared_eachJoiningTheCatalogueParty() {
        assertEquals("catalogue-tree catalogue-details catalogue-cards catalogue-browser",
                String.join(" ", CatalogueWidgetDeclarations.KINDS.stream().map(k -> k.kind()).toList()));
        CatalogueWidgetDeclarations.KINDS.forEach(k -> assertEquals(java.util.List.of(CatalogueChoice.TYPE), k.parties(), k.kind()));
        assertEquals("CatalogueBrowser", CatalogueWidgetDeclarations.Browser.INSTANCE.className());
    }

    @Test
    void anAtIsReadFromAnAddress_andWrittenBack() {
        var q = new CatalogueAt.Query();
        assertEquals(new hue.captains.singapura.js.homing.workspace.widgets.WidgetQuery.Read.Ok<>(CatalogueAt.ROOT), q.from(java.util.Map.of()));
        assertEquals(new hue.captains.singapura.js.homing.workspace.widgets.WidgetQuery.Read.Ok<>(new CatalogueAt("/kitchen")), q.from(java.util.Map.of("at", java.util.List.of("/kitchen"))));
        assertEquals(java.util.Map.of(), q.to(CatalogueAt.ROOT));
        assertEquals(java.util.List.of("/kitchen"), q.to(new CatalogueAt("/kitchen")).get("at"));
    }
}
