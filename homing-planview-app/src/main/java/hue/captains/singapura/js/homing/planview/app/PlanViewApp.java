package hue.captains.singapura.js.homing.planview.app;

import hue.captains.singapura.js.homing.core.AppModule;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.core.ParamCodec;
import hue.captains.singapura.js.homing.core.QueryString;
import hue.captains.singapura.js.homing.docview.app.DocDeskModule;
import hue.captains.singapura.js.homing.planview.widgets.PlanHeadContentModule;
import hue.captains.singapura.js.homing.planview.widgets.PlanHeadModule;
import hue.captains.singapura.js.homing.planview.widgets.PlanHeadStewardModule;
import hue.captains.singapura.js.homing.planview.widgets.PlanListContentModule;
import hue.captains.singapura.js.homing.planview.widgets.PlanListModule;
import hue.captains.singapura.js.homing.planview.widgets.PlanListStewardModule;
import hue.captains.singapura.js.homing.planview.widgets.PlanPhaseContentModule;
import hue.captains.singapura.js.homing.planview.widgets.PlanPhaseModule;
import hue.captains.singapura.js.homing.planview.widgets.PlanPhaseStewardModule;
import hue.captains.singapura.js.homing.workspace.content.ContentSecretaryModule;
import hue.captains.singapura.js.homing.workspace.parties.MessagingPartyModule;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * A plan, viewed, as a page: the same app for every plan, told which by its params - the plan's
 * authentic path. It is DocView's desk with a plan's widgets offered beside a doc's: the contents
 * beside the plan - its pillars and its phases its sections - every part a widget asking its
 * content party.
 */
public record PlanViewApp() implements AppModule<PlanViewApp.Params, PlanViewApp> {

    public static final PlanViewApp INSTANCE = new PlanViewApp();

    /**
     * The plan to view: its authentic path, and its name.
     *
     * @param plan  the authentic path, empty when the page is reached without one
     * @param title the plan's name, said while it is read
     */
    public record Params(String plan, String title) implements AppModule._Param {
        public Params {
            if (plan == null) plan = "";
            Objects.requireNonNull(title, "Params.title");
        }
    }

    record appMain() implements AppModule._AppMain<Params, PlanViewApp> {}

    public static final ParamCodec<Params> CODEC = new ParamCodec<>() {
        @Override public Decoded<Params> from(Map<String, List<String>> query) {
            String title = QueryString.first(query, "title");
            return Decoded.ok(new Params(QueryString.first(query, "plan"), title == null ? "" : title));
        }
        @Override public Map<String, List<String>> to(Params params) {
            var out = QueryString.params();
            QueryString.put(out, "plan", params.plan());
            QueryString.put(out, "title", params.title());
            return out;
        }
    };

    @Override public String title()      { return "PlanView"; }
    @Override public String simpleName() { return "plan-view"; }
    @Override public Class<Params> paramsType() { return Params.class; }
    @Override public ParamCodec<Params> paramCodec() { return CODEC; }

    @Override
    public ImportsFor<PlanViewApp> imports() {
        return ImportsFor.<PlanViewApp>builder()
                // the desk, which reads the plan and lays it out
                .add(new ModuleImports<>(List.of(new DocDeskModule.DocDesk()), DocDeskModule.INSTANCE))
                // a plan's content parties: the runtime, their secretary, the plan's types and stewards
                .add(new ModuleImports<>(List.of(new MessagingPartyModule.MessagingParty()), MessagingPartyModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new ContentSecretaryModule.ContentSecretary()), ContentSecretaryModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new PlanHeadContentModule.PLAN_HEAD()), PlanHeadContentModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new PlanListContentModule.PLAN_LIST()), PlanListContentModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new PlanPhaseContentModule.PLAN_PHASE()), PlanPhaseContentModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new PlanHeadStewardModule.PlanHeadSteward()), PlanHeadStewardModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new PlanListStewardModule.PlanListSteward()), PlanListStewardModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new PlanPhaseStewardModule.PlanPhaseSteward()), PlanPhaseStewardModule.INSTANCE))
                // a plan's widgets
                .add(new ModuleImports<>(List.of(new PlanHeadModule.PlanHead()), PlanHeadModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new PlanListModule.PlanList()), PlanListModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new PlanPhaseModule.PlanPhase()), PlanPhaseModule.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<PlanViewApp> exports() { return new ExportsOf<>(INSTANCE, List.of(new appMain())); }
}
