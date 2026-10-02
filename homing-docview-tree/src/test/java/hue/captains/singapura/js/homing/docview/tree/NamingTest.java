package hue.captains.singapura.js.homing.docview.tree;

import hue.captains.singapura.js.homing.workspace.groups.core.models.TreePlacement.Label;
import hue.captains.singapura.js.homing.workspace.groups.core.models.TreePlacement.Run;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The naming rules, each with the heading that exercises it; and a heading's label - its
 * plain text, its runs - as the TOC and the heading each draw it.
 */
class NamingTest {

    @Test
    void aHeadingIsLowerCased_accentsDropped_runsOfLettersAndDigitsJoinedByHyphens() {
        assertEquals("the-doc-tree", NodeNames.of("The doc tree"));
        assertEquals("cafe-creme-2", NodeNames.of("Café  Crème — 2!"));
        assertEquals("from-markdown", NodeNames.of("  From markdown  "));
        assertEquals("rfc-0066-e3", NodeNames.of("RFC 0066 · E3"));
    }

    @Test
    void aNameThatComesOutEmpty_isSection() {
        assertEquals("section", NodeNames.of("!!!"));
        assertEquals("section", NodeNames.of("—"));
        assertEquals("section", NodeNames.of(""));
    }

    @Test
    void overForty_itIsCutAtAWord_thenTheDigestOfTheWhole() {
        String heading = "A heading that is far longer than forty characters, which is the cut";
        String name = NodeNames.of(heading);
        String whole = "a-heading-that-is-far-longer-than-forty-characters-which-is-the-cut";
        assertEquals("a-heading-that-is-far-longer-than-forty-" + NodeNames.digest(whole), name);
        assertTrue(name.length() <= 48, name);
        assertTrue(NodeNames.cuts(heading));
        assertTrue(!NodeNames.cuts("The doc tree"));
        String oneWord = NodeNames.of("supercalifragilisticexpialidocious-and-then-some-more");
        assertEquals(40 + 7, NodeNames.of("x".repeat(60)).length(), "a word longer than the cut is cut where it is");
        assertTrue(oneWord.startsWith("supercalifragilisticexpialidocious-and-"), oneWord);
        assertEquals(NodeNames.of(heading), name, "the same heading, the same name");
    }

    @Test
    void aSiblingWithTheSameName_takesTwoThenThree_amongSiblingsOnly() {
        var siblings = new NodeNames.Siblings();
        assertEquals("notes", siblings.take("notes").value());
        assertEquals("notes-2", siblings.take("notes").value());
        assertEquals("notes-3", siblings.take("notes").value());
        assertEquals("other", siblings.take("other").value());
        assertEquals("notes", new NodeNames.Siblings().take("notes").value(), "counted among one node's children only");
        var long_ = new NodeNames.Siblings();
        String cut = NodeNames.of("A heading that is far longer than forty characters, which is the cut");
        long_.take(cut);
        String again = long_.take(cut).value();
        assertTrue(again.endsWith("-2") && again.length() <= 48, again);
    }

    @Test
    void aHeadingsLabel_isItsPlainText_andTheRunsThatDrawIt() {
        assertEquals(Label.of("Plain words"), HeadingLabels.of("Plain words"));
        assertEquals(Label.of("Closed"), HeadingLabels.of("Closed ##"));
        Label l = HeadingLabels.of("The `TreePlacement` and **its** *engine*");
        assertEquals("The TreePlacement and its engine", l.text());
        assertEquals(List.of(new Run.Text("The "), new Run.Code("TreePlacement"), new Run.Text(" and "), new Run.Strong("its"), new Run.Text(" "),
                new Run.Emphasis("engine")), l.runs());
    }

    @Test
    void aLinkIsItsText_anEscapeItsCharacter_andAnUnderscoreInsideAWordIsNotMarkup() {
        assertEquals("See the guide", HeadingLabels.of("See [the guide](/guides/one)").text());
        assertEquals("A *star* kept", HeadingLabels.of("A \\*star\\* kept").text());
        assertEquals(Label.of("snake_case_name stays"), HeadingLabels.of("snake_case_name stays"));
        assertEquals(List.of(new Run.Text("Double "), new Run.Code("``tick`` span")), HeadingLabels.of("Double ``` ``tick`` span ```").runs());
    }
}
