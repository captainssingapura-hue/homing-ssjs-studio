package hue.captains.singapura.js.homing.docview.widgets;

import hue.captains.singapura.js.homing.design.styles.CastingStyle;
import hue.captains.singapura.js.homing.design.styles.StyleDeclaration;
import hue.captains.singapura.js.homing.design.styling.StyleBundle;
import hue.captains.singapura.js.homing.ui.styles.Base;
import hue.captains.singapura.js.homing.ui.styles.BaseScheme;

import java.util.ArrayList;
import java.util.List;

/**
 * The document widgets' looks as this jar declares them (RFC 0067, appendix The Style Jars): its components' in Base,
 * read from its base module's bundles - the one list - so the per-jar check holds them as it holds a house jar's.
 */
public record DocViewWidgetsStyleDeclaration() implements StyleDeclaration {

    public static final DocViewWidgetsStyleDeclaration INSTANCE = new DocViewWidgetsStyleDeclaration();

    @Override public List<Provided<?, ?>> provided() {
        var looks = new ArrayList<CastingStyle<BaseScheme, Base, ?, ?>>();
        for (StyleBundle<BaseScheme, Base> b : DocViewBaseModule.INSTANCE.bundles()) looks.addAll(b.looks());
        return List.of(new Provided<>(Base.INSTANCE, looks));
    }
}
