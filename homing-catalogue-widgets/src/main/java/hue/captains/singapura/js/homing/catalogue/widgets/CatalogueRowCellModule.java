package hue.captains.singapura.js.homing.catalogue.widgets;

import hue.captains.singapura.js.homing.core.DomModule;
import hue.captains.singapura.js.homing.core.Exportable;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;

import java.util.List;

/** A catalogue tree's row: {@code CatalogueRowCell} - the entry's icon and name, then its badge; a cell of the relation tree's contract. */
public record CatalogueRowCellModule() implements DomModule<CatalogueRowCellModule> {

    public static final CatalogueRowCellModule INSTANCE = new CatalogueRowCellModule();

    public record CatalogueRowCell() implements Exportable._Class<CatalogueRowCellModule> {}

    @Override
    public ImportsFor<CatalogueRowCellModule> imports() {
        return ImportsFor.<CatalogueRowCellModule>builder()
                .add(new ModuleImports<>(List.of(new CatalogueStyles.cw_row(), new CatalogueStyles.cw_row_name(),
                        new CatalogueStyles.cw_row_badge()), CatalogueStyles.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<CatalogueRowCellModule> exports() { return new ExportsOf<>(INSTANCE, List.of(new CatalogueRowCell())); }
}
