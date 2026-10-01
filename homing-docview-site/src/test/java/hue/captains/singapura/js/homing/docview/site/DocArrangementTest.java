package hue.captains.singapura.js.homing.docview.site;

import hue.captains.singapura.js.homing.docview.reference.ReferenceDocs;
import hue.captains.singapura.js.homing.docview.tree.DocArrangements;
import hue.captains.singapura.js.homing.docview.tree.DocPayload;
import hue.captains.singapura.js.homing.docview.tree.DocTree;
import hue.captains.singapura.js.homing.docview.tree.DocTrees;
import hue.captains.singapura.js.homing.ssjs.test.JsModuleTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The arrangement a page makes of a doc's tree, from the payload, is the one Java makes - for every
 * reference doc, widget for widget and node for node; and it is refused without a tree or a doc.
 */
class DocArrangementTest extends JsModuleTestBase {

    @BeforeEach
    void load() { loadModule("/homing/js/hue/captains/singapura/js/homing/docview/site/DocArrangementModule.js"); }

    @Test
    void thePagesArrangement_isJavas_forEveryReferenceDoc() {
        ReferenceDocs.ALL.forEach((name, doc) -> {
            DocTree tree = DocTrees.of(doc);
            String at = "/reference/" + name;
            js.getBindings("js").putMember("payload", DocPayload.json(tree, at));
            js.getBindings("js").putMember("java", DocPayload.arrangement(DocArrangements.of(tree, at)));
            String page = js.eval("js", "var p = JSON.parse(payload); JSON.stringify(DocArrangement.of(p.tree, p.doc))").asString();
            String expected = js.eval("js", "JSON.stringify(JSON.parse(java))").asString();
            assertEquals(expected, page, name);
        });
    }

    @Test
    void itIsRefused_withoutATreeOrADoc() {
        assertTrue(js.eval("js", "(function () { try { DocArrangement.of(null, '/d'); return ''; } catch (e) { return e.message; } })()").asString()
                .contains("a doc's tree is required"));
        assertTrue(js.eval("js", "(function () { try { DocArrangement.of({ leaf: [], children: [] }, ''); return ''; } catch (e) { return e.message; } })()").asString()
                .contains("the doc's address is required"));
    }
}
