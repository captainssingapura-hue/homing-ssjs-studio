package hue.captains.singapura.js.homing.docview.widgets;

import hue.captains.singapura.js.homing.ssjs.test.JsModuleTestBase;
import org.graalvm.polyglot.Value;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The diagram steward on the content parties' runtime, headless - fetch stubbed, mermaid's engine
 * stood in for: hired only when a diagram is wanted; a diagram drawn once, however many widgets
 * want it, by the engine for its language, its source read from the doc's payload; one the engine
 * refuses, one in a language no engine draws, and a key that is not code, each said unavailable
 * with why; another engine registered for another language.
 */
class DiagramStewardTest extends JsModuleTestBase {

    private static final String WS = "/homing/js/hue/captains/singapura/js/homing/workspace/";
    private static final String DV = "/homing/js/hue/captains/singapura/js/homing/docview/widgets/";

    /** Mermaid's engine stood in for, counting what it draws; and a doc with two diagrams, a java part and prose. */
    private static final String ENGINE = """
        var drawn = [];
        var MermaidEngine = { draw: function (source) {
            drawn.push(source);
            return source.indexOf("unclosed") >= 0 ? Promise.reject(new Error("mermaid could not read it: Parse error on line 2"))
                                                   : Promise.resolve("<svg>" + source + "</svg>");
        } };
        """;

    private static final String SHIM = DiagramContent.TYPE.js() + """

        var console = { error: function () {} };
        function P(doc, key) { return ContentParams.of({ doc: doc, key: key }); }
        function item(type, doc, key, content) { return { type: type, params: P(doc, key), content: content }; }
        var PAYLOADS = { "/doc-view/payload?doc=%2Fd": { doc: "/d", tree: {}, items: [
            item("prose", "/d", ":0", { text: "intro" }),
            item("code", "/d", "a:0", { language: "mermaid", source: "flowchart LR" }),
            item("code", "/d", "a:1", { language: "mermaid", source: "flowchart LR\\n an [unclosed" }),
            item("code", "/d", "b:0", { language: "java", source: "record P() {}" })] } };
        var calls = [];
        function fetch(url) {
            calls.push(url);
            var body = PAYLOADS[url];
            return Promise.resolve({ ok: !!body, status: body ? 200 : 404, json: function () { return Promise.resolve(body); } });
        }
        var heard = [];
        function widget(party, name) {
            var m = party.join(name, {
                Content: function (x) { heard.push(name + " " + ContentParams.object(x.params).key + "=" + x.content.language + " " + x.content.svg); },
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
        js.eval("js", ENGINE);
        loadModule(DV + "DiagramStewardModule.js");
        js.eval("js", SHIM);
    }

    private Value eval(String src) { return js.eval("js", src); }
    private String str(String src) { return eval(src).asString(); }

    @Test
    void aDiagram_isDrawnOnce_byItsLanguagesEngine_forEveryWidgetThatWantsIt() {
        eval("var diagrams = new MessagingParty(DIAGRAM, ContentSecretary, DiagramSteward);");
        assertEquals("not yet", str("diagrams.inspect().steward"), "no steward hired - and no engine loaded - until a diagram is wanted");
        eval("var w1 = widget(diagrams, 'w1'), w2 = widget(diagrams, 'w2'); w1.want('/d', 'a:0'); w2.want('/d', 'a:0');");
        assertEquals("flowchart LR", str("drawn.join(' | ')"), "drawn once");
        assertEquals("/doc-view/payload?doc=%2Fd", str("calls.join(' | ')"), "its source read from the doc's payload, once");
        assertEquals("w1 a:0=mermaid <svg>flowchart LR</svg> | w2 a:0=mermaid <svg>flowchart LR</svg>", str("heard.join(' | ')"));
        eval("heard = []; var later = widget(diagrams, 'later'); later.want('/d', 'a:0');");
        assertEquals("later a:0=mermaid <svg>flowchart LR</svg>", str("heard.join(' | ')"), "answered from what the party holds");
        assertEquals(1, eval("drawn.length").asInt());
    }

    @Test
    void whatCannotBeDrawn_isSaidUnavailable_withWhy() {
        eval("var diagrams = new MessagingParty(DIAGRAM, ContentSecretary, DiagramSteward), w = widget(diagrams, 'w');"
                + "w.want('/d', 'a:1'); w.want('/d', 'b:0'); w.want('/d', ':0'); w.want('/nowhere', 'a:0');");
        String heard = str("heard.join(' | ')");
        assertTrue(heard.contains("w a:1 unavailable: mermaid could not read it: Parse error on line 2"), heard);
        assertTrue(heard.contains("w b:0 unavailable: no engine draws java"), heard);
        assertTrue(heard.contains("w :0 unavailable: the doc has no code part at :0"), heard);
        assertTrue(heard.contains("w a:0 unavailable: not found"), heard);
    }

    @Test
    void anotherEngine_isRegisteredForItsLanguage() {
        assertTrue(eval("DiagramSteward.draws('mermaid') && !DiagramSteward.draws('java')").asBoolean());
        eval("DiagramSteward.engine('java', { draw: function (s) { return Promise.resolve('<svg>java</svg>'); } });"
                + "var diagrams = new MessagingParty(DIAGRAM, ContentSecretary, DiagramSteward), w = widget(diagrams, 'w'); w.want('/d', 'b:0');");
        assertEquals("w b:0=java <svg>java</svg>", str("heard.join(' | ')"));
        assertTrue(eval("(function () { try { DiagramSteward.engine('x', {}); return false; } catch (e) { return true; } })()").asBoolean(),
                "an engine draws, or it is refused");
    }
}
