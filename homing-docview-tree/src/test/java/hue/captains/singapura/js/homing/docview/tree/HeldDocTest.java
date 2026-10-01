package hue.captains.singapura.js.homing.docview.tree;

import hue.captains.singapura.js.homing.docview.reference.ReferenceDocs;
import hue.captains.singapura.js.homing.studio.base.composed.ComposedDoc;
import hue.captains.singapura.js.homing.studio.base.composed.ComposedSegment;
import hue.captains.singapura.js.homing.studio.base.composed.MarkdownSegment;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A doc held in another - a ComposedSegment - is a part of its own in the tree, the doc as it is;
 * the payload writes it as a card: what the holder calls it, the held doc's title a link where
 * the site reads it - or said to be read nowhere - and its summary.
 */
class HeldDocTest {

    private static final ComposedDoc HOLDER = ComposedDoc.of(UUID.fromString("5d2b8f2e-6a41-4c3e-9f0b-1d7e3c9a4c03"), "Holder", "", "DOC",
            List.of(new MarkdownSegment("Before."), new ComposedSegment(ReferenceDocs.COMPOSED, "A deep dive")));

    @Test
    void aHeldDoc_isAPartOfItsOwn_theDocAsItIs() {
        DocTree t = DocTrees.of(HOLDER);
        var held = assertInstanceOf(Part.Held.class, t.root().leaf().get(1));
        assertEquals(ReferenceDocs.COMPOSED, held.doc());
        assertEquals("A deep dive", held.caption());
        assertEquals("prose", held.type(), "shown as prose: a card");
        assertEquals(List.of("A deep dive"), Citations.texts(held), "its words are its caption");
    }

    @Test
    void thePayloadWritesItsCard_aLinkWhereTheSiteReadsIt() {
        var held = (Part.Held) DocTrees.of(HOLDER).root().leaf().get(1);
        String placed = DocPayload.card(held, d -> d == ReferenceDocs.COMPOSED ? Optional.of("/reference/composed") : Optional.empty());
        assertEquals("> **A deep dive**\n>\n> [Composed reference](/reference/composed) — " + ReferenceDocs.COMPOSED.summary().strip(), placed);
        String nowhere = DocPayload.card(held, DocPayload.NO_PLACES);
        assertTrue(nowhere.contains("*Composed reference*") && nowhere.endsWith("It is read nowhere on this site."), nowhere);
        assertEquals("> [Composed reference](/c) — " + ReferenceDocs.COMPOSED.summary().strip(),
                DocPayload.card(new Part.Held(ReferenceDocs.COMPOSED, "Composed reference"), d -> Optional.of("/c")), "a caption that is the title is not said twice");
    }
}
