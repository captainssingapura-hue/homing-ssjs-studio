package hue.captains.singapura.js.homing.conformance.workbench.feeds;

import hue.captains.singapura.js.homing.core.Crate;
import hue.captains.singapura.js.homing.core.CrateEntry;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * {@code GET /conformance/crates} - the studio's own crates and their modules, as the navigator
 * shows them and the summary reads them: crate, then the crate's packages - its common package
 * prefix stripped, single-child chains collapsed into one node, so a large crate stays navigable
 * - then the module leaves. Only the crates the studio was given: the crates they require are
 * the graph's, not the navigator's.
 *
 * <pre>
 *   { crates: n, modules: n, children: [node] }
 *   node   { key, label, kind: "crate" | "package" | "module", children: [node], ... }
 *   crate  key "crate:&lt;name&gt;"           modules, prefix, requires: [name]
 *   package key "package:&lt;crate&gt;/&lt;path&gt;"  crate, modules
 *   module key "module:&lt;fqcn&gt;"           crate, form, fqcn
 * </pre>
 */
public final class CratesFeed extends WorkbenchFeed {

    public static final String ROUTE = "/conformance/crates";

    private final List<Crate> topLevel;
    private volatile String cached;

    public CratesFeed(List<Crate> topLevel) {
        super("conformance-crates");
        this.topLevel = List.copyOf(Objects.requireNonNull(topLevel, "CratesFeed.topLevel"));
    }

    @Override public String route() { return ROUTE; }

    @Override
    protected Body answer(Query query) {
        String local = cached;
        if (local == null) synchronized (this) { if (cached == null) cached = json().encode(); local = cached; }
        return Body.json(local);
    }

    /** The whole tree. */
    public JsonObject json() {
        var crates = new JsonArray();
        int modules = 0;
        for (Crate c : topLevel) { crates.add(crate(c)); modules += c.entries().size(); }
        return new JsonObject().put("crates", topLevel.size()).put("modules", modules).put("children", crates);
    }

    private static JsonObject crate(Crate crate) {
        Trie trie = new Trie();
        for (CrateEntry e : crate.entries()) trie.insert(segments(packageOf(e.moduleClass())), e);
        var prefix = new StringBuilder();
        while (trie.entries.isEmpty() && trie.children.size() == 1) {
            var only = trie.children.entrySet().iterator().next();
            if (!prefix.isEmpty()) prefix.append('.');
            prefix.append(only.getKey());
            trie = only.getValue();
        }
        return node("crate:" + crate.name(), crate.name(), "crate")
                .put("modules", crate.entries().size())
                .put("prefix", prefix.toString())
                .put("requires", new JsonArray(crate.requires().stream().map(Crate::name).toList()))
                .put("children", trie.nodes(crate.name(), ""));
    }

    private static JsonObject node(String key, String label, String kind) {
        return new JsonObject().put("key", key).put("label", label).put("kind", kind);
    }

    private static String packageOf(String fqcn) {
        int dot = fqcn.lastIndexOf('.');
        return dot < 0 ? "" : fqcn.substring(0, dot);
    }

    private static List<String> segments(String pkg) { return pkg.isEmpty() ? List.of() : List.of(pkg.split("\\.")); }

    /** A crate's modules by their packages: an edge a package segment, entries the modules in that package. */
    private static final class Trie {
        final Map<String, Trie> children = new LinkedHashMap<>();
        final List<CrateEntry> entries = new ArrayList<>();

        void insert(List<String> segs, CrateEntry e) {
            if (segs.isEmpty()) { entries.add(e); return; }
            children.computeIfAbsent(segs.get(0), k -> new Trie()).insert(segs.subList(1, segs.size()), e);
        }

        int modules() {
            int n = entries.size();
            for (Trie t : children.values()) n += t.modules();
            return n;
        }

        /** The packages here, a single-child chain collapsed into one, then the modules. */
        JsonArray nodes(String crate, String prefix) {
            var out = new JsonArray();
            for (var ch : children.entrySet()) {
                String label = ch.getKey();
                Trie t = ch.getValue();
                while (t.entries.isEmpty() && t.children.size() == 1) {
                    var only = t.children.entrySet().iterator().next();
                    label = label + "." + only.getKey();
                    t = only.getValue();
                }
                String path = prefix.isEmpty() ? label : prefix + "." + label;
                out.add(node("package:" + crate + "/" + path, label, "package")
                        .put("crate", crate).put("modules", t.modules()).put("children", t.nodes(crate, path)));
            }
            for (CrateEntry e : entries) {
                out.add(node("module:" + e.moduleClass(), e.module().getClass().getSimpleName(), "module")
                        .put("crate", crate).put("form", e.form().name()).put("fqcn", e.moduleClass()).put("children", new JsonArray()));
            }
            return out;
        }
    }
}
