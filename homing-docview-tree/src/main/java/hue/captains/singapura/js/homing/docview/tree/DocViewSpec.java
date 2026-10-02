package hue.captains.singapura.js.homing.docview.tree;

import hue.captains.singapura.js.homing.workspace.groups.core.models.WidgetKind;
import hue.captains.singapura.js.homing.workspace.groups.core.models.WorkspaceKind;
import hue.captains.singapura.js.homing.workspace.groups.core.models.WorkspaceSpec;

import java.util.Set;

/** DocView as a workspace: the widget types its tree places - one per part type. */
public record DocViewSpec() implements WorkspaceSpec {

    public static final DocViewSpec INSTANCE = new DocViewSpec();

    /** The part types, each a widget type. */
    public static final Set<String> TYPES = Set.of("prose", "code", "table", "image");

    @Override public WorkspaceKind workspaceKind() { return WorkspaceKind.of("doc-view"); }

    @Override
    public Set<WidgetKind> widgetKinds() {
        return Set.of(WidgetKind.of("prose"), WidgetKind.of("code"), WidgetKind.of("table"), WidgetKind.of("image"));
    }
}
