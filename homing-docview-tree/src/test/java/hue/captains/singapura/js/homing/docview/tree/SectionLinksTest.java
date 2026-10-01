package hue.captains.singapura.js.homing.docview.tree;

import hue.captains.singapura.js.homing.docview.reference.ReferenceDocs;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Links within a doc: each a section's full path, or a miss - with the path it may mean - and an
 * anchor an author set in a heading, the section's name.
 */
class SectionLinksTest {

    private static final String MARKDOWN = """
            Intro: see [the side-bar](#guide/side-bar), [it again](#side-bar), [nowhere](#gone),
            [a doc](#ref:other), and `[an example](#example)`.

            # Guide

            ## Side-bar — wire shape <a id="side-bar"></a>

            Back to [the guide](#guide).

            ## Steps

            ### Side-bar

            Twice named [side-bar](#side-bar).
            """;

    @Test
    void anAnchorInAHeading_isTheSectionsName_andNoPartOfItsLabel() {
        DocTree t = MarkdownTrees.of("Doc", MARKDOWN);
        assertEquals(List.of("", "guide", "guide/side-bar", "guide/steps", "guide/steps/side-bar"), t.paths());
        assertEquals("Side-bar — wire shape", t.node("guide/side-bar").orElseThrow().label().text());
    }

    @Test
    void aLinkWithinADoc_isASectionsFullPath_orAMiss_withThePathItMayMean() {
        DocTree t = MarkdownTrees.of("Doc", MARKDOWN);
        assertEquals(List.of(new SectionLinks.Link("", "guide/side-bar"), new SectionLinks.Link("", "side-bar"), new SectionLinks.Link("", "gone"),
                        new SectionLinks.Link("guide/side-bar", "guide"), new SectionLinks.Link("guide/steps/side-bar", "side-bar")),
                SectionLinks.of(t), "a citation is no link within the doc; a link quoted as code is an example of one");
        assertEquals(List.of(new SectionLinks.Miss("", "side-bar", Optional.empty()), new SectionLinks.Miss("", "gone", Optional.empty()),
                        new SectionLinks.Miss("guide/steps/side-bar", "side-bar", Optional.empty())),
                SectionLinks.unresolved(t), "two sections end with side-bar: no one path is meant");
        DocTree once = MarkdownTrees.of("Doc", "See [steps](#steps).\n\n# Guide\n\n## Steps\n");
        assertEquals(List.of(new SectionLinks.Miss("", "steps", Optional.of("guide/steps"))), SectionLinks.unresolved(once));
    }

    @Test
    void theReferenceDocs_linkWithinThemselves_bySectionsFullPaths() {
        ReferenceDocs.ALL.forEach((name, doc) -> assertEquals(List.of(), SectionLinks.unresolved(DocTrees.of(doc)), name));
    }
}
