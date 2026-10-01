package hue.captains.singapura.js.homing.docview.site;

import hue.captains.singapura.js.homing.docview.reference.MarkdownReference;
import hue.captains.singapura.js.homing.docview.reference.ReferenceDocs;
import hue.captains.singapura.js.homing.docview.tree.Citations;
import hue.captains.singapura.js.homing.docview.tree.DocRef;
import hue.captains.singapura.js.homing.docview.tree.DocTree;
import hue.captains.singapura.js.homing.docview.tree.DocTrees;
import hue.captains.singapura.js.homing.server.EmptyParam;
import hue.captains.singapura.js.homing.server.HtmlPageContent;
import hue.captains.singapura.js.homing.site.Path;
import hue.captains.singapura.js.homing.site.Placed;
import hue.captains.singapura.js.homing.site.Query;
import hue.captains.singapura.js.homing.site.Trail;
import hue.captains.singapura.js.homing.site.catalogue.CatalogueRouter;
import hue.captains.singapura.js.homing.site.catalogue.L0_Catalogue;
import hue.captains.singapura.js.homing.site.catalogue.L1_Catalogue;
import hue.captains.singapura.js.homing.site.catalogue.Leaf;
import hue.captains.singapura.js.homing.site.mpa.Mpa;
import hue.captains.singapura.js.homing.studio.base.composed.ComposedDoc;
import hue.captains.singapura.js.homing.studio.base.Doc;
import hue.captains.singapura.js.homing.studio.base.DocReference;
import hue.captains.singapura.js.homing.studio.base.Reference;
import hue.captains.singapura.js.homing.studio.base.composed.MarkdownSegment;
import hue.captains.singapura.js.homing.tree.NodeName;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A doc's references, resolved against where a site reads its docs: a doc reference goes to the
 * target's authentic path - its reading page's, never the inspector's, and never an id; one no
 * catalogue places goes nowhere and says so; an external one to its address; each with the
 * sections citing it, a table's cell among them. A doc read at two places is said; a citation of
 * a name the doc does not declare is said; the reference docs cite nothing they do not declare.
 */
class DocReferencesTest {

    /** A page the doc is read on. */
    record Reading(Doc doc) implements Placed, HoldsDoc {
        @Override public HtmlPageContent html(Trail trail, Query q) { return new HtmlPageContent("read " + doc.title()); }
    }

    /** Another kind of page the doc is read on: two kinds, so a doc can be read at two places - which a site must not do. */
    record ReadingToo(Doc doc) implements Placed, HoldsDoc {
        @Override public HtmlPageContent html(Trail trail, Query q) { return new HtmlPageContent("read again " + doc.title()); }
    }

    /** The reference docs read at their names, and inspected below - all but the unplaced one. */
    record Shelf() implements L0_Catalogue<Shelf> {
        static final Shelf INSTANCE = new Shelf();
        @Override public String name() { return "Shelf"; }
        @Override public List<? extends L1_Catalogue<Shelf, ?>> subCatalogues() { return List.of(Inspect.INSTANCE); }
        @Override public List<Leaf<Shelf>> leaves(Mpa mpa) {
            return ReferenceDocs.ALL.entrySet().stream().map(e -> Leaf.of(this, new NodeName(e.getKey()), e.getValue().title(), "", new Reading(e.getValue()))).toList();
        }
    }

    record Inspect() implements L1_Catalogue<Shelf, Inspect> {
        static final Inspect INSTANCE = new Inspect();
        @Override public Shelf parent() { return Shelf.INSTANCE; }
        @Override public String name() { return "Inspect"; }
        @Override public NodeName slug() { return new NodeName("inspect"); }
        @Override public List<Leaf<Inspect>> leaves(Mpa mpa) {
            return ReferenceDocs.ALL.entrySet().stream().map(e -> DocLeaves.inspected(this, mpa, new NodeName(e.getKey()), e.getValue())).toList();
        }
    }

    static final CatalogueRouter ROUTER = CatalogueRouter.at(Path.ROOT, Shelf.INSTANCE, new DocRoutesTest.SayingMpa());

    private static DocTree tree(Doc d) { return DocTrees.of(d); }

    /** The section the markdown reference cites from: its path, and its heading. */
    private static final List<DocRef.Citing> CITATIONS = List.of(new DocRef.Citing("citations", "Citations"));

    @Test
    void aDocsPath_isItsReadingPages_neverTheInspectors() {
        var places = DocPlaces.of(ROUTER);
        assertEquals(4, places.size(), "four docs read; the inspector's pages are not where they are read");
        assertEquals("/rigid", places.pathOf(ReferenceDocs.RIGID).orElseThrow());
        assertEquals("/markdown", places.pathOf(MarkdownReference.INSTANCE).orElseThrow());
        assertEquals("/markdown", places.pathOf(new MarkdownReference()).orElseThrow(), "found by the doc itself: an equal doc is the same doc");
        assertTrue(places.pathOf(ReferenceDocs.UNPLACED).isEmpty(), "no catalogue places it");
        assertTrue(places.twice().isEmpty());
    }

    @Test
    void theMarkdownReferences_referencesResolve_eachKindAsItShould() {
        var refs = DocReferences.of(MarkdownReference.INSTANCE, tree(MarkdownReference.INSTANCE), DocPlaces.of(ROUTER));
        assertEquals(List.of(
                new DocRef("rigid", DocRef.DOC, "Rigid reference", ReferenceDocs.RIGID.summary(), "/rigid", CITATIONS),
                new DocRef("composed", DocRef.DOC, "Composed reference", ReferenceDocs.COMPOSED.summary(), "/composed", CITATIONS),
                new DocRef("commonmark", DocRef.EXTERNAL, "CommonMark", "The markdown specification the reader follows.", "https://commonmark.org", CITATIONS),
                new DocRef("unplaced", DocRef.UNPLACED, "An unplaced doc", "Declared and cited, and placed by no catalogue.", "", CITATIONS),
                new DocRef("named-rigid", DocRef.DOC, "Named rigid reference", ReferenceDocs.NAMED_RIGID.summary(), "/named-rigid", List.of())), refs,
                "in the order declared; the last declared and never cited");
        assertEquals(List.of("rigid", "composed", "commonmark", "unplaced"), List.copyOf(Citations.of(tree(MarkdownReference.INSTANCE)).keySet()),
                "the rigid reference cited twice - in prose and in a table's cell - and listed once");
    }

    @Test
    void theLaw_everyNameCitedIsDeclared_andADocHasOneReadingPage() {
        for (var e : ReferenceDocs.ALL.entrySet()) {
            assertEquals(List.of(), DocReferences.undeclared(e.getValue(), tree(e.getValue())), e.getKey() + " cites only what it declares");
        }
        var citing = ComposedDoc.of(UUID.fromString("5d2b8f2e-6a41-4c3e-9f0b-1d7e3c9a4c01"), "Citing", "", "DOC",
                List.of(new MarkdownSegment("See [the rigid one](#ref:rigid) and [nothing](#ref:ghost).")));
        assertEquals(List.of("rigid", "ghost"), DocReferences.undeclared(citing, tree(citing)), "a composed doc's citations, found as a markdown doc's are");

        record Twice() implements L0_Catalogue<Twice> {
            @Override public String name() { return "Twice"; }
            @Override public List<Leaf<Twice>> leaves(Mpa mpa) {
                return List.of(Leaf.of(this, new NodeName("one"), "One", "", new Reading(ReferenceDocs.RIGID)),
                        Leaf.of(this, new NodeName("two"), "Two", "", new ReadingToo(ReferenceDocs.RIGID)));
            }
        }
        Map<Doc, List<String>> twice = DocPlaces.of(CatalogueRouter.at(Path.ROOT, new Twice(), new DocRoutesTest.SayingMpa())).twice();
        assertEquals(Map.of(ReferenceDocs.RIGID, List.of("/one", "/two")), twice, "a doc read at two places has no one path: said");
    }

    @Test
    void thePayload_carriesTheReferences_asThePageReadsThem() throws Exception {
        String body = new PayloadGetAction(new DocViews(ROUTER)).execute(new PayloadGetAction.Query("/markdown"), new EmptyParam.NoHeaders()).get().body();
        var refs = new JsonObject(body).getJsonArray("references");
        assertEquals(5, refs.size());
        assertEquals("/rigid", refs.getJsonObject(0).getString("to"));
        assertEquals("unplaced", refs.getJsonObject(3).getString("kind"));
        assertEquals("Citations", refs.getJsonObject(0).getJsonArray("citedIn").getJsonObject(0).getString("label"));
        assertEquals("citations", refs.getJsonObject(0).getJsonArray("citedIn").getJsonObject(0).getString("path"));
        assertTrue(!body.contains("5d2b8f2e"), "no id anywhere in it");
    }

    @Test
    void aDocWithNoReferences_hasNone() {
        List<Reference> none = ReferenceDocs.RIGID.references();
        assertTrue(none.isEmpty());
        assertEquals(List.of(), DocReferences.of(ReferenceDocs.RIGID, tree(ReferenceDocs.RIGID), DocPlaces.of(ROUTER)));
        assertTrue(new DocReference("x", ReferenceDocs.RIGID).name().equals("x"));
    }
}
