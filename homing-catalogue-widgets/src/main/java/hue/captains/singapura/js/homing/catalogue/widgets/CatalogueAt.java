package hue.captains.singapura.js.homing.catalogue.widgets;

import hue.captains.singapura.js.homing.core.QueryString;
import hue.captains.singapura.js.homing.workspace.widgets.WidgetParams;
import hue.captains.singapura.js.homing.workspace.widgets.WidgetQuery;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * The params of a catalogue widget that shows from a catalogue down: the catalogue's
 * authentic path - {@code /} for the site's own. Whether a catalogue is there is the
 * site's to say when the widget asks; the params only say where to look.
 */
public record CatalogueAt(String at) implements WidgetParams {

    /** From the site's own catalogue. */
    public static final CatalogueAt ROOT = new CatalogueAt("/");

    public CatalogueAt {
        Objects.requireNonNull(at, "CatalogueAt.at");
        if (!at.startsWith("/")) throw new IllegalArgumentException("CatalogueAt.at '" + at + "': an authentic path, from /");
    }

    /** {@code at=/kitchen/soups}; absent, the site's own. */
    public record Query() implements WidgetQuery<CatalogueAt> {

        @Override
        public Read<CatalogueAt> from(Map<String, List<String>> query) {
            String said = QueryString.first(query, "at");
            if (said == null) return Read.ok(ROOT);
            if (!said.startsWith("/")) return Read.refused("at", said, "an authentic path, from /");
            return Read.ok(new CatalogueAt(said));
        }

        @Override
        public Map<String, List<String>> to(CatalogueAt params) {
            var q = QueryString.params();
            if (!params.equals(ROOT)) QueryString.put(q, "at", params.at());
            return q;
        }
    }
}
