package hue.captains.singapura.js.homing.docview.reference;

import hue.captains.singapura.js.homing.studio.base.Doc;
import hue.captains.singapura.js.homing.studio.base.composed.ArticulatedCell;
import hue.captains.singapura.js.homing.studio.base.composed.Articulation;
import hue.captains.singapura.js.homing.studio.base.composed.CodeLanguage;
import hue.captains.singapura.js.homing.studio.base.composed.CodeSegment;
import hue.captains.singapura.js.homing.studio.base.composed.ComposedDoc;
import hue.captains.singapura.js.homing.studio.base.composed.ComposedSegment;
import hue.captains.singapura.js.homing.studio.base.composed.Listable;
import hue.captains.singapura.js.homing.studio.base.composed.MarkdownSegment;
import hue.captains.singapura.js.homing.studio.base.composed.ParagraphSegment;
import hue.captains.singapura.js.homing.studio.base.composed.RelationArticulations;
import hue.captains.singapura.js.homing.studio.base.composed.RelationSegment;
import hue.captains.singapura.js.homing.studio.base.composed.RigidNodeContent;
import hue.captains.singapura.js.homing.studio.base.composed.SvgSegment;
import hue.captains.singapura.js.homing.studio.base.composed.TableSegment;
import hue.captains.singapura.js.homing.studio.base.composed.TypedCodeSegment;
import hue.captains.singapura.js.homing.studio.base.composed.UnorderedListSegment;
import hue.captains.singapura.js.homing.studio.base.composed.graph.RigidNode;
import hue.captains.singapura.js.homing.studio.base.composed.text.Line;
import hue.captains.singapura.js.homing.studio.base.composed.text.Title;
import hue.captains.singapura.js.homing.studio.base.rigid.RigidDoc;
import hue.captains.singapura.js.homing.studio.base.rigid.RigidDocV2;
import hue.captains.singapura.js.homing.studio.base.table.TableData;
import hue.captains.singapura.js.homing.studio.base.table.TableDoc;
import hue.captains.singapura.js.homing.tree.NodeName;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * DocView's reference docs: one of each kind the view takes, each crafted to hold the
 * constructs of its kind - what the view is demonstrated with and validated against.
 */
public final class ReferenceDocs {

    private ReferenceDocs() {}

    /** A rigid doc: its nodes named by their titles, its segments of every rigid kind. */
    public static final RigidDoc RIGID = RigidDoc.root(UUID.fromString("5d2b8f2e-6a41-4c3e-9f0b-1d7e3c9a4b02"), "Rigid reference",
                    "A rigid doc: nodes by their titles, segments of every kind.", "DOC")
            .text("The rigid doc's introduction: its root's own text.")
            .l1("Prose segments")
                .text("A text segment: *emphasis* and `code` in it.")
                .markdown("A markdown segment, its table split out as a part:\n\n| a | b |\n|---|---|\n| 1 | 2 |\n\nAnd prose after it.")
                .l1build()
            .l1("Code and a relation")
                .code("record Node(String title) {}", CodeLanguage.JAVA)
                .relation(List.of("Segment", "Part"), List.of(List.of("CodeSegment", "code"), List.of("RelationSegment", "table")), "Segments and their parts")
                .l2("A child of it")
                    .text("Two levels down.")
                    .l2build()
                .l1build()
            .l1("A picture")
                .svg(ReferenceSvg.INSTANCE, "A heading, and its leaf")
                .l1build()
            .l1("Prose segments")
                .text("A second node titled alike: named with a -2.")
                .l1build()
            .build();

    /** A rigid doc by names: its nodes named by their authors, used as written. */
    public static final RigidDocV2 NAMED_RIGID = RigidDocV2.fromNodes(UUID.fromString("5d2b8f2e-6a41-4c3e-9f0b-1d7e3c9a4b03"), "Named rigid reference",
            "A rigid doc by names: each node's name its author's.", "DOC",
            () -> {
                var root = RigidNode.<String>root("root", new NodeName("named"), new Title("Named rigid reference"));
                var why = root.child("why", new NodeName("why.v2"), new Title("Why names are authored"), 1);
                var how = root.child("how", new NodeName("how_it_reads"), new Title("How it reads"), 2);
                var deep = how.child("deep", new NodeName("deeper"), new Title("Deeper"));
                return List.of(root, why, how, deep);
            },
            (String source) -> switch (source) {
                case "root" -> RigidNodeContent.of(new ParagraphSegment(List.of(new Line.Plain("The named rigid doc's introduction."))));
                case "why" -> RigidNodeContent.captioned("A caption, shown first", new MarkdownSegment("A name its author gave - `why.v2` - used as written, dot and all."));
                case "how" -> RigidNodeContent.of(new TypedCodeSegment("root.child(source, new NodeName(\"deeper\"))", CodeLanguage.JAVA, Optional.empty()));
                default -> RigidNodeContent.of(new ParagraphSegment(List.of(new Line.Plain("Two levels down, by name."))));
            });

    /** A table as a doc of its own, its cells articulated. */
    static final TableDoc TABLE = new TableDoc(UUID.fromString("5d2b8f2e-6a41-4c3e-9f0b-1d7e3c9a4b06"), "Kinds and their trees", "",
            new TableData(List.of(TableData.Cell.of("Kind"), TableData.Cell.of("Its tree")), List.of(
                    List.of(TableData.Cell.of("Markdown"), TableData.Cell.badged("its headings", TableData.Badge.SUCCESS)),
                    List.of(new TableData.Cell("A rigid doc and a composed one: their nodes, and their titled segments", 2, 1, null, TableData.Align.CENTER)))));

    /** A composed doc held inside the composed reference. */
    static final ComposedDoc INNER = ComposedDoc.of(UUID.fromString("5d2b8f2e-6a41-4c3e-9f0b-1d7e3c9a4b07"), "A doc inside a doc", "", "DOC",
            List.of(new MarkdownSegment("Held by the composed reference.")));

    /** A composed doc: titled segments start nodes, untitled ones belong to the node before them. */
    public static final ComposedDoc COMPOSED = ComposedDoc.of(UUID.fromString("5d2b8f2e-6a41-4c3e-9f0b-1d7e3c9a4b04"), "Composed reference",
            "A composed doc: titled segments start nodes, untitled ones follow.", "DOC", List.of(
                    new MarkdownSegment("The composed doc's introduction: untitled segments before the first titled one are the root's.", Optional.empty()),
                    new ParagraphSegment(List.of(new Line.Plain("A paragraph segment, also the root's."))),
                    new MarkdownSegment("A titled markdown segment starts a node.\n\n```mermaid\nflowchart LR\n  segment --> node\n```", Optional.of("Titled markdown")),
                    new UnorderedListSegment(List.<Listable>of(new ParagraphSegment(List.of(new Line.Plain("an untitled list"))),
                            new ParagraphSegment(List.of(new Line.Plain("belongs to the node before it"))))),
                    new RelationSegment(List.of("Construct", "Status"), List.of(List.of("titled segments", "nodes"), List.of("untitled segments", "follow")),
                            Optional.of(new Line.Plain("Composed constructs")),
                            new RelationArticulations(List.of(new ArticulatedCell(0, 1, new Articulation(Optional.of(TableData.Badge.SUCCESS),
                                    Optional.empty(), Optional.of(Articulation.Emphasis.STRONG)))))),
                    new MarkdownSegment("A table held as a doc of its own, and an SVG: their captions are captions, not headings - they belong here.",
                            Optional.of("Figures and tables")),
                    new TableSegment(TABLE),
                    new SvgSegment(ReferenceSvg.INSTANCE),
                    new CodeSegment("echo composed", "bash", Optional.of("A titled code segment")),
                    new MarkdownSegment("A composed doc held inside this one - shown as a link, or grafted in, is an open question.",
                            Optional.of("A doc inside a doc")),
                    new ComposedSegment(INNER)));

    /** Every reference doc, by the path segment it is placed at, in the order they are listed. */
    public static final Map<String, Doc> ALL;

    static {
        var all = new LinkedHashMap<String, Doc>();
        all.put("markdown", MarkdownReference.INSTANCE);
        all.put("rigid", RIGID);
        all.put("named-rigid", NAMED_RIGID);
        all.put("composed", COMPOSED);
        ALL = Collections.unmodifiableMap(all);
    }
}
