package hue.captains.singapura.js.homing.catalogue.demo.notes;

import hue.captains.singapura.js.homing.catalogue.gate.CatalogueGate;
import org.junit.jupiter.api.Test;

import java.io.IOException;

/** the notes app's gate: its page and its sheet, held to conformance, strictly - and the studio's old stack nowhere. */
class NotesGateTest {

    private static final NotesCrate CRATE = NotesCrate.INSTANCE;

    @Test void theCrateIsStructurallyComplete()             { CatalogueGate.structurallyComplete(CRATE); }
    @Test void everyServedModuleKeepsItsLane_strictly()      { CatalogueGate.strict(CRATE); }
    @Test void theCssGraphKeepsItsLaws()                     { CatalogueGate.cssLaws(CRATE); }
    @Test void everyDesignBindsEveryPairTheSheetWears()      { CatalogueGate.designsBind(CRATE); }
    @Test void keysComeThroughTheParty()                     { CatalogueGate.keysThroughTheParty(CRATE); }
    @Test void everyNameTakenIsImported() throws IOException { CatalogueGate.everyNameTakenIsImported(CRATE); }
    @Test void noPlainModuleImportsADomModule()              { CatalogueGate.noPlainModuleImportsADomModule(CRATE); }
    @Test void theOldStudioIsNowhere()                       { CatalogueGate.noOldStudio(CRATE); }
}
