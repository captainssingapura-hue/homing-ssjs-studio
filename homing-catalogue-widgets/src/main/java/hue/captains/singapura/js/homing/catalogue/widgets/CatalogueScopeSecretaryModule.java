package hue.captains.singapura.js.homing.catalogue.widgets;

import hue.captains.singapura.js.homing.core.EsModule;
import hue.captains.singapura.js.homing.core.Exportable;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;

import java.util.List;

/**
 * The secretary of a composed widget's own catalogue party - a scope, linked to the
 * party the widget is given: the catalogue secretary within it, and at its edge a pick
 * and an asking to open going up, and a pick from above taken as the scope's own.
 */
public record CatalogueScopeSecretaryModule() implements EsModule<CatalogueScopeSecretaryModule> {

    public record CatalogueScopeSecretary() implements Exportable._Constant<CatalogueScopeSecretaryModule> {}

    public static final CatalogueScopeSecretaryModule INSTANCE = new CatalogueScopeSecretaryModule();

    @Override
    public ImportsFor<CatalogueScopeSecretaryModule> imports() {
        return ImportsFor.<CatalogueScopeSecretaryModule>builder()
                .add(new ModuleImports<>(List.of(new CatalogueChoiceSecretaryModule.CatalogueChoiceSecretary()), CatalogueChoiceSecretaryModule.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<CatalogueScopeSecretaryModule> exports() { return new ExportsOf<>(INSTANCE, List.of(new CatalogueScopeSecretary())); }
}
