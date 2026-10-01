package hue.captains.singapura.js.homing.docview.tree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Where a doc cites its references: every {@code [label](#ref:name)} in its parts - prose, a
 * table's cells and its caption, a figure's caption; never code, which quotes - by the name it
 * cites, with the sections it is cited in, each once, in reading order. Read off the tree, so a
 * citation is found wherever the doc's kind put it: a markdown doc's text, a rigid doc's, a
 * composed doc's segments.
 */
public final class Citations {

    private Citations() {}

    /** A citation as markdown writes one: a link whose target is {@code #ref:} and a name. */
    static final Pattern CITE = Pattern.compile("\\]\\(#ref:([^)\\s]+)\\)");

    /** The doc's citations: each name cited, and the sections citing it - by path, the root's "". */
    public static Map<String, List<String>> of(DocTree tree) {
        var found = new LinkedHashMap<String, LinkedHashSet<String>>();
        for (DocTree.Spot s : tree.spots()) {
            for (String text : texts(s.part())) {
                Matcher m = CITE.matcher(text);
                while (m.find()) found.computeIfAbsent(m.group(1), k -> new LinkedHashSet<>()).add(s.path());
            }
        }
        var out = new LinkedHashMap<String, List<String>>();
        found.forEach((name, paths) -> out.put(name, List.copyOf(paths)));
        return Collections.unmodifiableMap(out);
    }

    /** What of a part may cite: its words, wherever they are. */
    private static List<String> texts(Part part) {
        return switch (part) {
            case Part.Prose p -> List.of(p.text());
            case Part.Code c -> List.of();
            case Part.Image i -> List.of(i.caption());
            case Part.Table t -> {
                var texts = new ArrayList<String>();
                texts.add(t.caption());
                t.rows().forEach(r -> r.cells().forEach(c -> texts.add(c.text())));
                yield texts;
            }
        };
    }
}
