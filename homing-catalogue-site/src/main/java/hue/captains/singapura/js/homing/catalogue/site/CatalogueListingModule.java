package hue.captains.singapura.js.homing.catalogue.site;

import hue.captains.singapura.js.homing.component.BranchComponent;
import hue.captains.singapura.js.homing.core.DomModule;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.server.HrefManager;

import java.util.List;

/**
 * A catalogue as a site shows it: {@code new CatalogueListing(branch, path)} - its
 * head, then what is under it as tiles, each a link to its authentic path, read
 * from {@link VertexGetAction#PATH}. Knows no app.
 */
public record CatalogueListingModule() implements DomModule<CatalogueListingModule> {

    public static final CatalogueListingModule INSTANCE = new CatalogueListingModule();

    public record CatalogueListing() implements BranchComponent<CatalogueListingModule> {
        @Override public String summary() { return "A catalogue as a site shows it: its head, then its catalogues and its pages, each a link to its authentic path."; }
    }

    @Override
    public ImportsFor<CatalogueListingModule> imports() {
        return ImportsFor.<CatalogueListingModule>builder()
                .add(new ModuleImports<>(List.of(new HrefManager.HrefManagerInstance()), HrefManager.INSTANCE))
                .add(new ModuleImports<>(List.of(new ListingStyles.cl_root(), new ListingStyles.cl_kicker(), new ListingStyles.cl_title(),
                        new ListingStyles.cl_summary(), new ListingStyles.cl_section_title(), new ListingStyles.cl_grid(), new ListingStyles.cl_tile(),
                        new ListingStyles.cl_tile_name(), new ListingStyles.cl_tile_badge(), new ListingStyles.cl_tile_summary(),
                        new ListingStyles.cl_empty(), new ListingStyles.cl_failed(), new ListingStyles.cl_hidden()), ListingStyles.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<CatalogueListingModule> exports() { return new ExportsOf<>(INSTANCE, List.of(new CatalogueListing())); }
}
