package hue.captains.singapura.js.homing.docview.tree;

import hue.captains.singapura.js.homing.docview.reference.MarkdownReference;
import hue.captains.singapura.js.homing.docview.reference.ReferenceDocs;
import hue.captains.singapura.js.homing.studio.base.Doc;
import hue.captains.singapura.js.homing.studio.base.ProxyDoc;
import hue.captains.singapura.js.homing.studio.base.table.TableData;
import hue.captains.singapura.js.homing.studio.base.table.TableDoc;
import hue.captains.singapura.js.homing.workspace.groups.core.models.TreePlacement;
import hue.captains.singapura.js.homing.workspace.groups.core.models.TreePlacement.Run;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The reference docs, each made into its tree: the whole outline as it should be - every
 * node's path and its leaf's parts - and each construct where its doc says it is.
 */
class ReferenceTreesTest {

    private static DocTree tree(String name) { return DocTrees.of(ReferenceDocs.ALL.get(name)); }

    private static Part part(DocTree t, String key) { return t.part(key).orElseThrow(() -> new AssertionError("no part " + key)); }

    @Test
    void theMarkdownReference_outline() {
        assertEquals("""
                /  P
                prose-only  P
                a-table-between-prose  P T3x3 P
                code-between-prose  P C(java) P C P
                a-table-first  T2x1
                a-diagram  C(mermaid) P C(mermaid)
                a-section-with-its-own-content-and-86e72a  P
                a-section-with-its-own-content-and-86e72a/the-first-child  P
                a-section-with-its-own-content-and-86e72a/the-second-child
                a-section-with-its-own-content-and-86e72a/the-second-child/skipping-a-level  P
                a-section-with-no-content-of-its-own
                a-section-with-no-content-of-its-own/only-a-child  P
                an-empty-heading
                duplicate  P
                duplicate-2  P
                the-treeplacement-strong-and-emphasis  P
                a-heading-far-longer-than-forty-af2503  P
                cafe-creme-and-umlauts  P
                section  P
                code-inside-a-list  P
                a-quote-holding-a-table  P
                citations  P T2x1""", Outlines.of(tree("markdown")));
    }

    @Test
    void theMarkdownReference_eachConstructWhereItSays() {
        DocTree t = tree("markdown");
        assertEquals("Markdown reference", t.root().label().text(), "the title that repeats the doc's is the root");
        assertTrue(((Part.Prose) part(t, ":0")).text().startsWith("The introduction"));
        assertTrue(((Part.Prose) part(t, "prose-only:0")).text().contains("> A quote, in the prose."), "a quote stays in the prose");

        var table = (Part.Table) part(t, "a-table-between-prose:1");
        assertEquals(List.of("left", "center", "right"), table.columns().stream().map(Part.Column::align).toList());
        assertEquals("A pipe | escaped", table.rows().get(1).cells().get(0).text());
        assertEquals("A pipe in `code | spans`", table.rows().get(2).cells().get(0).text(), "a pipe inside a code span is kept");

        assertEquals(new Part.Code("java", "record Point(int x, int y) {}"), part(t, "code-between-prose:1"));
        var plain = (Part.Code) part(t, "code-between-prose:3");
        assertEquals("", plain.language());
        assertTrue(plain.source().contains("# Not a heading: inside a fence."), "nothing inside a fence is a heading");
        assertEquals("mermaid", ((Part.Code) part(t, "a-diagram:0")).language());

        TreePlacement.Label runs = t.node("the-treeplacement-strong-and-emphasis").orElseThrow().label();
        assertEquals(List.of(new Run.Text("The "), new Run.Code("TreePlacement"), new Run.Text(", "), new Run.Strong("strong"),
                new Run.Text(" and "), new Run.Emphasis("emphasis")), runs.runs());
        assertTrue(((Part.Prose) part(t, "code-inside-a-list:0")).text().contains("```bash"), "a fence inside a list item stays in the prose");
        assertTrue(((Part.Prose) part(t, "a-quote-holding-a-table:0")).text().contains("A setext heading\n---"), "a setext heading is prose");
        assertEquals(List.of(), t.node("an-empty-heading").orElseThrow().leaf(), "a heading with nothing under it has no leaf");
        assertTrue(NodeNames.cuts(t.node("a-heading-far-longer-than-forty-af2503").orElseThrow().label().text()), "a long heading cut, and digested");
    }

    @Test
    void theRigidReference_itsNodesNamedByTheirTitles() {
        DocTree t = tree("rigid");
        assertEquals("""
                /  P
                prose-segments  P P T2x1 P
                code-and-a-relation  C(java) T2x2
                code-and-a-relation/a-child-of-it  P
                a-picture  I(svg)
                prose-segments-2  P""", Outlines.of(t));
        assertEquals("Segments and their parts", ((Part.Table) part(t, "code-and-a-relation:1")).caption());
        var picture = (Part.Image) part(t, "a-picture:0");
        assertTrue(picture.svg().startsWith("<svg"));
        assertEquals("A heading and its leaf", picture.alt());
        assertEquals("A heading, and its leaf", picture.caption());
    }

    @Test
    void theNamedRigidReference_itsAuthoredNamesUsedAsWritten() {
        DocTree t = tree("named-rigid");
        assertEquals("""
                /  P
                why.v2  P P
                how_it_reads  C(java)
                how_it_reads/deeper  P""", Outlines.of(t));
        assertEquals("A caption, shown first", ((Part.Prose) part(t, "why.v2:0")).text());
        assertEquals("Why names are authored", t.node("why.v2").orElseThrow().label().text());
    }

    @Test
    void theComposedReference_titledSegmentsStartNodes_untitledOnesFollow() {
        DocTree t = tree("composed");
        assertEquals("""
                /  P P
                titled-markdown  P C(mermaid) P T2x2
                figures-and-tables  P T2x2 I(svg)
                a-titled-code-segment  C(bash)
                a-doc-inside-a-doc  P H""", Outlines.of(t));
        assertEquals("- an untitled list\n- belongs to the node before it", ((Part.Prose) part(t, "titled-markdown:2")).text(), "a list of prose, a markdown list");
        var relation = (Part.Table) part(t, "titled-markdown:3");
        assertEquals("Composed constructs", relation.caption());
        var cell = relation.rows().get(0).cells().get(1);
        assertEquals(List.of("success", "strong"), List.of(cell.badge(), cell.emphasis()), "a cell articulated as its relation says");
        var kinds = (Part.Table) part(t, "figures-and-tables:1");
        assertEquals("Kinds and their trees", kinds.caption());
        assertEquals("success", kinds.rows().get(0).cells().get(1).badge());
        var spanning = kinds.rows().get(1).cells().get(0);
        assertEquals(List.of(2, 1), List.of(spanning.colSpan(), spanning.rowSpan()));
        assertEquals("center", spanning.align());
        assertSame(ReferenceDocs.INNER, ((Part.Held) part(t, "a-doc-inside-a-doc:1")).doc(), "a doc held: a part of its own, the doc as it is - the payload writes its card");
    }

    @Test
    void aProxy_isTheDocItStandsFor_underItsOwnTitle() {
        Doc proxy = new ProxyDoc(UUID.fromString("5d2b8f2e-6a41-4c3e-9f0b-1d7e3c9a4b09"), MarkdownReference.INSTANCE,
                Optional.of("Standing in"), Optional.empty(), Optional.empty());
        DocTree t = DocTrees.of(proxy);
        assertEquals("Standing in", t.root().label().text());
        assertEquals(Outlines.of(tree("markdown")).lines().skip(1).toList(), Outlines.of(t).lines().skip(1).toList());
    }

    @Test
    void aKindTheViewDoesNotTake_isRefused_sayingSo() {
        var table = new TableDoc(UUID.fromString("5d2b8f2e-6a41-4c3e-9f0b-1d7e3c9a4b08"), "A table alone", "", TableData.fromCsv("a,b\n1,2"));
        var e = assertThrows(IllegalArgumentException.class, () -> DocTrees.of((Doc) table));
        assertTrue(e.getMessage().contains("DocView takes markdown, rigid and composed docs"), e.getMessage());
    }
}
