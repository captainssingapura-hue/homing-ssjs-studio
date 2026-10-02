package hue.captains.singapura.js.homing.catalogue.demo;

import hue.captains.singapura.js.homing.catalogue.site.CatalogueRoutes;
import hue.captains.singapura.js.homing.docview.site.DocRoutes;
import hue.captains.singapura.tao.http.config.HostConfig;
import hue.captains.singapura.tao.http.vertx.VertxActionHost;

/**
 * Serves {@link DemoSite} through its MPA, with the catalogue's route - what
 * each listing reads its catalogue from - before the site's catch-all.
 * {@code mvn -o -pl homing-catalogue-demo exec:java}, on 8106 unless
 * {@code -Dcatalogue.demo.port} says otherwise.
 */
public final class DemoServer {

    private static final int PORT = Integer.getInteger("catalogue.demo.port", 8106);

    private DemoServer() {}

    public static void main(String[] args) {
        var routes = DocRoutes.with(CatalogueRoutes.with(DemoSite.MPA.registry(DemoSite.INSTANCE), DemoSite.ROUTER), DemoSite.ROUTER);
        new VertxActionHost(routes, HostConfig.http(PORT)).start()
                .onSuccess(s -> System.out.println("[DemoServer] http://localhost:" + s.actualPort() + "/"))
                .onFailure(err -> { err.printStackTrace(); System.exit(1); });
    }
}
