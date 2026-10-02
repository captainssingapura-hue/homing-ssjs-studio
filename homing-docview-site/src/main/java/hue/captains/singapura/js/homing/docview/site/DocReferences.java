package hue.captains.singapura.js.homing.docview.site;

import hue.captains.singapura.js.homing.docview.tree.Citations;
import hue.captains.singapura.js.homing.docview.tree.DocRef;
import hue.captains.singapura.js.homing.docview.tree.DocTree;
import hue.captains.singapura.js.homing.studio.base.Doc;
import hue.captains.singapura.js.homing.studio.base.DocReference;
import hue.captains.singapura.js.homing.studio.base.ExternalReference;
import hue.captains.singapura.js.homing.studio.base.ImageReference;
import hue.captains.singapura.js.homing.studio.base.Reference;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A doc's references, resolved for its page: each it declares, in the order declared, with the
 * sections that cite it. A reference to a doc goes to that doc's authentic path - its reading
 * page's, wherever the site places it ({@link DocPlaces}) - or, no catalogue placing it, nowhere,
 * and says so; an external one goes to its address. Never an id.
 *
 * <p>And the law a doc keeps: every name it cites, it declares ({@link #undeclared}).</p>
 */
public final class DocReferences {

    private DocReferences() {}

    /** What a reference whose doc could not be read says of it. */
    static final String MISSING = "The doc it names was not yet made when this one was: a cycle between the two docs' constants.";

    /** The doc's references, resolved against where the site's docs are read. */
    public static List<DocRef> of(Doc doc, DocTree tree, DocPlaces places) {
        Map<String, List<String>> cited = Citations.of(tree);
        return doc.references().stream().map(r -> resolve(r, citing(tree, cited.getOrDefault(r.name(), List.of())), places)).toList();
    }

    /** The sections at those paths, each with its heading as the doc gives it - the root's, the doc's title. */
    static List<DocRef.Citing> citing(DocTree tree, List<String> paths) {
        return paths.stream().map(p -> new DocRef.Citing(p, tree.node(p).map(n -> n.label().text()).orElse(p))).toList();
    }

    /** The names the doc cites and does not declare: a citation that goes nowhere, which a doc must not have. */
    public static List<String> undeclared(Doc doc, DocTree tree) {
        Set<String> declared = doc.references().stream().map(Reference::name).collect(Collectors.toSet());
        return Citations.of(tree).keySet().stream().filter(n -> !declared.contains(n)).toList();
    }

    static DocRef resolve(Reference r, List<DocRef.Citing> citedIn, DocPlaces places) {
        return switch (r) {
            // a doc whose constant was not yet made when this one was - a cycle between the two
            // docs' classes - is no doc at all here: said to go nowhere, by its name
            case DocReference d when d.target() == null -> new DocRef(d.name(), DocRef.UNPLACED, d.name(), MISSING, "", citedIn);
            case DocReference d -> places.pathOf(d.target())
                    .map(at -> new DocRef(d.name(), DocRef.DOC, d.target().title(), d.target().summary(), at, citedIn))
                    .orElseGet(() -> new DocRef(d.name(), DocRef.UNPLACED, d.target().title(), d.target().summary(), "", citedIn));
            case ExternalReference e -> new DocRef(e.name(), DocRef.EXTERNAL, e.label(), e.description(), e.url(), citedIn);
            case ImageReference i -> new DocRef(i.name(), DocRef.IMAGE, i.alt(), i.caption(), "", citedIn);
        };
    }
}
