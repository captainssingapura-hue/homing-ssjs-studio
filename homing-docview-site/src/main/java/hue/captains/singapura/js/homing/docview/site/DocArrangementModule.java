package hue.captains.singapura.js.homing.docview.site;

import hue.captains.singapura.js.homing.core.EsModule;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.Exportable;
import hue.captains.singapura.js.homing.core.ImportsFor;

import java.util.List;

/**
 * A doc's arrangement, made on the page: {@code DocArrangement.of(tree, doc)} - from the tree the
 * doc's payload brings, each part of a leaf a widget of its type, its params the doc and the part's
 * key. Pure; the same arrangement Java makes of the tree.
 */
public record DocArrangementModule() implements EsModule<DocArrangementModule> {

    public static final DocArrangementModule INSTANCE = new DocArrangementModule();

    public record DocArrangement() implements Exportable._Class<DocArrangementModule> {}

    @Override public ImportsFor<DocArrangementModule> imports() { return ImportsFor.noImports(); }

    @Override
    public ExportsOf<DocArrangementModule> exports() { return new ExportsOf<>(INSTANCE, List.of(new DocArrangement())); }
}
