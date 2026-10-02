package hue.captains.singapura.js.homing.docview.widgets;

import hue.captains.singapura.js.homing.core.DomModule;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.server.HrefManager;
import hue.captains.singapura.js.homing.workspace.widgets.SelfContainedWidget;

import java.util.List;

/**
 * A doc's references: {@code new DocReferences(container, params)} - a list, as the doc's last
 * section shows it: each reference its title, a link where it goes; what it is; its summary; where
 * it goes, and the sections that cite it.
 */
public record DocReferencesModule() implements DomModule<DocReferencesModule> {

    public static final DocReferencesModule INSTANCE = new DocReferencesModule();

    public record DocReferences() implements SelfContainedWidget<DocReferencesModule> {
        @Override public String summary() { return "A doc's references as a list: each its title, a link where it goes; what it is; its summary; where it is cited."; }
    }

    @Override
    public ImportsFor<DocReferencesModule> imports() {
        return ImportsFor.<DocReferencesModule>builder()
                .add(new ModuleImports<>(List.of(new ContentWidgetModule.ContentWidget()), ContentWidgetModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new ReferencesContentModule.REFERENCES()), ReferencesContentModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new HrefManager.HrefManagerInstance()), HrefManager.INSTANCE))
                .add(new ModuleImports<>(List.of(new DocWidgetStyles.dw_refs(), new DocWidgetStyles.dw_ref(), new DocWidgetStyles.dw_ref_current(),
                        new DocWidgetStyles.dw_ref_head(), new DocWidgetStyles.dw_ref_title(), new DocWidgetStyles.dw_ref_summary(),
                        new DocWidgetStyles.dw_ref_meta(), new DocWidgetStyles.dw_ref_where(), new DocWidgetStyles.dw_link(), new DocWidgetStyles.dw_badge(),
                        new DocWidgetStyles.dw_warning(), new DocWidgetStyles.dw_dim(), new DocWidgetStyles.dw_note()), DocWidgetStyles.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<DocReferencesModule> exports() { return new ExportsOf<>(INSTANCE, List.of(new DocReferences())); }
}
