package hue.captains.singapura.js.homing.docview.widgets;

import hue.captains.singapura.js.homing.core.DomModule;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.workspace.widgets.SelfContainedWidget;

import java.util.List;

/** A doc's code part: {@code new DocCode(container, params)} - one widget for every language, its renderer by the language. */
public record DocCodeModule() implements DomModule<DocCodeModule> {

    public static final DocCodeModule INSTANCE = new DocCodeModule();

    public record DocCode() implements SelfContainedWidget<DocCodeModule> {
        @Override public String summary() { return "A doc's code part: a language and its source, drawn by the renderer registered for the language, or as its source."; }
    }

    @Override
    public ImportsFor<DocCodeModule> imports() {
        return ImportsFor.<DocCodeModule>builder()
                .add(new ModuleImports<>(List.of(new ContentWidgetModule.ContentWidget()), ContentWidgetModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new CodeContentModule.CODE()), CodeContentModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new DocWidgetStyles.dw_lang(), new DocWidgetStyles.dw_pre()), DocWidgetStyles.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<DocCodeModule> exports() { return new ExportsOf<>(INSTANCE, List.of(new DocCode())); }
}
