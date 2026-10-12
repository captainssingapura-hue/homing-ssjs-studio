package hue.captains.singapura.js.homing.docview.widgets;

import hue.captains.singapura.js.homing.core.DomModule;
import hue.captains.singapura.js.homing.core.Exportable;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.ui.elements.CastingsModule;
import hue.captains.singapura.js.homing.ui.elements.LookModule;

import java.util.List;

/**
 * A document's elements in the house's look: {@code DocLooks.wear(el, leaf)} - each the leaf it is (a quote, a block of
 * code, a table and its cells, code, a link, a caption, a tab, a badge of a tone), worn inline on the native element a
 * doc's widget makes, which stays what the browser makes it.
 */
public record DocLooksModule() implements DomModule<DocLooksModule> {

    public static final DocLooksModule INSTANCE = new DocLooksModule();

    public record DocLooks() implements Exportable._Class<DocLooksModule> {}

    @Override
    public ImportsFor<DocLooksModule> imports() {
        return ImportsFor.<DocLooksModule>builder()
                .add(new ModuleImports<>(List.of(new LookModule.Look()), LookModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new CastingsModule.ROLE()), CastingsModule.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<DocLooksModule> exports() { return new ExportsOf<>(INSTANCE, List.of(new DocLooks())); }
}
