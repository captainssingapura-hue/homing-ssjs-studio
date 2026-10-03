package hue.captains.singapura.js.homing.docview.widgets;

import hue.captains.singapura.js.homing.component.keyboard.FocusPartyModule;
import hue.captains.singapura.js.homing.component.keyboard.KeysModule;
import hue.captains.singapura.js.homing.component.keyboard.focusParties;
import hue.captains.singapura.js.homing.core.DomModule;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.Exportable;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.core.js.DomOpsPartyModule;
import hue.captains.singapura.js.homing.core.js.domOpsParties;
import hue.captains.singapura.js.homing.workspace.content.ContentParamsModule;
import hue.captains.singapura.js.homing.workspace.stage.StageButtonModule;
import hue.captains.singapura.js.homing.workspace.stage.StagePartyModule;

import java.util.List;

/**
 * What every primitive of a doc is: {@code ContentWidget} - made from its type and params alone,
 * it asks its type's content party for its content and draws it, saying so while it waits and
 * when it cannot. Each primitive extends it.
 */
public record ContentWidgetModule() implements DomModule<ContentWidgetModule> {

    public static final ContentWidgetModule INSTANCE = new ContentWidgetModule();

    public record ContentWidget() implements Exportable._Class<ContentWidgetModule> {}

    @Override
    public ImportsFor<ContentWidgetModule> imports() {
        return ImportsFor.<ContentWidgetModule>builder()
                .add(new ModuleImports<>(List.of(new domOpsParties()), DomOpsPartyModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new focusParties()), FocusPartyModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new KeysModule.Keys()), KeysModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new ContentParamsModule.ContentParams()), ContentParamsModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new StagePartyModule.STAGE()), StagePartyModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new ReadingPartyModule.READING()), ReadingPartyModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new StageButtonModule.StageButton()), StageButtonModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new DocWidgetStyles.dw_widget(), new DocWidgetStyles.dw_note(), new DocWidgetStyles.dw_hidden()),
                        DocWidgetStyles.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<ContentWidgetModule> exports() { return new ExportsOf<>(INSTANCE, List.of(new ContentWidget())); }
}
