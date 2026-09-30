package hue.captains.singapura.js.homing.docview.tree;

import hue.captains.singapura.js.homing.docview.reference.ReferenceDocs;
import hue.captains.singapura.js.homing.ssjs.test.JsModuleTestBase;
import hue.captains.singapura.js.homing.workspace.groups.core.models.ArrangedWidget;
import hue.captains.singapura.js.homing.workspace.groups.core.models.TreePlacement;
import org.graalvm.polyglot.Value;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Every reference doc as a placement of widgets - each part a widget of its type, its params
 * the doc's address and the part's key, the placement's own invariants and the arrangement's
 * checks held - and as the payload a page loads, read as a page reads it: JSON.parse.
 */
class ArrangementAndPayloadTest extends JsModuleTestBase {

    private static final String AT = "/reference/";

    @Test
    void everyPart_isAWidgetOfItsType_itsParamsTheDocAndTheKey() {
        ReferenceDocs.ALL.forEach((name, doc) -> {
            DocTree tree = DocTrees.of(doc);
            var arrangement = DocArrangements.of(tree, AT + name);   // the placement's invariants and the arrangement's checks, held
            List<DocTree.Spot> spots = tree.spots();
            List<TreePlacement.Spot> placed = arrangement.placement().spots();
            assertEquals(spots.size(), placed.size(), name);
            for (int i = 0; i < spots.size(); i++) {
                assertEquals(spots.get(i).key(), placed.get(i).locator(), name + ": the key is the widget's place");
                ArrangedWidget w = arrangement.widget(placed.get(i).widget()).orElseThrow();
                assertEquals(spots.get(i).part().type(), w.kind().value(), name);
                assertEquals(AT + name, w.params().get(DocArrangements.DOC));
                assertEquals(spots.get(i).key(), w.params().get(DocArrangements.KEY));
                assertEquals(2, w.params().size(), "the doc and the key, and nothing of where it sits");
            }
            assertEquals(tree.paths(), arrangement.placement().paths(), name + ": the same tree, node for node");
        });
    }

    @Test
    void thePayload_readsAsAPageReadsIt_everyPlainPartInIt() {
        global("JSON");
        ReferenceDocs.ALL.forEach((name, doc) -> {
            DocTree tree = DocTrees.of(doc);
            js.getBindings("js").putMember("text", DocPayload.json(tree, AT + name));
            Value p = js.eval("js", "JSON.parse(text)");
            assertEquals(AT + name, p.getMember("doc").asString());
            assertEquals("tree", p.getMember("arrangement").getMember("engine").asString());
            assertEquals("doc-view", p.getMember("arrangement").getMember("workspace").asString());
            long plain = tree.spots().stream().filter(s -> !(s.part() instanceof Part.Image)).count();
            assertEquals(plain, p.getMember("items").getArraySize(), name + ": every plain part, and no image");
            Value first = p.getMember("items").getArrayElement(0);
            assertEquals("doc", first.getMember("params").getArrayElement(0).getMember("name").asString(), "params in the order of their names");
            assertEquals("key", first.getMember("params").getArrayElement(1).getMember("name").asString());
            assertEquals(":0", first.getMember("params").getArrayElement(1).getMember("value").asString());
        });
    }

    @Test
    void eachTypesContent_isOfItsShape() {
        global("JSON");
        DocTree t = DocTrees.of(ReferenceDocs.ALL.get("markdown"));
        js.getBindings("js").putMember("text", DocPayload.json(t, AT + "markdown"));
        js.eval("js", "var p = JSON.parse(text); var byKey = {}; p.items.forEach(function (i) { byKey[i.params[1].value] = i; });");
        assertEquals("prose", js.eval("js", "byKey[':0'].type").asString());
        assertTrue(js.eval("js", "byKey[':0'].content.text").asString().startsWith("The introduction"));
        assertEquals("java|record Point(int x, int y) {}", js.eval("js", "var c = byKey['code-between-prose:1'].content; c.language + '|' + c.source").asString());
        assertEquals("Construct,Where,Count|left,center,right|A pipe | escaped|1|1",
                js.eval("js", "var tb = byKey['a-table-between-prose:1'].content; tb.columns.map(function (c) { return c.title; }) + '|' + "
                        + "tb.columns.map(function (c) { return c.align; }) + '|' + tb.rows[1].cells[0].text + '|' + tb.rows[1].cells[0].colSpan + '|' + tb.rows[1].cells[0].rowSpan").asString());
        assertEquals("a-diagram", js.eval("js", "p.arrangement.root.children[4].name").asString());
        assertEquals("text,code,text,strong,text,emphasis", js.eval("js",
                "p.arrangement.root.children.filter(function (n) { return n.label.runs.length; })[0].label.runs.map(function (r) { return r.kind; }).join(',')").asString());
    }

    @Test
    void anImage_isFetchedByItsKey_notInThePayload() {
        global("JSON");
        DocTree t = DocTrees.of(ReferenceDocs.ALL.get("rigid"));
        js.getBindings("js").putMember("text", DocPayload.content(t, AT + "rigid", "a-picture:0", DocPayload.NO_RASTERS).orElseThrow());
        Value c = js.eval("js", "JSON.parse(text)");
        assertEquals("image", c.getMember("type").asString());
        assertTrue(c.getMember("content").getMember("svg").asString().startsWith("<svg"));
        assertEquals("A heading, and its leaf", c.getMember("content").getMember("caption").asString());
        assertTrue(DocPayload.content(t, AT + "rigid", "nowhere:9", DocPayload.NO_RASTERS).isEmpty());
    }
}
