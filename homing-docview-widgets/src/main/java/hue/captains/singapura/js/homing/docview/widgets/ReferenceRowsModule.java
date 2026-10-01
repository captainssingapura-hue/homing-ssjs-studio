package hue.captains.singapura.js.homing.docview.widgets;

import hue.captains.singapura.js.homing.core.DomModule;
import hue.captains.singapura.js.homing.core.Exportable;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.relgrid.RelGridStockCellsModule;

import java.util.List;

/** The references table's relation: {@code new ReferenceRows({ branch })} - a doc's references, a row each, read only, its cells stock text cells. */
public record ReferenceRowsModule() implements DomModule<ReferenceRowsModule> {

    public static final ReferenceRowsModule INSTANCE = new ReferenceRowsModule();

    public record ReferenceRows() implements Exportable._Class<ReferenceRowsModule> {}

    @Override
    public ImportsFor<ReferenceRowsModule> imports() {
        return ImportsFor.<ReferenceRowsModule>builder()
                .add(new ModuleImports<>(List.of(new RelGridStockCellsModule.RelGridTextCell()), RelGridStockCellsModule.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<ReferenceRowsModule> exports() { return new ExportsOf<>(INSTANCE, List.of(new ReferenceRows())); }
}
