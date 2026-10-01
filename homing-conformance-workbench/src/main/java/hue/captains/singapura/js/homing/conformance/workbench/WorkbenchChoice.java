package hue.captains.singapura.js.homing.conformance.workbench;

import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.workspace.parties.PartyType;

import java.util.List;

/**
 * What a workbench party carries: which node of the workbench the widgets that meet in it are
 * about - a crate, a package, a module; a vehicle, a family, a component - and the asking to open
 * one. A node travels as its key, as the feed names it, so {@code to} is always one.
 *
 * <p>A member does - {@link Pick}, {@link CurrentRequested}, {@link Open}; the party says -
 * {@link Picked}, {@link Opening}. What opening a node means is the panes' to say: the source
 * pane shows a module that is opened, and nothing else does.</p>
 */
public sealed interface WorkbenchChoice {
    /** A member picked this node. */
    record Pick(String to) implements WorkbenchChoice {}
    /** A member asks which node is picked - one that joins late - and is answered alone, when one is. */
    record CurrentRequested() implements WorkbenchChoice {}
    /** A person asked to open this node: an Enter, a double press. */
    record Open(String to) implements WorkbenchChoice {}
    /** The party says: this node is picked. */
    record Picked(String to) implements WorkbenchChoice {}
    /** The party says: this node is opened. */
    record Opening(String to) implements WorkbenchChoice {}

    /**
     * The type: {@code workbench}, its identity on a page - its constant served as
     * {@code WORKBENCH} - and a root instance's secretary, {@code WorkbenchChoiceSecretary}.
     */
    PartyType<WorkbenchChoice> TYPE = new PartyType<>("workbench", WorkbenchChoice.class)
            .servedFrom(new ModuleImports<>(List.of(new WorkbenchChoiceModule.WORKBENCH()), WorkbenchChoiceModule.INSTANCE))
            .withSecretary(new ModuleImports<>(List.of(new WorkbenchChoiceSecretaryModule.WorkbenchChoiceSecretary()), WorkbenchChoiceSecretaryModule.INSTANCE));
}
