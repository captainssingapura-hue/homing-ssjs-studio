package hue.captains.singapura.js.homing.docview.widgets;

import hue.captains.singapura.js.homing.ssjs.test.JsModuleTestBase;
import org.graalvm.polyglot.Value;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A doc's stewards on the content parties' runtime, headless, fetch stubbed: a doc's payload is
 * fetched once for every part asked of it; the steward loads ahead every part of its type; a key
 * the doc has not, and a doc whose tree failed, are said unavailable; an image is fetched by its
 * key, and a part of another type at that key is refused.
 */
class StewardsTest extends JsModuleTestBase {

    private static final String WS = "/homing/js/hue/captains/singapura/js/homing/workspace/";
    private static final String DV = "/homing/js/hue/captains/singapura/js/homing/docview/widgets/";

    /** A doc with three prose parts and a code part; a doc whose tree failed; an image at a key; fetch, stubbed and counted. */
    private static final String SHIM = ProseContent.TYPE.js() + "\n" + ImageContent.TYPE.js() + """

        var console = { error: function () {} };
        function P(doc, key) { return ContentParams.of({ doc: doc, key: key }); }
        function item(type, doc, key, content) { return { type: type, params: P(doc, key), content: content }; }
        var PAYLOADS = {
            "/doc-view/payload?doc=%2Fd": { doc: "/d", arrangement: {}, items: [
                item("prose", "/d", ":0", { text: "intro" }), item("prose", "/d", "a:0", { text: "a" }),
                item("code", "/d", "a:1", { language: "java", source: "x" }), item("prose", "/d", "b:0", { text: "b" })] },
            "/doc-view/payload?doc=%2Fbroken": { doc: "/broken", failed: "IllegalArgumentException: no" },
            "/doc-view/content?doc=%2Fd&key=pic%3A0": { type: "image", params: P("/d", "pic:0"), content: { svg: "<svg/>", src: "", alt: "a", caption: "" } },
            "/doc-view/content?doc=%2Fd&key=a%3A0": { type: "prose", params: P("/d", "a:0"), content: { text: "a" } }
        };
        var calls = [];
        function fetch(url) {
            calls.push(url);
            var body = PAYLOADS[url];
            return Promise.resolve({ ok: !!body, status: body ? 200 : 404, json: function () { return Promise.resolve(body); } });
        }
        var heard = [];
        function widget(party, name) {
            var m = party.join(name, {
                Content: function (x) { heard.push(name + " " + ContentParams.object(x.params).key + "=" + JSON.stringify(x.content)); },
                Unavailable: function (x) { heard.push(name + " " + ContentParams.object(x.params).key + " unavailable: " + x.why); }
            });
            return { want: function (doc, key) { m.tell({ kind: "Wanted", params: P(doc, key) }); } };
        }
        """;

    @BeforeEach
    void load() {
        loadModule(WS + "parties/MessagingPartyModule.js");
        loadModule(WS + "content/ContentParamsModule.js");
        loadModule(WS + "content/ContentSecretaryModule.js");
        loadModule(DV + "DocSourcesModule.js");
        loadModule(DV + "DocPartStewardModule.js");
        loadModule(DV + "ProseStewardModule.js");
        loadModule(DV + "ImageStewardModule.js");
        js.eval("js", SHIM);
    }

    private Value eval(String src) { return js.eval("js", src); }
    private String str(String src) { return eval(src).asString(); }

    @Test
    void aDocsPayload_isFetchedOnce_forEveryPartAskedOfIt() {
        eval("var prose = new MessagingParty(PROSE, ContentSecretary, ProseSteward), w1 = widget(prose, 'w1'), w2 = widget(prose, 'w2');"
                + "w1.want('/d', 'a:0'); w2.want('/d', ':0');");
        assertEquals("/doc-view/payload?doc=%2Fd", str("calls.join(' | ')"), "one fetch, for two parts");
        assertEquals("w2 :0={\"text\":\"intro\"} | w1 a:0={\"text\":\"a\"}", str("heard.join(' | ')"),
                "both answered - in the payload's order, as the steward loads ahead");
    }

    @Test
    void theSteward_loadsAheadEveryPartOfItsType_andOnlyItsType() {
        eval("var prose = new MessagingParty(PROSE, ContentSecretary, ProseSteward), w = widget(prose, 'w'); w.want('/d', 'b:0');");
        assertEquals("%3A0,a%3A0,b%3A0", str("Object.keys(prose.state().held).map(function (k) { return k.split('key=')[1]; }).sort().join(',')"),
                "every prose part of the doc held; its code part not");
        eval("heard = []; var later = widget(prose, 'later'); later.want('/d', 'a:0');");
        assertEquals("later a:0={\"text\":\"a\"}", str("heard.join(' | ')"), "answered from what is held");
        assertEquals(1, eval("calls.length").asInt());
    }

    @Test
    void aKeyTheDocHasNot_andADocWhoseTreeFailed_areSaidUnavailable() {
        eval("var prose = new MessagingParty(PROSE, ContentSecretary, ProseSteward), w = widget(prose, 'w');"
                + "w.want('/d', 'nowhere:0'); w.want('/d', 'a:1'); w.want('/broken', ':0');");
        assertTrue(str("heard.join(' | ')").contains("w nowhere:0 unavailable: the doc has no prose part at nowhere:0"), str("heard.join(' | ')"));
        assertTrue(str("heard.join(' | ')").contains("w a:1 unavailable: the doc has no prose part at a:1"), "a part of another type is not this one");
        assertTrue(str("heard.join(' | ')").contains("w :0 unavailable: the doc's tree could not be built: IllegalArgumentException: no"));
    }

    @Test
    void anImage_isFetchedByItsKey_andAPartOfAnotherTypeRefused() {
        eval("var image = new MessagingParty(IMAGE, ContentSecretary, ImageSteward), w = widget(image, 'w'); w.want('/d', 'pic:0'); w.want('/d', 'a:0');");
        assertEquals("/doc-view/content?doc=%2Fd&key=pic%3A0 | /doc-view/content?doc=%2Fd&key=a%3A0", str("calls.join(' | ')"));
        assertTrue(str("heard.join(' | ')").contains("w pic:0={\"svg\":\"<svg/>\",\"src\":\"\",\"alt\":\"a\",\"caption\":\"\"}"), str("heard.join(' | ')"));
        assertTrue(str("heard.join(' | ')").contains("w a:0 unavailable: the part at a:0 is prose, not an image"));
    }
}
