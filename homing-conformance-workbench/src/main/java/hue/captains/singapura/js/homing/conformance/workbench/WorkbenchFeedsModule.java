package hue.captains.singapura.js.homing.conformance.workbench;

import hue.captains.singapura.js.homing.conformance.workbench.feeds.ComponentsFeed;
import hue.captains.singapura.js.homing.conformance.workbench.feeds.CrateConformanceFeed;
import hue.captains.singapura.js.homing.conformance.workbench.feeds.CrateGraphFeed;
import hue.captains.singapura.js.homing.conformance.workbench.feeds.CratesFeed;
import hue.captains.singapura.js.homing.conformance.workbench.feeds.ReportFeed;
import hue.captains.singapura.js.homing.core.EsModule;
import hue.captains.singapura.js.homing.core.Exportable;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;

import java.util.List;

/**
 * Where the workbench's widgets read from, and each read once a page: {@code WorkbenchFeeds} -
 * a feed by its name, its routes the Java feeds' own ({@link WorkbenchRoutesModule}); a crate
 * node by its key, from the crates feed; a module's source, as it is served.
 */
public record WorkbenchFeedsModule() implements EsModule<WorkbenchFeedsModule> {

    public static final WorkbenchFeedsModule INSTANCE = new WorkbenchFeedsModule();

    public record WorkbenchFeeds() implements Exportable._Class<WorkbenchFeedsModule> {}

    /** The feeds' routes, by the name the widgets ask by. */
    static final List<String[]> ROUTES = List.of(
            new String[] { "crates", CratesFeed.ROUTE },
            new String[] { "crateConformance", CrateConformanceFeed.ROUTE },
            new String[] { "crateGraph", CrateGraphFeed.ROUTE },
            new String[] { "report", ReportFeed.ROUTE },
            new String[] { "components", ComponentsFeed.ROUTE },
            new String[] { "module", "/module" });

    @Override
    public ImportsFor<WorkbenchFeedsModule> imports() {
        return ImportsFor.<WorkbenchFeedsModule>builder()
                .add(new ModuleImports<>(List.of(new WorkbenchRoutesModule.WORKBENCH_ROUTES()), WorkbenchRoutesModule.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<WorkbenchFeedsModule> exports() { return new ExportsOf<>(INSTANCE, List.of(new WorkbenchFeeds())); }
}
