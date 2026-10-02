package hue.captains.singapura.js.homing.conformance.workbench.feeds;

import hue.captains.singapura.js.homing.conformance.export.ConformanceReportSource;
import hue.captains.singapura.js.homing.conformance.report.codec.ConformanceReportCodec;
import hue.captains.singapura.js.homing.conformance.report.codec.ModuleResultCodec;
import hue.captains.singapura.js.homing.conformance.rules.report.ConformanceReport;
import hue.captains.singapura.js.homing.conformance.rules.report.CrateReport;
import hue.captains.singapura.js.homing.conformance.rules.report.ModuleResult;

import java.util.Objects;

/**
 * {@code GET /conformance/report} - the report the build exported, whole: its summary and every
 * module's result, each written through its Java codec, so the wire form is what the generated
 * JS codecs read ({@code ReportCodecsModule}). Read once.
 *
 * <pre>
 *   { summary: ConformanceReport, modules: [ModuleResult] }
 * </pre>
 */
public final class ReportFeed extends WorkbenchFeed {

    public static final String ROUTE = "/conformance/report";

    private final ConformanceReportSource report;
    private volatile String cached;

    public ReportFeed(ConformanceReportSource report) {
        super("conformance-report");
        this.report = Objects.requireNonNull(report, "ReportFeed.report");
    }

    @Override public String route() { return ROUTE; }

    @Override
    protected Body answer(Query query) {
        String local = cached;
        if (local == null) synchronized (this) { if (cached == null) cached = json(); local = cached; }
        return Body.json(local);
    }

    private String json() {
        ConformanceReport summary = report.summary();
        var modules = new StringBuilder("[");
        boolean first = true;
        for (CrateReport crate : summary.crates()) {
            for (String moduleClass : crate.modules()) {
                ModuleResult m = report.module(moduleClass).orElse(null);
                if (m == null) continue;
                if (!first) modules.append(',');
                first = false;
                modules.append(ModuleResultCodec.INSTANCE.transformTo(m));
            }
        }
        return "{\"summary\":" + ConformanceReportCodec.INSTANCE.transformTo(summary) + ",\"modules\":" + modules.append(']') + "}";
    }
}
