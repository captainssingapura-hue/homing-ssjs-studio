package hue.captains.singapura.js.homing.docview.widgets;

import hue.captains.singapura.js.homing.component.taxonomy.ComponentModule;
import hue.captains.singapura.js.homing.design.styles.base.DocReference;
import hue.captains.singapura.js.homing.design.styling.BaseModule;
import hue.captains.singapura.js.homing.design.styling.StyleBundle;
import hue.captains.singapura.js.homing.ui.styles.Base;
import hue.captains.singapura.js.homing.ui.styles.BaseScheme;

import java.util.List;

/**
 * The document widgets' components as an instance of the house registers them (RFC 0067, D50, D51): their component
 * module, each component with its look in Base. A site that shows documents hands it to the house's styling
 * ({@code HouseStyles.INSTANCE.styling(List.of(..., DocViewBaseModule.INSTANCE))}), so a document's references are in its
 * taxonomy and drawn in every style it serves.
 */
public record DocViewBaseModule() implements BaseModule<BaseScheme, Base> {

    public static final DocViewBaseModule INSTANCE = new DocViewBaseModule();

    @Override public Base style() { return Base.INSTANCE; }

    @Override public ComponentModule components() { return DocViewComponentModule.INSTANCE; }

    @Override public List<StyleBundle<BaseScheme, Base>> bundles() {
        return List.of(new StyleBundle<>(DocViewComponents.DocReference.INSTANCE, List.of(new DocReference())));
    }
}
