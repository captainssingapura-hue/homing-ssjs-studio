package hue.captains.singapura.js.homing.docview.app;

import hue.captains.singapura.js.homing.core.EsModule;
import hue.captains.singapura.js.homing.core.Exportable;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;

import java.util.List;

/**
 * The steward of a doc reader's reading party: {@code ReadingSteward.over({ placement, contents })}
 * - the class the party hires. Told that the reader pressed a widget with no keys of its own, it
 * takes the reader to that widget's section in the contents, and gives the contents the keys there.
 */
public record ReadingStewardModule() implements EsModule<ReadingStewardModule> {

    public static final ReadingStewardModule INSTANCE = new ReadingStewardModule();

    public record ReadingSteward() implements Exportable._Class<ReadingStewardModule> {}

    @Override public ImportsFor<ReadingStewardModule> imports() { return ImportsFor.noImports(); }

    @Override
    public ExportsOf<ReadingStewardModule> exports() { return new ExportsOf<>(INSTANCE, List.of(new ReadingSteward())); }
}
