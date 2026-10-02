package hue.captains.singapura.js.homing.docview.gallery;

import hue.captains.singapura.js.homing.catalogue.site.CatalogueRoutes;
import hue.captains.singapura.js.homing.docview.site.DocRoutes;
import hue.captains.singapura.tao.http.config.HostConfig;
import hue.captains.singapura.tao.http.vertx.VertxActionHost;

/**
 * Serves {@link GallerySite} through its MPA, with the catalogue's route and DocView's - what the
 * stewards read the reference docs from - before the site's catch-all.
 * {@code mvn -o -pl homing-docview-gallery exec:java}, on 8107 unless {@code -Ddocview.gallery.port} says otherwise.
 */
public final class GalleryServer {

    private static final int PORT = Integer.getInteger("docview.gallery.port", 8107);

    private GalleryServer() {}

    public static void main(String[] args) {
        var routes = DocRoutes.with(CatalogueRoutes.with(GallerySite.MPA.registry(GallerySite.INSTANCE), GallerySite.ROUTER), GallerySite.ROUTER);
        new VertxActionHost(routes, HostConfig.http(PORT)).start()
                .onSuccess(s -> System.out.println("[GalleryServer] http://localhost:" + s.actualPort() + "/"))
                .onFailure(err -> { err.printStackTrace(); System.exit(1); });
    }
}
