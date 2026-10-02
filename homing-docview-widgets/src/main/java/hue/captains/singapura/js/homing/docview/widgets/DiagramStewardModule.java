package hue.captains.singapura.js.homing.docview.widgets;

import hue.captains.singapura.js.homing.core.DomModule;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.Exportable;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.workspace.content.ContentParamsModule;

import java.util.List;

/**
 * The steward of a doc's diagrams: {@code new DiagramSteward(tell)} - each diagram's source read from
 * the doc, drawn by the engine for its language in the background, told Loaded when it is drawn or
 * Failed with why. A DOM module because its engines are: drawing happens on the page.
 */
public record DiagramStewardModule() implements DomModule<DiagramStewardModule> {

    public static final DiagramStewardModule INSTANCE = new DiagramStewardModule();

    public record DiagramSteward() implements Exportable._Class<DiagramStewardModule> {}

    @Override
    public ImportsFor<DiagramStewardModule> imports() {
        return ImportsFor.<DiagramStewardModule>builder()
                .add(new ModuleImports<>(List.of(new ContentParamsModule.ContentParams()), ContentParamsModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new DocSourcesModule.DocSources()), DocSourcesModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new MermaidEngineModule.MermaidEngine()), MermaidEngineModule.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<DiagramStewardModule> exports() { return new ExportsOf<>(INSTANCE, List.of(new DiagramSteward())); }
}
