package hue.captains.singapura.js.homing.docview.widgets;

import hue.captains.singapura.js.homing.component.taxonomy.Component;
import hue.captains.singapura.js.homing.component.taxonomy.ComponentModule;

import java.util.List;

/** The document widgets' components as a module (RFC 0067, D46): what an instance that shows documents registers. */
public record DocViewComponentModule() implements ComponentModule {

    public static final DocViewComponentModule INSTANCE = new DocViewComponentModule();

    @Override public List<? extends Component<?>> components() { return List.of(DocViewComponents.DocReference.INSTANCE); }
}
