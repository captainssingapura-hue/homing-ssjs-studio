package hue.captains.singapura.js.homing.docview.widgets;

import hue.captains.singapura.js.homing.component.keyboard.Key;
import hue.captains.singapura.js.homing.component.keyboard.KeyBinding;
import hue.captains.singapura.js.homing.component.keyboard.NeedKeyboard;
import hue.captains.singapura.js.homing.core.DomModule;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.relgrid.RelGridModule;
import hue.captains.singapura.js.homing.server.HrefManager;
import hue.captains.singapura.js.homing.workspace.widgets.SelfContainedWidget;

import java.util.List;

/** A doc's references: {@code new DocReferences(container, { doc })} - a table on the relation grid, a row each; Enter opens what one names. */
public record DocReferencesModule() implements DomModule<DocReferencesModule> {

    public static final DocReferencesModule INSTANCE = new DocReferencesModule();

    public record DocReferences() implements SelfContainedWidget<DocReferencesModule>, NeedKeyboard {
        @Override public String summary() { return "A doc's references as a table: each its name, what it names, where it goes, how often it is cited; Enter opens it."; }

        /** Its one key of its own, natively, while a cell of the grid has the focus; the grid's are the grid's. */
        public static final List<KeyBinding> KEYS = List.of(KeyBinding.of(Key.ENTER, "open what the reference names"));
        @Override public List<KeyBinding> keys() { return KEYS; }
    }

    @Override
    public ImportsFor<DocReferencesModule> imports() {
        return ImportsFor.<DocReferencesModule>builder()
                .add(new ModuleImports<>(List.of(new ContentWidgetModule.ContentWidget()), ContentWidgetModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new ReferencesContentModule.REFERENCES()), ReferencesContentModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new ReferenceRowsModule.ReferenceRows()), ReferenceRowsModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new RelGridModule.RelGrid()), RelGridModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new HrefManager.HrefManagerInstance()), HrefManager.INSTANCE))
                .add(new ModuleImports<>(List.of(new DocWidgetStyles.dw_refs(), new DocWidgetStyles.dw_refs_head(), new DocWidgetStyles.dw_refs_title(),
                        new DocWidgetStyles.dw_refs_count(), new DocWidgetStyles.dw_refs_box(), new DocWidgetStyles.dw_note(), new DocWidgetStyles.dw_hidden()),
                        DocWidgetStyles.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<DocReferencesModule> exports() { return new ExportsOf<>(INSTANCE, List.of(new DocReferences())); }
}
