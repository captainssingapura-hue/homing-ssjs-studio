package hue.captains.singapura.js.homing.catalogue.site;

import hue.captains.singapura.js.homing.core.AppModule;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.core.ParamCodec;
import hue.captains.singapura.js.homing.core.QueryString;
import hue.captains.singapura.js.homing.core.js.DomOpsPartyModule;
import hue.captains.singapura.js.homing.core.js.domOpsParty;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * A catalogue's listing as a page of the site's MPA: the {@link CatalogueListingModule}
 * of the catalogue at {@code path}, under the chrome every page of the site wears.
 * A site makes one per catalogue through {@link AppListing}; the path is stamped
 * into the page, never read off its address - the address is the catalogue's own.
 */
public record CatalogueListingApp() implements AppModule<CatalogueListingApp.Params, CatalogueListingApp> {

    public static final CatalogueListingApp INSTANCE = new CatalogueListingApp();

    /** The catalogue's address, mount and all. */
    public record Params(String path) implements AppModule._Param {
        public Params { Objects.requireNonNull(path, "Params.path"); }
    }

    record appMain() implements AppModule._AppMain<Params, CatalogueListingApp> {}

    public static final ParamCodec<Params> CODEC = new ParamCodec<>() {
        @Override public Decoded<Params> from(Map<String, List<String>> query) {
            String path = QueryString.first(query, "path");
            return Decoded.ok(new Params(path == null || path.isBlank() ? "/" : path));
        }
        @Override public Map<String, List<String>> to(Params params) { return QueryString.of("path", params.path()); }
    };

    @Override public String title()      { return "Catalogue"; }
    @Override public String simpleName() { return "catalogue-listing"; }
    @Override public Class<Params> paramsType() { return Params.class; }
    @Override public ParamCodec<Params> paramCodec() { return CODEC; }

    @Override
    public ImportsFor<CatalogueListingApp> imports() {
        return ImportsFor.<CatalogueListingApp>builder()
                .add(new ModuleImports<>(List.of(new domOpsParty()), DomOpsPartyModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new CatalogueListingModule.CatalogueListing()), CatalogueListingModule.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<CatalogueListingApp> exports() { return new ExportsOf<>(INSTANCE, List.of(new appMain())); }
}
