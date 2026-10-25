package hue.captains.singapura.js.homing.design.styles.base;

import hue.captains.singapura.js.homing.component.taxonomy.RoleRoot;
import hue.captains.singapura.js.homing.design.styles.CastingStyle;
import hue.captains.singapura.js.homing.design.styles.StylingRes;
import hue.captains.singapura.js.homing.docview.widgets.DocViewComponents;
import hue.captains.singapura.js.homing.ui.styles.Base;
import hue.captains.singapura.js.homing.ui.styles.BaseColours;
import hue.captains.singapura.js.homing.ui.styles.BaseScheme;

/**
 * The document reference in the base layer: ruled off above in the hairline, clear on the page, its corner square, and
 * washed in the accent while a citation has asked for it. Whole: every property its look has (D9).
 */
public record DocReference() implements CastingStyle<BaseScheme, Base, DocViewComponents.DocReference, RoleRoot> {
    @Override public StylingRes<BaseScheme, DocViewComponents.DocReference> style() {
        return new StylingRes.CssContent<>(new BaseColours.Border(), new BaseColours.AccentWashLight());
    }
}
