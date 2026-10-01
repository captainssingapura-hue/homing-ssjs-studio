package hue.captains.singapura.js.homing.conformance.workbench;

import hue.captains.singapura.js.homing.core.EsModule;
import hue.captains.singapura.js.homing.core.Exportable;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;

import java.util.List;

/**
 * The workbench party's secretary: {@code WorkbenchChoiceSecretary} - which node is picked, said
 * to every member when it changes and to a member that asks; an asking to open, said to all. Pure.
 */
public record WorkbenchChoiceSecretaryModule() implements EsModule<WorkbenchChoiceSecretaryModule> {

    public static final WorkbenchChoiceSecretaryModule INSTANCE = new WorkbenchChoiceSecretaryModule();

    public record WorkbenchChoiceSecretary() implements Exportable._Constant<WorkbenchChoiceSecretaryModule> {}

    @Override public ImportsFor<WorkbenchChoiceSecretaryModule> imports() { return ImportsFor.noImports(); }

    @Override
    public ExportsOf<WorkbenchChoiceSecretaryModule> exports() { return new ExportsOf<>(INSTANCE, List.of(new WorkbenchChoiceSecretary())); }
}
