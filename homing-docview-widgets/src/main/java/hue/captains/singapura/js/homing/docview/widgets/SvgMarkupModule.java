package hue.captains.singapura.js.homing.docview.widgets;

import hue.captains.singapura.js.homing.core.DomModule;
import hue.captains.singapura.js.homing.core.ExportsOf;
import hue.captains.singapura.js.homing.core.Exportable;
import hue.captains.singapura.js.homing.core.ImportsFor;

import java.util.List;

/** SVG markup read into an element, nothing in it that runs kept: {@code SvgMarkup.xml(markup)}, {@code SvgMarkup.html(markup)}. */
public record SvgMarkupModule() implements DomModule<SvgMarkupModule> {

    public static final SvgMarkupModule INSTANCE = new SvgMarkupModule();

    public record SvgMarkup() implements Exportable._Class<SvgMarkupModule> {}

    @Override public ImportsFor<SvgMarkupModule> imports() { return ImportsFor.noImports(); }

    @Override
    public ExportsOf<SvgMarkupModule> exports() { return new ExportsOf<>(INSTANCE, List.of(new SvgMarkup())); }
}
