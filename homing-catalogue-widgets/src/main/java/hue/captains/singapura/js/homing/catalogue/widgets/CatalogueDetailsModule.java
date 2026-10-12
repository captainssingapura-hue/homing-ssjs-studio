package hue.captains.singapura.js.homing.catalogue.widgets;

import hue.captains.singapura.js.homing.catalogue.widgets.CatalogueWidgetModule.CatalogueWidget;
import hue.captains.singapura.js.homing.component.keyboard.KeyBinding;
import hue.captains.singapura.js.homing.conformance.rules.ConformanceDebt;
import hue.captains.singapura.js.homing.conformance.rules.WidgetDrawsOnlyComponentsRule;
import hue.captains.singapura.js.homing.core.DomModule;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.ImportsFor;
import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.ui.elements.CaptionModule;
import hue.captains.singapura.js.homing.ui.elements.CastingsModule;
import hue.captains.singapura.js.homing.ui.elements.HeadingModule;
import hue.captains.singapura.js.homing.ui.elements.KickerModule;
import hue.captains.singapura.js.homing.ui.elements.LedeModule;
import hue.captains.singapura.js.homing.ui.elements.LinkModule;
import hue.captains.singapura.js.homing.workspace.widgets.SelfContainedWidget;
import hue.captains.singapura.js.homing.workspace.widgets.WidgetStyles;

import java.util.List;

/** The entry that is picked, flat on the sheet it lies on: {@code new CatalogueDetails(container, params)} - its badge, name, summary, and the way to it. */
@ConformanceDebt(rule = WidgetDrawsOnlyComponentsRule.class, findings = 18,
        reason = "It makes its own elements, places elements, wears classes of its own and writes text of its own: it waits for the designed containers, the widget slot and the content components to draw only components.")
public record CatalogueDetailsModule() implements DomModule<CatalogueDetailsModule> {

    public static final CatalogueDetailsModule INSTANCE = new CatalogueDetailsModule();

    /** No keys of its own: a press in it says the reader is reading the entry shown. */
    public record CatalogueDetails() implements SelfContainedWidget<CatalogueDetailsModule> {
        @Override public String summary() { return "The entry that is picked, flat on the sheet it lies on: its badge, its name, its summary, and the way to it."; }
    }

    @Override
    public ImportsFor<CatalogueDetailsModule> imports() {
        return ImportsFor.<CatalogueDetailsModule>builder()
                .add(new ModuleImports<>(List.of(new CatalogueWidget()), CatalogueWidgetModule.INSTANCE))
                // what it says is the house's: a kicker, a heading, a lede, a link - and a caption for the hint
                .add(new ModuleImports<>(List.of(new KickerModule.Kicker(), new KickerModule.KickerFactory()), KickerModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new HeadingModule.Heading(), new HeadingModule.HeadingFactory()), HeadingModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new LedeModule.Lede(), new LedeModule.LedeFactory()), LedeModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new LinkModule.Link(), new LinkModule.LinkFactory()), LinkModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new CaptionModule.Caption(), new CaptionModule.CaptionFactory()), CaptionModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new CastingsModule.ROLE()), CastingsModule.INSTANCE))
                .add(new ModuleImports<>(List.of(new WidgetStyles.wg_scroll()), WidgetStyles.INSTANCE))
                .add(new ModuleImports<>(List.of(new CatalogueStyles.cw_details(), new CatalogueStyles.cw_entry(), new CatalogueStyles.cw_kicker(), new CatalogueStyles.cw_title(),
                        new CatalogueStyles.cw_summary(), new CatalogueStyles.cw_way(), new CatalogueStyles.cw_hint(),
                        new CatalogueStyles.cw_hidden()), CatalogueStyles.INSTANCE))
                .build();
    }

    @Override
    public ExportsOf<CatalogueDetailsModule> exports() { return new ExportsOf<>(INSTANCE, List.of(new CatalogueDetails())); }
}
