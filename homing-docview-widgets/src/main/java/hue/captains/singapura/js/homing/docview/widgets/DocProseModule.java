package hue.captains.singapura.js.homing.docview.widgets;

import hue.captains.singapura.js.homing.core.DomModule;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.workspace.widgets.SelfContainedWidget;

import java.util.List;

/** A doc's prose part: {@code new DocProse(container, params)} - markdown text, drawn from its tokens. */
public record DocProseModule() implements DomModule<DocProseModule> {

    public static final DocProseModule INSTANCE = new DocProseModule();

    public record DocProse() implements SelfContainedWidget<DocProseModule> {
        @Override public String summary() { return "A doc's prose part: markdown text asked of the prose party by its params, drawn from its tokens."; }
    }

    @Override
    public ImportsFor<DocProseModule> imports() {
        return ImportsFor.<DocProseModule>builder()
                .add(new ModuleImports<>(List.of(new ContentWidgetModule.ContentWidget()), ContentWidgetModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new MarkdownDomModule.MarkdownDom()), MarkdownDomModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new ProseContentModule.PROSE()), ProseContentModule.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<DocProseModule> exports() { return new ExportsOf<>(INSTANCE, List.of(new DocProse())); }
}
