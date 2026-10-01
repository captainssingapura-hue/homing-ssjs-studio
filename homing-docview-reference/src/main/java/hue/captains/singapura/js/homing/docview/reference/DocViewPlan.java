package hue.captains.singapura.js.homing.docview.reference;

import hue.captains.singapura.js.homing.studio.base.tracker.Acceptance;
import hue.captains.singapura.js.homing.studio.base.tracker.Decision;
import hue.captains.singapura.js.homing.studio.base.tracker.DecisionStatus;
import hue.captains.singapura.js.homing.studio.base.tracker.Dependency;
import hue.captains.singapura.js.homing.studio.base.tracker.Metric;
import hue.captains.singapura.js.homing.studio.base.tracker.Objective;
import hue.captains.singapura.js.homing.studio.base.tracker.Phase;
import hue.captains.singapura.js.homing.studio.base.tracker.PhaseStatus;
import hue.captains.singapura.js.homing.studio.base.tracker.Plan;
import hue.captains.singapura.js.homing.studio.base.tracker.Task;

import java.util.List;

/**
 * DocView's own phases, as a plan: the reference plan - every pillar filled, a phase of every
 * status but one, tasks, metrics, dependencies - and the demo of the phase that shows plans. Its
 * execution doc is the RFC that designs DocView, which lives in the self-studio: on a site that
 * does not place it, it is said to be nowhere.
 */
public record DocViewPlan() implements Plan {

    /** The RFC 0066 Episode 3 appendix that designs DocView: its doc's id, in the self-studio. */
    public static final String RFC = "07b38326-0bb4-4bc0-aea3-4007b89c2111";

    @Override public String name()     { return "DocView on a tree placement"; }
    @Override public String summary()  { return "DocView's seven phases, from the tree placement to plans"; }
    @Override public String kicker()   { return "RFC 0066 · E3"; }
    @Override public String subtitle() { return "A doc is shown by a special-purpose workspace: a fixed set of top-level widgets over a **tree placement**, a generic engine that places widgets at the nodes of a fixed tree."; }
    @Override public String executionDoc() { return RFC; }

    @Override
    public List<Objective> objectives() {
        return List.of(
                new Objective("One view, one tree", "Markdown and rigid docs shown through one view, from one tree."),
                new Objective("A pure model", "The doc model is data: nothing about viewing lives in it."),
                new Objective("Generic machinery", "The tree placement and the content parties know nothing of docs, so plans and other trees use them unchanged."),
                new Objective("An address for every section", "The doc's authentic path and the section's path, surviving edits elsewhere in the doc."));
    }

    @Override
    public List<Decision> decisions() {
        return List.of(
                new Decision("Q1", "A composed doc's `ComposedSegment` - a doc inside a doc: a link to it, or its tree grafted in?",
                        "A link, until a reader asks for more.", null, DecisionStatus.OPEN, "", ""),
                new Decision("Q2", "`EmbeddedSegment` - an ES-module app embedded in a doc: shown as a workspace widget once it is one, and until then?",
                        "Said, with its module's name, until it is a widget.", null, DecisionStatus.OPEN, "", ""),
                new Decision("Q3", "The names cut at 40 characters: do markdown authors need a way to name a heading?",
                        "Not yet: a cut name carries a digest of the whole, so it stays unique and stable.", null, DecisionStatus.OPEN, "", ""),
                new Decision("Q4", "The Graphviz library is fetched from outside, as mermaid's is: which build, and served from where?",
                        "Served as mermaid is: loaded only when a dot diagram is wanted.", null, DecisionStatus.OPEN, "", "Deferred with the dot renderer."),
                new Decision("Q5", "Anonymous tables and code in markdown: nameless children with their own widgets, or parts in a flow?",
                        "Parts in a flow.", "Parts in a flow: a node's leaf is a list of unnamed widgets.", DecisionStatus.RESOLVED,
                        "A table or a block of code has no heading to be named by; as a part of its section's leaf it is placed and asked for like any other.", ""));
    }

    @Override
    public List<Acceptance> acceptance() {
        return List.of(
                new Acceptance("Every reference resolves", "Every reference in the corpora resolves to a placed navigable, or the build names the one that does not.", true),
                new Acceptance("The gate holds", "Every crate passes the conformance gate, strictly.", true),
                new Acceptance("Plans need nothing new below", "Nothing in the tree placement or the content parties changed for plans.", true),
                new Acceptance("The self-studio reads in DocView", "Its docs and plans placed on the new stack, the old viewers retired.", false));
    }

    @Override
    public List<Phase> phases() {
        return List.of(
                new Phase("1", "The tree placement", "A generic engine that places widgets at the nodes of a fixed tree.",
                        "Headings are structure, as a split grid's tab bars are; each node carries one slot, its leaf, for widgets that never learn where they sit.",
                        PhaseStatus.DONE,
                        List.of(new Task("TreePlacement and its arrangement, engine `tree`", true), new Task("TreeLayout: sections, indent, fold", true),
                                new Task("TreeToc on a relation tree, in a split grid", true), new Task("The bench's `/tree` page", true)),
                        List.of(), "The bench's `/tree` page: a tree laid out, folded, followed by its contents.", "", "Medium", "", List.of()),
                new Phase("2", "Content parties", "One party per content type, a pure secretary, one steward per hierarchy.",
                        "A widget asks for its content with its params; the secretary keeps what was fetched and routes; the steward alone does the fetching.",
                        PhaseStatus.DONE,
                        List.of(new Task("The route to the steward, hired lazily", true), new Task("The content-party pattern: messages and the pure secretary", true),
                                new Task("The flow widget", true)),
                        List.of(new Dependency("1", "Widgets placed before they ask")),
                        "Headless tests: kept content, pending askers, one steward per hierarchy. The bench's request count: each item fetched once.", "", "Medium", "", List.of()),
                new Phase("3", "The doc tree", "Markdown, rigid and composed docs made into one tree of named headings.",
                        "A doc's tree is built headlessly, once; its names by the naming rules; its arrangement and content functions owned by the viewer.",
                        PhaseStatus.DONE,
                        List.of(new Task("`MarkdownSource` in the model", true), new Task("The builder, with the naming rules", true),
                                new Task("The arrangement, the content functions, the routes", true)),
                        List.of(new Dependency("2", "Its parts are asked for through the parties")),
                        "Tests over the corpora: every tree builds, every invariant holds.", "", "Large", "",
                        List.of(new Metric("Names cut at 40 characters, with a digest", "-", "477", ""))),
                new Phase("4", "DocView", "The desk: the contents beside the doc, every part a widget.",
                        "The fixed split grid, the TOC and the main view; prose, and code as source; the page and its fragment.",
                        PhaseStatus.DONE,
                        List.of(new Task("The desk and the page's fragment", true), new Task("The TOC and the main view", true), new Task("Prose, and code as source", true)),
                        List.of(new Dependency("3", "It lays out the doc's tree")),
                        "Scripted checks in the browser: the TOC follows the reading position and moves it; a section's address opens at it.", "", "Large", "",
                        List.of(new Metric("Reference docs read in DocView", "0", "4", "+4"))),
                new Phase("5", "Heavy widgets and the Stage", "Diagrams, images and tables, and a modal stage to see them larger.",
                        "The Stage swaps the very widget between its place and itself, asking the placement alone; the widget never learns it moved.",
                        PhaseStatus.IN_PROGRESS,
                        List.of(new Task("The Stage and the stage party", true), new Task("Mermaid, lazily, in the design's colours", true), new Task("Zoom and pan, in place and on the stage", true),
                                new Task("The image widget", true), new Task("The dot renderer", false), new Task("The table on a relation grid", false)),
                        List.of(new Dependency("4", "Its widgets are placed on the desk")),
                        "A renderer that fails shows the source; Escape on the Stage gives the keys back to the widget that asked.", "", "Large",
                        "Deferred, with mermaid's live theme switch: the dot renderer, and the table on a relation grid.",
                        List.of(new Metric("Diagram languages drawn", "0", "1", "+1"))),
                new Phase("6", "References", "Citations, and the doc's references, each opening its target at its authentic path.",
                        "A reference to a doc goes to where the site reads it - never an id; one no catalogue places is said to go nowhere.",
                        PhaseStatus.DONE,
                        List.of(new Task("The references widget, the doc's last section", true), new Task("Citations shown where they are cited", true),
                                new Task("Each reference resolved through the catalogue", true)),
                        List.of(new Dependency("4", "The references are a section of the desk's tree")),
                        "Every reference in the corpora resolves to a placed navigable, or the build names the one that does not.", "", "Medium", "", List.of()),
                new Phase("7", "Plans", "A plan as a tree on the same placement, with its own widgets.",
                        "A plan's pillars are its sections, each phase a section of its own; a phase's details are folded in its widget, unfolded on demand.",
                        PhaseStatus.IN_PROGRESS,
                        List.of(new Task("The plan model as data, out of the old stack", true), new Task("A plan's tree and payload", true),
                                new Task("The plan's widgets: its head, its lists, its phases", true), new Task("The desk shared by docs and plans", true),
                                new Task("The self-studio's plans read here", false)),
                        List.of(new Dependency("4", "A plan is read on the same desk"), new Dependency("6", "Its execution doc goes where the site reads it")),
                        "Nothing in the tree placement or the content parties changed for it.", "", "Medium", "", List.of()));
    }
}
