package hue.captains.singapura.js.homing.docview.widgets;

import hue.captains.singapura.js.homing.core.EsModule;
import hue.captains.singapura.js.homing.core.Exportable;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;

import java.util.List;

/**
 * The secretary of a doc reader's reading party: a member's {@code ReadHere} goes to the steward,
 * and the last widget read is kept for whoever looks. Pure.
 */
public record ReadingSecretaryModule() implements EsModule<ReadingSecretaryModule> {

    public static final ReadingSecretaryModule INSTANCE = new ReadingSecretaryModule();

    public record ReadingSecretary() implements Exportable._Constant<ReadingSecretaryModule> {}

    @Override public ImportsFor<ReadingSecretaryModule> imports() { return ImportsFor.noImports(); }

    @Override
    public ExportsOf<ReadingSecretaryModule> exports() { return new ExportsOf<>(INSTANCE, List.of(new ReadingSecretary())); }
}
