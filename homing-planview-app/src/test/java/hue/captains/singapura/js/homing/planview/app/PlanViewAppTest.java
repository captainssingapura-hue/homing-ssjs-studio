package hue.captains.singapura.js.homing.planview.app;

import hue.captains.singapura.js.homing.catalogue.gate.CatalogueGate;
import hue.captains.singapura.js.homing.core.AppModule;
import hue.captains.singapura.js.homing.core.EsModule;
import hue.captains.singapura.js.homing.docview.reference.ReferencePlans;
import hue.captains.singapura.js.homing.server.HtmlPageContent;
import hue.captains.singapura.js.homing.server.ThemeRegistry;
import hue.captains.singapura.js.homing.site.Path;
import hue.captains.singapura.js.homing.site.Placed;
import hue.captains.singapura.js.homing.site.Query;
import hue.captains.singapura.js.homing.site.Site;
import hue.captains.singapura.js.homing.site.catalogue.CatalogueRouter;
import hue.captains.singapura.js.homing.site.catalogue.L0_Catalogue;
import hue.captains.singapura.js.homing.site.catalogue.Leaf;
import hue.captains.singapura.js.homing.site.mpa.Brand;
import hue.captains.singapura.js.homing.site.mpa.Mpa;
import hue.captains.singapura.js.homing.tree.NodeName;
import hue.captains.singapura.tao.http.action.ActionRegistry;
import io.vertx.ext.web.RoutingContext;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * PlanView, the app: held to the repo's gate, strictly; and a plan placed with it - its page holds
 * the plan, and is PlanView told the plan's authentic path and name.
 */
class PlanViewAppTest {

    private static final PlanViewAppCrate CRATE = PlanViewAppCrate.INSTANCE;

    @Test void theCrateIsStructurallyComplete()             { CatalogueGate.structurallyComplete(CRATE); }
    @Test void everyServedModuleKeepsItsLane_strictly()      { CatalogueGate.strict(CRATE); }
    @Test void theCssGraphKeepsItsLaws()                     { CatalogueGate.cssLaws(CRATE); }
    @Test void everyDesignBindsEveryPairTheSheetWears()      { CatalogueGate.designsBind(CRATE); }
    @Test void keysComeThroughTheParty()                     { CatalogueGate.keysThroughTheParty(CRATE); }
    @Test void everyNameTakenIsImported() throws IOException { CatalogueGate.everyNameTakenIsImported(CRATE); }
    @Test void noPlainModuleImportsADomModule()              { CatalogueGate.noPlainModuleImportsADomModule(CRATE); }
    @Test void theOldStudioIsNowhere()                       { CatalogueGate.noOldStudio(CRATE); }

    /** An MPA that makes a page saying which app it was made for, and with what. */
    record SayingMpa() implements Mpa {
        @Override public Brand brand() { return new Brand("Test", "/"); }
        @Override public ThemeRegistry themes() { throw new UnsupportedOperationException(); }
        @Override public String moduleUrl(EsModule<?> module) { throw new UnsupportedOperationException(); }
        @Override public <P extends AppModule._Param, M extends AppModule<P, M>> Placed page(M app, P params) {
            return (trail, q) -> new HtmlPageContent(app.simpleName() + " " + app.paramCodec().to(params));
        }
        @Override public ActionRegistry<RoutingContext> registry(Site site) { throw new UnsupportedOperationException(); }
    }

    record Plans() implements L0_Catalogue<Plans> {
        static final Plans INSTANCE = new Plans();
        @Override public String name() { return "Plans"; }
        @Override public List<Leaf<Plans>> leaves(Mpa mpa) {
            return ReferencePlans.ALL.entrySet().stream().map(e -> PlanViewLeaves.viewed(this, mpa, new NodeName(e.getKey()), e.getValue())).toList();
        }
    }

    @Test
    void aPlansPage_holdsThePlan_andIsPlanViewToldWhereItIs() {
        var router = CatalogueRouter.at(Path.ROOT, Plans.INSTANCE, new SayingMpa());
        for (var e : ReferencePlans.ALL.entrySet()) {
            String at = "/" + e.getKey();
            var leaf = router.tree().leavesOf(Plans.INSTANCE).stream().filter(l -> l.slug().value().equals(e.getKey())).findFirst().orElseThrow();
            assertSame(e.getValue(), assertInstanceOf(PlanViewPage.class, leaf.page()).plan());
            assertEquals(at, router.hrefOf(leaf.page()).orElseThrow());
            String html = router.resolve(Path.parse(at)).orElseThrow().html(Query.NONE).body();
            assertTrue(html.startsWith("plan-view {"), html);
            assertTrue(html.contains("plan=[" + at + "]") && html.contains("title=[" + e.getValue().name() + "]"), html);
        }
    }
}
