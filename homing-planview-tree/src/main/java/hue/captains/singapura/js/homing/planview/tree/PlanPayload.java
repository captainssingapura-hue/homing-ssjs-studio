package hue.captains.singapura.js.homing.planview.tree;

import hue.captains.singapura.js.homing.docview.tree.DocPayload;
import hue.captains.singapura.js.homing.docview.tree.DocRef;
import hue.captains.singapura.js.homing.docview.tree.Json;
import hue.captains.singapura.js.homing.studio.base.tracker.Acceptance;
import hue.captains.singapura.js.homing.studio.base.tracker.Decision;
import hue.captains.singapura.js.homing.studio.base.tracker.DecisionStatus;
import hue.captains.singapura.js.homing.studio.base.tracker.Metric;
import hue.captains.singapura.js.homing.studio.base.tracker.Objective;
import hue.captains.singapura.js.homing.studio.base.tracker.Phase;
import hue.captains.singapura.js.homing.studio.base.tracker.PhaseStatus;
import hue.captains.singapura.js.homing.studio.base.tracker.Plan;
import hue.captains.singapura.js.homing.studio.base.tracker.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static hue.captains.singapura.js.homing.docview.tree.DocPayload.ordered;
import static hue.captains.singapura.js.homing.planview.tree.PlanTree.text;

/**
 * A plan as a page loads it, in one - the same payload a doc's is: its tree, every section's name
 * and label and the parts of its leaf by type and key, from which the page makes its arrangement;
 * and the content of every part, with the params it is asked by. A plan has no references of
 * its own: the docs it names - its execution doc, its dossier - are its head's.
 *
 * <pre>{@code
 * { "doc": "/plans/docview",
 *   "tree": { "name": "", "label": { "text": "…", "runs": [] }, "leaf": [{ "type": "plan-head", "key": ":0" }],
 *             "children": [ { "name": "objectives", … }, …, { "name": "phases", "children": [ { "name": "1", … } ] } ] },
 *   "items": [ { "type": "plan-head", "params": [{ "name": "doc", … }, { "name": "key", "value": ":0" }],
 *                "content": { "kicker": "…", "lede": "…", "progress": 80, "phases": { "of": 5, "total": 7 }, … } }, … ],
 *   "references": [] }
 * }</pre>
 * The content of each type:
 * <ul>
 *   <li>{@code plan-head}: {@code { kicker, lede, progress, phases, decisions, acceptance, docs }} - the counts
 *       each {@code { of, total }} (phases done, decisions open, acceptance met); each doc {@code { role, kind, title, summary, to }};</li>
 *   <li>{@code plan-list}: {@code { note, rows: [{ id, mark, label, title, text, more: [{ label, text }] }] }} - the
 *       mark {@code met}, {@code unmet}, a decision's status, or empty;</li>
 *   <li>{@code plan-phase}: {@code { id, status, statusLabel, progress, effort, summary, description, tasks: [{ text, done }],
 *       metrics: [{ label, before, after, delta }], after: [{ phase, path, label, reason }], verification, rollback, notes }}.</li>
 * </ul>
 */
public final class PlanPayload {

    private PlanPayload() {}

    /** A plan's docs, found nowhere: each said to be placed nowhere. */
    public static final Function<String, Optional<DocRef>> NOWHERE = id -> Optional.empty();

    /** The payload of a plan's tree, at its address; the docs it names found by their ids as the site finds them. */
    public static String json(PlanTree tree, String at, Function<String, Optional<DocRef>> docs) {
        var items = new ArrayList<String>();
        for (PlanTree.Spot s : tree.spots()) {
            items.add(Json.obj(ordered("type", Json.str(s.part().type()), "params", DocPayload.params(at, s.key()), "content", content(s.part(), docs))));
        }
        return Json.obj(ordered("doc", Json.str(at), "tree", tree(tree.root(), ""), "items", "[" + String.join(",", items) + "]", "references", "[]"));
    }

    /** A section as the page reads it: its name, its label, its leaf's parts by type and key, its children. */
    private static String tree(PlanTree.Node n, String path) {
        var leaf = new ArrayList<String>();
        for (int i = 0; i < n.leaf().size(); i++) leaf.add(Json.obj(ordered("type", Json.str(n.leaf().get(i).type()), "key", Json.str(path + ":" + i))));
        return Json.obj(ordered("name", Json.str(n.name().map(Object::toString).orElse("")), "label", DocPayload.label(n.label()),
                "leaf", "[" + String.join(",", leaf) + "]", "children", Json.arr(n.children(), c -> tree(c, PlanTree.pathOf(path, c)))));
    }

    static String content(PlanPart part, Function<String, Optional<DocRef>> docs) {
        return switch (part) {
            case PlanPart.Head h -> head(h.plan(), docs);
            case PlanPart.Objectives o -> list("", o.items(), PlanPayload::objective);
            case PlanPart.Decisions d -> list(d.items().stream().filter(x -> x.status() == DecisionStatus.OPEN).count() + " open of " + d.items().size(),
                    d.items(), PlanPayload::decision);
            case PlanPart.Acceptances a -> list(a.items().stream().filter(Acceptance::met).count() + " of " + a.items().size() + " met",
                    a.items(), PlanPayload::acceptance);
            case PlanPart.Step s -> phase(s);
        };
    }

    private static String head(Plan p, Function<String, Optional<DocRef>> docs) {
        long done = p.phases().stream().filter(x -> x.status() == PhaseStatus.DONE).count();
        var named = new ArrayList<String>();
        doc("Execution plan", p.executionDoc(), docs).ifPresent(named::add);
        doc("Dossier", p.dossierDoc(), docs).ifPresent(named::add);
        String lede = text(p.subtitle()).isBlank() ? text(p.summary()) : text(p.subtitle());
        return Json.obj(ordered("kicker", Json.str(text(p.kicker())), "lede", Json.str(lede), "progress", Json.num(p.totalProgress()),
                "phases", count((int) done, p.phases().size()), "decisions", count(p.openDecisions(), p.decisions().size()),
                "acceptance", count(p.acceptanceMet(), p.acceptance().size()), "docs", "[" + String.join(",", named) + "]"));
    }

    /** A doc the plan names, by its id: where the site reads it, or nowhere - none when the plan names none. */
    private static Optional<String> doc(String role, String id, Function<String, Optional<DocRef>> docs) {
        if (id == null || id.isBlank()) return Optional.empty();
        return Optional.of(docs.apply(id.strip())
                .map(r -> Json.obj(ordered("role", Json.str(role), "kind", Json.str(r.kind()), "title", Json.str(r.title()), "summary", Json.str(r.summary()), "to", Json.str(r.to()))))
                .orElseGet(() -> Json.obj(ordered("role", Json.str(role), "kind", Json.str(DocRef.UNPLACED), "title", Json.str(""), "summary", Json.str(""), "to", Json.str("")))));
    }

    private static String count(int of, int total) { return Json.obj(ordered("of", Json.num(of), "total", Json.num(total))); }

    private static <T> String list(String note, List<T> items, Function<T, String> row) {
        return Json.obj(ordered("note", Json.str(note), "rows", Json.arr(items, row)));
    }

    private static String row(String id, String mark, String label, String title, String text, List<String[]> more) {
        return Json.obj(ordered("id", Json.str(id), "mark", Json.str(mark), "label", Json.str(label), "title", Json.str(title), "text", Json.str(text),
                "more", Json.arr(more.stream().filter(m -> !m[1].isBlank()).toList(), m -> Json.obj(ordered("label", Json.str(m[0]), "text", Json.str(m[1]))))));
    }

    private static String objective(Objective o) { return row("", "", "", text(o.label()), text(o.description()), List.of()); }

    private static String decision(Decision d) {
        DecisionStatus s = d.status() == null ? DecisionStatus.OPEN : d.status();
        return row(text(d.id()), s.slug, s.label, text(d.question()), text(d.recommendation()),
                List.of(new String[] {"Chosen", text(d.chosenValue())}, new String[] {"Rationale", text(d.rationale())}, new String[] {"Notes", text(d.notes())}));
    }

    private static String acceptance(Acceptance a) {
        return row("", a.met() ? "met" : "unmet", a.met() ? "Met" : "Not met", text(a.label()), text(a.description()), List.of());
    }

    private static String phase(PlanPart.Step s) {
        Phase p = s.phase();
        PhaseStatus status = p.status() == null ? PhaseStatus.NOT_STARTED : p.status();
        List<Task> tasks = p.tasks() == null ? List.of() : p.tasks();
        List<Metric> metrics = p.metrics() == null ? List.of() : p.metrics();
        return Json.obj(ordered("id", Json.str(text(p.id())), "status", Json.str(status.slug), "statusLabel", Json.str(status.label),
                "progress", Json.num(p.tasks() == null ? 0 : p.progressPercent()), "effort", Json.str(text(p.effort())),
                "summary", Json.str(text(p.summary())), "description", Json.str(text(p.description())),
                "tasks", Json.arr(tasks, t -> Json.obj(ordered("text", Json.str(text(t.description())), "done", String.valueOf(t.done())))),
                "metrics", Json.arr(metrics, m -> Json.obj(ordered("label", Json.str(text(m.label())), "before", Json.str(text(m.before())),
                        "after", Json.str(text(m.after())), "delta", Json.str(text(m.delta()))))),
                "after", Json.arr(s.after(), a -> Json.obj(ordered("phase", Json.str(a.phase()), "path", Json.str(a.path()), "label", Json.str(a.label()), "reason", Json.str(a.reason())))),
                "verification", Json.str(text(p.verification())), "rollback", Json.str(text(p.rollback())), "notes", Json.str(text(p.notes()))));
    }
}
