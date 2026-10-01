package hue.captains.singapura.js.homing.conformance.workbench.feeds;

import hue.captains.singapura.js.homing.conformance.export.ConformanceReportSource;
import hue.captains.singapura.js.homing.conformance.rules.CrateClosure;
import hue.captains.singapura.js.homing.conformance.rules.CrateConformance;
import hue.captains.singapura.js.homing.conformance.rules.report.FindingReport;
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
 * {@code GET /conformance/crate-conformance} - the two dimensions of conformance, for the
 * panes: a crate's integrity ({@link CrateConformance} - its orphans and its illegal imports)
 * and the rule findings the build exported (read from the report, never run here). A module's
 * findings are its layering's and its rules', together; a crate's rule findings are its
 * modules', rolled up - the crate is where they aggregate. Computed once.
 *
 * <pre>
 *   { ok, crates: { name: { name, modules: n, ok, orphans: [..], illegalImports: [..], ruleFindings: [..] } },
 *         modules: { fqcn: { moduleClass, crate, form, ok, findings: [..] } } }
 * </pre>
 */
public final class CrateConformanceFeed extends WorkbenchFeed {

    public static final String ROUTE = "/conformance/crate-conformance";

    private final List<Crate> topLevel;
    private final ConformanceReportSource report;
    private volatile String cached;

    public CrateConformanceFeed(List<Crate> topLevel, ConformanceReportSource report) {
        super("conformance-crate-conformance");
        this.topLevel = List.copyOf(Objects.requireNonNull(topLevel, "CrateConformanceFeed.topLevel"));
        this.report = Objects.requireNonNull(report, "CrateConformanceFeed.report");
    }

    @Override public String route() { return ROUTE; }

    @Override
    protected Body answer(Query query) {
        String local = cached;
        if (local == null) synchronized (this) { if (cached == null) cached = json().encode(); local = cached; }
        return Body.json(local);
    }

    /** Both dimensions, by crate and by module. */
    public JsonObject json() {
        List<Crate> closure = CrateClosure.of(topLevel);
        CrateConformance.Result base = CrateConformance.evaluate(closure);
        Map<String, List<String>> rulesByModule = new LinkedHashMap<>();
        for (Crate c : closure) {
            for (CrateEntry entry : c.entries()) {
                String fqcn = entry.moduleClass();
                report.module(fqcn).ifPresent(mr -> {
                    for (FindingReport f : mr.findings()) rulesByModule.computeIfAbsent(fqcn, k -> new ArrayList<>()).add(f.rule() + ": " + f.message());
                });
            }
        }
        var crates = new JsonObject();
        var modules = new JsonObject();
        boolean allOk = true;
        for (Crate c : closure) {
            CrateConformance.CrateResult cr = base.crates().get(c.name());
            var crateRules = new ArrayList<String>();
            for (CrateEntry entry : c.entries()) {
                String fqcn = entry.moduleClass();
                CrateConformance.ModuleResult mr = base.modules().get(fqcn);
                var findings = new ArrayList<>(mr == null ? List.<String>of() : mr.findings());
                List<String> rules = rulesByModule.getOrDefault(fqcn, List.of());
                findings.addAll(rules);
                crateRules.addAll(rules);
                modules.put(fqcn, new JsonObject().put("moduleClass", fqcn).put("crate", c.name()).put("form", mr == null ? "" : mr.form())
                        .put("ok", findings.isEmpty()).put("findings", new JsonArray(findings)));
            }
            boolean ok = cr != null && cr.ok() && crateRules.isEmpty();
            allOk &= ok;
            crates.put(c.name(), new JsonObject().put("name", c.name()).put("modules", c.entries().size()).put("ok", ok)
                    .put("orphans", new JsonArray(cr == null ? List.of() : cr.orphans()))
                    .put("illegalImports", new JsonArray(cr == null ? List.of() : cr.illegalImports()))
                    .put("ruleFindings", new JsonArray(crateRules)));
        }
        return new JsonObject().put("ok", allOk).put("crates", crates).put("modules", modules);
    }
}
