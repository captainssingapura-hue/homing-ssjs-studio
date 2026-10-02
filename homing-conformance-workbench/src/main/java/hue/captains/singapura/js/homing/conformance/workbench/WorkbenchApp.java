package hue.captains.singapura.js.homing.conformance.workbench;

import hue.captains.singapura.js.homing.core.AppModule;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.core.ParamCodec;
import hue.captains.singapura.js.homing.workspace.site.GroupedWorkspacePageModule;

import java.util.List;

/**
 * The workbenches as a page of any standard MPA: the grouped workspace page, handed the
 * workbenches' manifests, their group and their first states. Its params are a grouped page's -
 * the group its route names, and whether the server keeps its states.
 */
public record WorkbenchApp() implements AppModule<GroupedWorkspacePageModule.Params, WorkbenchApp> {

    public static final WorkbenchApp INSTANCE = new WorkbenchApp();

    record appMain() implements AppModule._AppMain<GroupedWorkspacePageModule.Params, WorkbenchApp> {}

    @Override public String title()      { return "Conformance"; }
    @Override public String simpleName() { return "conformance-workbench"; }
    @Override public Class<GroupedWorkspacePageModule.Params> paramsType() { return GroupedWorkspacePageModule.Params.class; }
    @Override public ParamCodec<GroupedWorkspacePageModule.Params> paramCodec() { return GroupedWorkspacePageModule.CODEC; }

    @Override
    public ImportsFor<WorkbenchApp> imports() {
        return ImportsFor.<WorkbenchApp>builder()
                .add(new ModuleImports<>(List.of(new GroupedWorkspacePageModule.GroupedWorkspacePage()), GroupedWorkspacePageModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new WorkbenchWorkspacesModule.WORKBENCH_WORKSPACES()), WorkbenchWorkspacesModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new WorkbenchGroupsModule.WORKBENCH_GROUPS()), WorkbenchGroupsModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new WorkbenchArrangementsModule.WORKBENCH_ARRANGEMENTS()), WorkbenchArrangementsModule.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<WorkbenchApp> exports() { return new ExportsOf<>(INSTANCE, List.of(new appMain())); }
}
