package hue.captains.singapura.js.homing.catalogue.widgets;

import hue.captains.singapura.js.homing.core.EsModule;
import hue.captains.singapura.js.homing.core.Exportable;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;

import java.util.List;

/**
 * The catalogue party's secretary: {@code initial} and {@code behavior(state, envelope)
 * → { newState, actions }} - which entry is picked, said when it changes and to a member
 * that asks; an asking to open said to every member, the host among them. Diligent.
 */
public record CatalogueChoiceSecretaryModule() implements EsModule<CatalogueChoiceSecretaryModule> {

    public record CatalogueChoiceSecretary() implements Exportable._Constant<CatalogueChoiceSecretaryModule> {}

    public static final CatalogueChoiceSecretaryModule INSTANCE = new CatalogueChoiceSecretaryModule();

    @Override public ImportsFor<CatalogueChoiceSecretaryModule> imports() { return ImportsFor.noImports(); }

    @Override
    public ExportsOf<CatalogueChoiceSecretaryModule> exports() { return new ExportsOf<>(INSTANCE, List.of(new CatalogueChoiceSecretary())); }
}
