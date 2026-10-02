package hue.captains.singapura.js.homing.planview.tree;

import hue.captains.singapura.js.homing.docview.reference.DocViewPlan;
import hue.captains.singapura.js.homing.docview.reference.ReferencePlans;
import hue.captains.singapura.js.homing.docview.tree.DocRef;
import hue.captains.singapura.js.homing.ssjs.test.JsModuleTestBase;
import hue.captains.singapura.js.homing.studio.base.tracker.Acceptance;
import hue.captains.singapura.js.homing.studio.base.tracker.Decision;
import hue.captains.singapura.js.homing.studio.base.tracker.Dependency;
import hue.captains.singapura.js.homing.studio.base.tracker.Phase;
import hue.captains.singapura.js.homing.studio.base.tracker.PhaseStatus;
import hue.captains.singapura.js.homing.studio.base.tracker.Plan;
import hue.captains.singapura.js.homing.studio.base.tracker.Task;
import org.graalvm.polyglot.Value;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A plan's tree: its sections, the parts of their leaves and the keys they are asked by; and its
 * payload, read as a page reads it - JSON.parse - every part's content of its type's shape.
 */
class PlanTreeTest extends JsModuleTestBase {

    private static final String AT = "/plans/docview";

    @Test
    void theReferencePlan_isItsPillarsThenAPhaseASection() {
        PlanTree t = PlanTree.of(ReferencePlans.DOCVIEW);
        assertEquals(List.of("", "objectives", "decisions", "acceptance", "phases",
                "phases/1", "phases/2", "phases/3", "phases/4", "phases/5", "phases/6", "phases/7"), t.paths());
        assertEquals(List.of(":0 plan-head", "objectives:0 plan-list", "decisions:0 plan-list", "acceptance:0 plan-list",
                        "phases/1:0 plan-phase", "phases/2:0 plan-phase", "phases/3:0 plan-phase", "phases/4:0 plan-phase",
                        "phases/5:0 plan-phase", "phases/6:0 plan-phase", "phases/7:0 plan-phase"),
                t.spots().stream().map(s -> s.key() + " " + s.part().type()).toList(), "the phases' own section has nothing of its own");
        assertEquals("DocView on a tree placement", t.root().label().text());
        assertEquals("7 — Plans", t.root().children().get(3).children().get(6).label().text());
    }

    @Test
    void aPhase_knowsWhereThePhasesItDependsOnAre() {
        var plan = new Edges(List.of(phase("P 1", "First", List.of()), phase("A", "Second", List.of(new Dependency("P 1", "built on it"))),
                phase("A", "Third", List.of(new Dependency("missing", "nowhere"), new Dependency("A", "the first A")))));
        PlanTree t = PlanTree.of(plan);
        assertEquals(List.of("", "phases", "phases/p-1", "phases/A", "phases/A-2"), t.paths(),
                "an id that may not be a name is made one as a heading's is; a repeat takes -2");
        var second = (PlanPart.Step) t.root().children().get(0).children().get(1).leaf().get(0);
        assertEquals(List.of(new PlanPart.After("P 1", "phases/p-1", "First", "built on it")), second.after());
        var third = (PlanPart.Step) t.root().children().get(0).children().get(2).leaf().get(0);
        assertEquals(List.of(new PlanPart.After("missing", "", "", "nowhere"), new PlanPart.After("A", "phases/A", "Second", "the first A")), third.after(),
                "a phase the plan does not have goes nowhere; a repeated id is the first phase with it");
    }

    @Test
    void aPillarLeftEmpty_hasNoSection() {
        assertEquals(List.of(""), PlanTree.of(new Edges(List.of())).paths());
    }

    @Test
    void thePayload_readsAsAPageReadsIt() {
        global("JSON");
        PlanTree t = PlanTree.of(ReferencePlans.DOCVIEW);
        js.getBindings("js").putMember("text", PlanPayload.json(t, AT, PlanPayload.NOWHERE));
        js.eval("js", "var p = JSON.parse(text); var byKey = {}; p.items.forEach(function (i) { byKey[i.params[1].value] = i; });");
        assertEquals(AT, js.eval("js", "p.doc").asString());
        assertEquals(t.spots().size(), js.eval("js", "p.items.length").asInt(), "every part, in one");
        assertEquals(0, js.eval("js", "p.references.length").asInt());
        assertEquals("doc|key", js.eval("js", "byKey[':0'].params.map(function (x) { return x.name; }).join('|')").asString());
        assertEquals("phases|1,2,3,4,5,6,7", js.eval("js", "var ph = p.tree.children[3]; ph.name + '|' + ph.children.map(function (c) { return c.name; })").asString());

        Value head = js.eval("js", "byKey[':0'].content");
        assertEquals("RFC 0066 · E3", head.getMember("kicker").asString());
        assertEquals("5/7 4/5 3/4", js.eval("js", "var h = byKey[':0'].content; [h.phases, h.decisions, h.acceptance].map(function (c) { return c.of + '/' + c.total; }).join(' ')").asString(),
                "phases done, decisions open, acceptance met");
        assertEquals("Execution plan|unplaced|", js.eval("js", "var d = byKey[':0'].content.docs[0]; d.role + '|' + d.kind + '|' + d.to").asString());

        assertEquals("4 open of 5|Q5|resolved|Resolved|Chosen,Rationale", js.eval("js",
                "var c = byKey['decisions:0'].content, r = c.rows[4]; c.note + '|' + r.id + '|' + r.mark + '|' + r.label + '|' + r.more.map(function (m) { return m.label; })").asString(),
                "what a decision does not say is not listed");
        assertEquals("3 of 4 met|met,met,met,unmet", js.eval("js",
                "var a = byKey['acceptance:0'].content; a.note + '|' + a.rows.map(function (r) { return r.mark; })").asString());
        assertEquals("|One view, one tree|", js.eval("js", "var o = byKey['objectives:0'].content.rows[0]; o.mark + '|' + o.title + '|' + o.id").asString());

        assertEquals("in-progress|80|5|4,6|phases/4,phases/6", js.eval("js",
                "var f = byKey['phases/7:0'].content; f.status + '|' + f.progress + '|' + f.tasks.length + '|' + f.after.map(function (a) { return a.phase; }) + '|' + f.after.map(function (a) { return a.path; })").asString());
        assertEquals("Diagram languages drawn|false", js.eval("js",
                "var five = byKey['phases/5:0'].content; five.metrics[0].label + '|' + five.tasks[4].done").asString());
    }

    @Test
    void aDocThePlanNames_goesWhereTheSiteReadsIt() {
        global("JSON");
        var placed = new DocRef("x", DocRef.DOC, "The RFC", "Its design", "/rfcs/docview", List.of());
        js.getBindings("js").putMember("text", PlanPayload.json(PlanTree.of(ReferencePlans.DOCVIEW), AT,
                id -> id.equals(DocViewPlan.RFC) ? Optional.of(placed) : Optional.empty()));
        assertEquals("Execution plan|doc|The RFC|/rfcs/docview", js.eval("js",
                "var d = JSON.parse(text).items[0].content.docs[0]; d.role + '|' + d.kind + '|' + d.title + '|' + d.to").asString());
    }

    private static Phase phase(String id, String label, List<Dependency> after) {
        return new Phase(id, label, "", "", PhaseStatus.NOT_STARTED, List.of(new Task("a task", false)), after, "", "", "", "");
    }

    /** A plan of phases alone, the other pillars left empty. */
    record Edges(List<Phase> phases) implements Plan {
        @Override public String name() { return "Edges"; }
        @Override public List<Decision> decisions() { return List.of(); }
        @Override public List<Acceptance> acceptance() { return List.of(); }
    }
}
