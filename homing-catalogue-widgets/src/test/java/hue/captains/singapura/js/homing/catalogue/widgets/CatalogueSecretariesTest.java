package hue.captains.singapura.js.homing.catalogue.widgets;

import hue.captains.singapura.js.homing.ssjs.test.SecretaryTestBase;
import org.graalvm.polyglot.Value;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * The catalogue party, headless: the secretary within a scope, the scope's edge, and a
 * browser's scope linked under a page's party as the page runs them - a pick in the tree
 * reaching the details and the page, an asking to open reaching the page alone to act on.
 */
class CatalogueSecretariesTest extends SecretaryTestBase {

    private static final String DIR = "/homing/js/hue/captains/singapura/js/homing/catalogue/widgets/";
    private static final String PARTY = "/homing/js/hue/captains/singapura/js/homing/workspace/parties/MessagingPartyModule.js";

    @BeforeEach
    void load() {
        loadModule(DIR + "CatalogueChoiceSecretaryModule.js");
        loadSecretary(DIR + "CatalogueScopeSecretaryModule.js", "CatalogueScopeSecretary");
    }

    private static String kinds(Value step) {
        var out = new StringBuilder();
        Value a = step.getMember("actions");
        for (int i = 0; i < a.getArraySize(); i++) out.append(i == 0 ? "" : " ").append(a.getArrayElement(i).getMember("kind").asString())
                .append(":").append(a.getArrayElement(i).getMember("message").getMember("kind").asString());
        return out.toString();
    }
    private static int count(Value step, String field) { return step.getMember("newState").getMember(field).asInt(); }
    private static String picked(Value step) { return step.getMember("newState").getMember("choice").getMember("picked").asString(); }
    private Value pickedSoups() { return dispatch(initial(), envelope("Pick", Map.of("to", "/kitchen/soups"), "tree")).getMember("newState"); }

    @Test
    void aPickInTheScopeIsToldToIt_andGoesUp_once() {
        Value step = dispatch(initial(), envelope("Pick", Map.of("to", "/kitchen/soups"), "tree"));
        assertEquals("BroadcastToMembers:Picked SendToParent:Pick", kinds(step));
        assertEquals("/kitchen/soups", picked(step));
        Value again = dispatch(step.getMember("newState"), envelope("Pick", Map.of("to", "/kitchen/soups"), "tree"));
        assertEquals("", kinds(again), "the same again is nothing");
        assertEquals(1, count(again, "bubbled"));
        assertEquals(1, count(again, "kept"));
    }

    @Test
    void anAskingToOpenAlwaysGoesUp_theHostsToActOn() {
        Value step = dispatch(pickedSoups(), envelope("Open", Map.of("to", "/kitchen/soups/laksa", "opens", "in-place"), "details"));
        assertEquals("BroadcastToMembers:Opening SendToParent:Open", kinds(step));
        Value twice = dispatch(step.getMember("newState"), envelope("Open", Map.of("to", "/kitchen/soups/laksa", "opens", "in-place"), "details"));
        assertEquals("BroadcastToMembers:Opening SendToParent:Open", kinds(twice), "opening twice is asking twice");
    }

    /** Where the reader is reading is the scope's own: told to its members, for its lead to take the reader there; never up. */
    @Test
    void aReadIsToldToTheScope_neverUp() {
        Value step = dispatch(pickedSoups(), envelope("Read", Map.of("to", "/kitchen/soups"), "details"));
        assertEquals("BroadcastToMembers:Reading", kinds(step));
        assertEquals("/kitchen/soups", action(step, 0).getMember("message").getMember("to").asString());
        assertEquals("/kitchen/soups", picked(step), "reading is not picking");
    }

    @Test
    void aQuestionIsAnsweredInTheScope_neverUp() {
        Value step = dispatch(pickedSoups(), envelope("CurrentRequested", Map.of(), "details"));
        assertEquals("SendToMember:Picked", kinds(step));
        assertEquals("details", action(step, 0).getMember("to").asString());
        assertEquals("", kinds(dispatch(initial(), envelope("CurrentRequested", Map.of(), "details"))), "nothing picked, nothing said");
    }

    @Test
    void aPickFromAboveIsTheScopesOwn_andTheRestIsPassedOver() {
        Value step = dispatch(pickedSoups(), envelope("Picked", Map.of("to", "/notes"), "upstream"));
        assertEquals("BroadcastToMembers:Picked", kinds(step));
        assertEquals("/notes", picked(step));
        assertEquals(1, count(step, "adopted"));
        assertEquals("", kinds(dispatch(pickedSoups(), envelope("Opening", Map.of("to", "/notes", "opens", "in-place"), "upstream"))));
        assertEquals("", kinds(dispatch(pickedSoups(), envelope("Pick", Map.of("to", "/notes"), "upstream"))));
    }

    /** A browser's scope, linked under a page's party: the pick and the opening, as a page sees them. */
    @Test
    void linkedUnderAPage_aPickReachesAll_andAnOpeningReachesThePage() {
        loadModule(PARTY);
        js.eval("js", CatalogueChoice.TYPE.js());
        js.eval("js", """
            var console = { error: function () {} };
            var page = new MessagingParty(CATALOGUE, CatalogueChoiceSecretary), heard = [];
            page.join("page", { Picked: function (m) { heard.push("page " + m.to); },
                                Opening: function (m) { heard.push("page opens " + m.to + " " + m.opens); } });
            var scope = new MessagingParty(CATALOGUE, CatalogueScopeSecretary);
            scope.link(page, "catalogueBrowser").tell({ kind: "CurrentRequested" });
            var tree = scope.join("tree", { Picked: function (m) { heard.push("tree " + m.to); } });
            var details = scope.join("details", { Picked: function (m) { heard.push("details " + m.to); },
                                                  Opening: function (m) { heard.push("details opens " + m.to); } });
            tree.tell({ kind: "Pick", to: "/kitchen/soups/laksa" });
            """);
        assertEquals("tree /kitchen/soups/laksa | details /kitchen/soups/laksa | page /kitchen/soups/laksa",
                js.eval("js", "heard.join(' | ')").asString());
        js.eval("js", "heard = []; details.tell({ kind: 'Open', to: '/kitchen/soups/laksa', opens: 'new-tab' })");
        assertEquals("details opens /kitchen/soups/laksa | page opens /kitchen/soups/laksa new-tab", js.eval("js", "heard.join(' | ')").asString(),
                "the scope is told, and the page - whose it is to open");
        assertEquals(0, js.eval("js", "scope.inspect().refused.length + page.inspect().refused.length").asInt());
    }
}
