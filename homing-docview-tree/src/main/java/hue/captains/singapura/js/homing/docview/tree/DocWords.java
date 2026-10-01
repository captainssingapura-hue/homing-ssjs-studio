package hue.captains.singapura.js.homing.docview.tree;

import java.util.ArrayList;
import java.util.List;

/**
 * A doc's words, as a reader reads them: what of each part is text - prose, a table's cells and
 * caption, a figure's caption - with its code taken out, a fence or a span between backticks, which
 * quotes rather than says. What a law over a doc's text reads: its citations, its links, anything a
 * doc must not say outside code.
 */
public final class DocWords {

    private DocWords() {}

    /** Words, and the path of the section they are in - the root's "". */
    public record Words(String path, String text) {}

    /** Every part's words, in reading order, code taken out. */
    public static List<Words> of(DocTree tree) {
        var out = new ArrayList<Words>();
        for (DocTree.Spot s : tree.spots()) {
            for (String text : Citations.texts(s.part())) out.add(new Words(s.path(), Citations.withoutCode(text)));
        }
        return List.copyOf(out);
    }
}
