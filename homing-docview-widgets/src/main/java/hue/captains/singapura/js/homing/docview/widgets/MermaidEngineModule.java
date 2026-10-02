package hue.captains.singapura.js.homing.docview.widgets;

import hue.captains.singapura.js.homing.core.DomModule;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.Exportable;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;

import java.util.List;

/**
 * Mermaid's engine, for the diagram steward: {@code MermaidEngine.draw(source)} - the library imported
 * the first time a diagram is drawn and never before, diagrams drawn one at a time in the theme the
 * page's ground asks for. A DOM module: mermaid draws in a scratch element of the page's, and the
 * ground is read from the page.
 */
public record MermaidEngineModule() implements DomModule<MermaidEngineModule> {

    public static final MermaidEngineModule INSTANCE = new MermaidEngineModule();

    public record MermaidEngine() implements Exportable._Class<MermaidEngineModule> {}

    @Override
    public ImportsFor<MermaidEngineModule> imports() {
        return ImportsFor.<MermaidEngineModule>builder()
                .add(new ModuleImports<>(List.of(new MermaidLibraryModule.MERMAID_LIBRARY(), new MermaidLibraryModule.MERMAID_PALETTE()), MermaidLibraryModule.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<MermaidEngineModule> exports() { return new ExportsOf<>(INSTANCE, List.of(new MermaidEngine())); }
}
