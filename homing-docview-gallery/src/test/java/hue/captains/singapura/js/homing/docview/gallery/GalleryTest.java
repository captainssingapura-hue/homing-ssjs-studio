package hue.captains.singapura.js.homing.docview.gallery;

import hue.captains.singapura.js.homing.catalogue.gate.CatalogueGate;
import hue.captains.singapura.js.homing.docview.site.DocViews;
import hue.captains.singapura.js.homing.docview.tree.Part;
import hue.captains.singapura.js.homing.site.Path;
import hue.captains.singapura.js.homing.site.Query;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The gallery: held to the repo's gate; its pages where it says; and every specimen naming a part
 * of its primitive that its doc has - but the one that names nothing, on purpose.
 */
class GalleryTest {

    private static final DocViewGalleryCrate CRATE = DocViewGalleryCrate.INSTANCE;

    @Test void theCrateIsStructurallyComplete()             { CatalogueGate.structurallyComplete(CRATE); }
    @Test void everyServedModuleKeepsItsLane_strictly()      { CatalogueGate.strict(CRATE); }
    @Test void theCssGraphKeepsItsLaws()                     { CatalogueGate.cssLaws(CRATE); }
    @Test void everyDesignBindsEveryPairTheSheetWears()      { CatalogueGate.designsBind(CRATE); }
    @Test void keysComeThroughTheParty()                     { CatalogueGate.keysThroughTheParty(CRATE); }
    @Test void everyNameTakenIsImported() throws IOException { CatalogueGate.everyNameTakenIsImported(CRATE); }
    @Test void noPlainModuleImportsADomModule()              { CatalogueGate.noPlainModuleImportsADomModule(CRATE); }
    @Test void theOldStudioIsNowhere()                       { CatalogueGate.noOldStudio(CRATE); }

    @Test
    void thePages_eachAtItsKind_andTheDocsAtTheirPaths() {
        for (String at : List.of("/all", "/prose", "/code", "/table", "/image", "/docs/markdown", "/docs/rigid", "/docs/named-rigid", "/docs/composed")) {
            String html = GallerySite.INSTANCE.router().resolve(Path.parse(at)).orElseThrow(() -> new AssertionError("nothing at " + at)).html(Query.NONE).body();
            assertTrue(html.contains("<html") || html.contains("<!DOCTYPE"), at);
        }
    }

    @Test
    void everySpecimen_namesAPartOfItsKindThatItsDocHas_butTheMissingOne() {
        var views = new DocViews(GallerySite.ROUTER);
        var wrong = new ArrayList<String>();
        Specimens.BY_KIND.forEach((kind, specimens) -> {
            for (var s : specimens) {
                var built = views.at(s.doc()).orElseThrow(() -> new AssertionError("no doc at " + s.doc()));
                var part = built.tree().part(s.key());
                boolean missing = s.key().startsWith("nowhere-");
                if (missing && part.isPresent()) wrong.add(kind + " " + s.key() + ": meant to name nothing, names " + part.get().type());
                if (!missing && part.map(Part::type).filter(kind::equals).isEmpty()) {
                    wrong.add(kind + " " + s.doc() + " " + s.key() + ": names " + part.map(Part::type).orElse("nothing"));
                }
            }
        });
        assertEquals(List.of(), wrong);
        assertEquals(List.of("prose", "code", "table", "image"), List.copyOf(Specimens.BY_KIND.keySet()));
    }

    @Test
    void theSpecimensAsThePageHasThem() {
        String js = Specimens.js();
        assertTrue(js.startsWith("const GALLERY_SPECIMENS = Object.freeze({ \"prose\": Object.freeze([Object.freeze({ title: \"An introduction\""), js);
        assertTrue(js.contains("doc: \"/docs/rigid\", key: \"a-picture:0\""), js);
    }
}
