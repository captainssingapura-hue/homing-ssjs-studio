package hue.captains.singapura.js.homing.planview.tree;

import hue.captains.singapura.js.homing.docview.tree.NodeNames;
import hue.captains.singapura.js.homing.studio.base.tracker.Dependency;
import hue.captains.singapura.js.homing.studio.base.tracker.Phase;
import hue.captains.singapura.js.homing.studio.base.tracker.Plan;
import hue.captains.singapura.js.homing.workspace.groups.core.models.TreePlacement.Label;
import hue.captains.singapura.js.homing.workspace.groups.core.models.TreePlacement.Name;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * A plan as a tree of named sections, as a doc is one - built headlessly, once. The root is the
 * plan, its leaf its head; then a section for each pillar it fills - objectives, decisions,
 * acceptance - each a list; then its phases, a section each, named by the phase's id as written
 * when it may be a name, and made from it as a heading's name is when not. A pillar the plan
 * leaves empty has no section.
 *
 * <p>A part is where it is by its node's path and its position in the node's leaf: {@link Spot},
 * {@code phases/3:0} - the key its content is asked by.</p>
 *
 * @param root the plan: no name, its label the plan's name
 */
public record PlanTree(Node root) {

    public static final String OBJECTIVES = "objectives";
    public static final String DECISIONS = "decisions";
    public static final String ACCEPTANCE = "acceptance";
    public static final String PHASES = "phases";

    /** What a phase's id must be to be its name as written - a name's grammar. */
    private static final Pattern NAMABLE = Pattern.compile("[A-Za-z0-9._-]{1,48}");

    public PlanTree { Objects.requireNonNull(root, "PlanTree.root"); }

    /** A section: its name in the path - none for the root - its label, its leaf, its named children. */
    public record Node(Optional<Name> name, Label label, List<PlanPart> leaf, List<Node> children) {
        public Node {
            Objects.requireNonNull(name, "Node.name");
            Objects.requireNonNull(label, "Node.label");
            leaf = List.copyOf(Objects.requireNonNull(leaf, "Node.leaf"));
            children = List.copyOf(Objects.requireNonNull(children, "Node.children"));
        }
    }

    /** A part of a leaf, where it is: its node's path, its position in the leaf. */
    public record Spot(String path, int position, PlanPart part) {
        /** Its key: {@code phases/3:0}; in the root's leaf, {@code :0}. */
        public String key() { return path + ":" + position; }
    }

    /** A plan's tree. */
    public static PlanTree of(Plan plan) {
        Objects.requireNonNull(plan, "PlanTree.plan");
        var sections = new ArrayList<Node>();
        if (!plan.objectives().isEmpty()) sections.add(section(OBJECTIVES, "Objectives", new PlanPart.Objectives(plan.objectives())));
        if (!plan.decisions().isEmpty()) sections.add(section(DECISIONS, "Decisions", new PlanPart.Decisions(plan.decisions())));
        if (!plan.acceptance().isEmpty()) sections.add(section(ACCEPTANCE, "Acceptance", new PlanPart.Acceptances(plan.acceptance())));
        if (!plan.phases().isEmpty()) sections.add(phases(plan.phases()));
        return new PlanTree(new Node(Optional.empty(), label(plan.name()), List.of(new PlanPart.Head(plan)), sections));
    }

    private static Node section(String name, String label, PlanPart part) {
        return new Node(Optional.of(Name.of(name)), label(label), List.of(part), List.of());
    }

    /** The phases: a section each, under one with nothing of its own; each knowing where the phases it depends on are. */
    private static Node phases(List<Phase> phases) {
        var siblings = new NodeNames.Siblings();
        var names = new ArrayList<Name>();
        Map<String, Integer> byId = new HashMap<>();   // a phase's id → the first phase with it
        for (int i = 0; i < phases.size(); i++) {
            names.add(siblings.take(nameOf(phases.get(i).id())));
            byId.putIfAbsent(text(phases.get(i).id()), i);
        }
        var nodes = new ArrayList<Node>();
        for (int i = 0; i < phases.size(); i++) {
            Phase p = phases.get(i);
            var after = (p.dependsOn() == null ? List.<Dependency>of() : p.dependsOn()).stream().map(d -> {
                Integer at = byId.get(text(d.phaseId()));
                return at == null ? new PlanPart.After(text(d.phaseId()), "", "", text(d.reason()))
                        : new PlanPart.After(text(d.phaseId()), PHASES + "/" + names.get(at).value(), text(phases.get(at).label()), text(d.reason()));
            }).toList();
            nodes.add(new Node(Optional.of(names.get(i)), label(titleOf(p)), List.of(new PlanPart.Step(p, after)), List.of()));
        }
        return new Node(Optional.of(Name.of(PHASES)), label("Phases"), List.of(), nodes);
    }

    /** A phase's name: its id as written, when it may be a name; else made from it, as a heading's is. */
    static String nameOf(String id) {
        String s = text(id);
        return NAMABLE.matcher(s).matches() ? s : NodeNames.of(s);
    }

    /** A phase's heading: its id and its label - either alone when the other is blank. */
    static String titleOf(Phase p) {
        String id = text(p.id()).strip(), label = text(p.label()).strip();
        if (id.isEmpty()) return label.isEmpty() ? "Phase" : label;
        return label.isEmpty() ? id : id + " — " + label;
    }

    private static Label label(String text) { return new Label(text(text), List.of()); }

    /** A plan's text, never null: what is not said is empty. */
    static String text(String s) { return s == null ? "" : s; }

    /** Every part, in reading order, where it is. */
    public List<Spot> spots() {
        var out = new ArrayList<Spot>();
        walk(root, "", out);
        return List.copyOf(out);
    }

    private static void walk(Node node, String path, List<Spot> out) {
        for (int i = 0; i < node.leaf().size(); i++) out.add(new Spot(path, i, node.leaf().get(i)));
        for (Node child : node.children()) walk(child, pathOf(path, child), out);
    }

    /** Every node's path, in reading order, the root's first. */
    public List<String> paths() {
        var out = new ArrayList<String>();
        paths(root, "", out);
        return List.copyOf(out);
    }

    private static void paths(Node node, String path, List<String> out) {
        out.add(path);
        for (Node child : node.children()) paths(child, pathOf(path, child), out);
    }

    static String pathOf(String parent, Node child) {
        String name = child.name().orElseThrow().value();
        return parent.isEmpty() ? name : parent + "/" + name;
    }
}
