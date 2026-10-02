package hue.captains.singapura.js.homing.conformance.workbench;

import hue.captains.singapura.js.homing.conformance.export.ConformanceReportSource;
import hue.captains.singapura.js.homing.conformance.workbench.feeds.ComponentsFeed;
import hue.captains.singapura.js.homing.conformance.workbench.feeds.CrateConformanceFeed;
import hue.captains.singapura.js.homing.conformance.workbench.feeds.CrateGraphFeed;
import hue.captains.singapura.js.homing.conformance.workbench.feeds.CratesFeed;
import hue.captains.singapura.js.homing.conformance.workbench.feeds.ReportFeed;
import hue.captains.singapura.js.homing.conformance.workbench.feeds.WorkbenchFeed;
import hue.captains.singapura.js.homing.core.Crate;
import hue.captains.singapura.js.homing.core.CrateEntry;
import hue.captains.singapura.js.homing.designs.HomingDesigns;
import hue.captains.singapura.js.homing.site.Router;
import hue.captains.singapura.js.homing.site.Site;
import hue.captains.singapura.js.homing.site.mpa.Brand;
import hue.captains.singapura.js.homing.site.mpa.StandardMpa;
import hue.captains.singapura.tao.http.action.ActionRegistry;
import hue.captains.singapura.tao.http.action.GetAction;
import hue.captains.singapura.tao.http.action.PostAction;
import hue.captains.singapura.tao.http.config.HostConfig;
import hue.captains.singapura.tao.http.vertx.VertxActionHost;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.RoutingContext;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemAlreadyExistsException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * A conformance studio, put together: the workbenches' page - the group {@code /conformance}, the
 * site's root sending to it - served through a standard MPA under the studio's brand, the feeds
 * the workbenches read beside the site's routes. What it browses is the crates it is given; what
 * it says of their rules is the report their build exported. A launcher says both, and serves it:
 *
 * <pre>
 *   ConformanceStudio.of("Homing · core conformance", CORE_CRATES, ConformanceStudio.exportedReport(MyLauncher.class)).serve(8090);
 * </pre>
 *
 * <p>The MPA serves the crates it browses beside the workbench's own, so the source pane reads a
 * module as it is served. The workbenches keep their states in the browser: the page is told no
 * server keeps them.</p>
 */
public final class ConformanceStudio {

    private final String brand;
    private final List<Crate> topLevel;
    private final StandardMpa mpa;
    private final Site site;
    private final List<WorkbenchFeed> feeds;

    private ConformanceStudio(String brand, List<Crate> topLevel, ConformanceReportSource report) {
        this.brand = Objects.requireNonNull(brand, "ConformanceStudio.brand");
        this.topLevel = List.copyOf(Objects.requireNonNull(topLevel, "ConformanceStudio.topLevel"));
        Objects.requireNonNull(report, "ConformanceStudio.report");
        this.mpa = StandardMpa.of(Brand.of(brand), HomingDesigns.REGISTRY, new Served(this.topLevel));
        Router router = WorkbenchWorkspaces.SITE.router(mpa, WorkbenchApp.INSTANCE, false);
        this.site = new Named("conformance-studio", router);
        this.feeds = List.of(new CratesFeed(this.topLevel), new CrateConformanceFeed(this.topLevel, report), new CrateGraphFeed(this.topLevel),
                new ReportFeed(report), new ComponentsFeed(this.topLevel));
    }

    /** A studio over these crates, saying of their rules what this report says. */
    public static ConformanceStudio of(String brand, List<Crate> topLevel, ConformanceReportSource report) {
        return new ConformanceStudio(brand, topLevel, report);
    }

    public String brand() { return brand; }

    public List<Crate> topLevel() { return topLevel; }

    public StandardMpa mpa() { return mpa; }

    public Site site() { return site; }

    public List<WorkbenchFeed> feeds() { return feeds; }

    /** The site's routes, with the feeds before its catch-all: the host mounts in the order given. */
    public ActionRegistry<RoutingContext> routes() {
        ActionRegistry<RoutingContext> base = mpa.registry(site);
        var gets = new LinkedHashMap<String, GetAction<RoutingContext, ?, ?, ?>>();
        boolean placed = false;
        for (var e : base.getActions().entrySet()) {
            if (!placed && e.getKey().equals("/*")) { feeds.forEach(f -> gets.put(f.route(), f)); placed = true; }
            gets.put(e.getKey(), e.getValue());
        }
        if (!placed) feeds.forEach(f -> gets.put(f.route(), f));
        var getsView = Collections.unmodifiableMap(gets);
        var postsView = Collections.unmodifiableMap(new LinkedHashMap<String, PostAction<RoutingContext, ?, ?, ?>>(base.postActions()));
        return new ActionRegistry<>() {
            @Override public Map<String, GetAction<RoutingContext, ?, ?, ?>> getActions() { return getsView; }
            @Override public Map<String, PostAction<RoutingContext, ?, ?, ?>> postActions() { return postsView; }
        };
    }

    /** Served on a port: the address it opens at printed, a failure ending the process. */
    public Future<HttpServer> serve(int port) {
        return new VertxActionHost(routes(), HostConfig.http(port)).start()
                .onSuccess(s -> System.out.println("[" + brand + "] http://localhost:" + s.actualPort() + "/"))
                .onFailure(err -> { err.printStackTrace(); System.exit(1); });
    }

    /**
     * The report a build exported beside {@code anchor}: {@code conformance-report/} at the root of
     * the classpath entry the anchor's class was loaded from - a directory, or a jar read in place.
     * That entry's own, never another's: a classpath can carry the reports of several builds.
     */
    public static ConformanceReportSource exportedReport(Class<?> anchor) {
        URL where = anchor.getProtectionDomain().getCodeSource().getLocation();
        try {
            Path entry = Path.of(where.toURI());
            Path dir;
            if (Files.isDirectory(entry)) dir = entry.resolve("conformance-report");
            else {
                URI jar = URI.create("jar:" + entry.toUri());
                FileSystem fs;
                try { fs = FileSystems.newFileSystem(jar, Map.of()); }
                catch (FileSystemAlreadyExistsException already) { fs = FileSystems.getFileSystem(jar); }
                dir = fs.getPath("/conformance-report");
            }
            if (!Files.isRegularFile(dir.resolve("report.json")))
                throw new IllegalStateException("no exported conformance report beside " + anchor.getName() + " in " + entry + ": the build's export must run first");
            return new ConformanceReportSource(dir);
        } catch (URISyntaxException | IOException e) {
            throw new IllegalStateException("the exported report could not be opened beside " + anchor.getName() + ": " + where, e);
        }
    }

    /** The site: its name and its router. */
    private record Named(String name, Router router) implements Site {}

    /** What the MPA serves: the workbench, and the crates it browses - their modules read as they are served. */
    private record Served(List<Crate> browsed) implements Crate {
        @Override public String name() { return "conformance-studio"; }
        @Override public List<Crate> requires() {
            var out = new ArrayList<Crate>();
            out.add(ConformanceWorkbenchCrate.INSTANCE);
            for (Crate c : browsed) if (!out.contains(c)) out.add(c);
            return List.copyOf(out);
        }
        @Override public List<CrateEntry> entries() { return List.of(); }
    }
}
