package hue.captains.singapura.js.homing.catalogue.widgets;

import hue.captains.singapura.js.homing.core.DomModule;
import hue.captains.singapura.js.homing.core.Exportable;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.ui.elements.BadgeModule;
import hue.captains.singapura.js.homing.ui.elements.CastingsModule;

import java.util.List;

/** A catalogue tree's row: {@code CatalogueRowCell} - the entry's icon and name, then its badge; a cell of the relation tree's contract. */
public record CatalogueRowCellModule() implements DomModule<CatalogueRowCellModule> {

    public static final CatalogueRowCellModule INSTANCE = new CatalogueRowCellModule();

    public record CatalogueRowCell() implements Exportable._Class<CatalogueRowCellModule> {}

    @Override
    public ImportsFor<CatalogueRowCellModule> imports() {
        return ImportsFor.<CatalogueRowCellModule>builder()
                // its badge the house's, cast as what sorts the entry
                .add(new ModuleImports<>(List.of(new BadgeModule.Badge(), new BadgeModule.BadgeFactory()), BadgeModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new CastingsModule.ROLE()), CastingsModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new CatalogueStyles.cw_row(), new CatalogueStyles.cw_row_name(),
                        new CatalogueStyles.cw_row_badge()), CatalogueStyles.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<CatalogueRowCellModule> exports() { return new ExportsOf<>(INSTANCE, List.of(new CatalogueRowCell())); }
}
