// =============================================================================
// WorkbenchApp — the conformance studio's workbenches, as a page: the grouped
// workspace page (GroupedWorkspacePage), handed the workbenches' manifests -
// WORKBENCH_WORKSPACES, each generated from its declaration in Java - the group
// they are filed in, WORKBENCH_GROUPS, and their first states,
// WORKBENCH_ARRANGEMENTS, laid out when the page writes a log that holds
// nothing yet. The route names the group, the anchor the workbench.
// =============================================================================

function appMain(el, params) {
    GroupedWorkspacePage.main(el, params, WORKBENCH_WORKSPACES, WORKBENCH_GROUPS, WORKBENCH_ARRANGEMENTS);
}
