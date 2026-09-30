package hue.captains.singapura.js.homing.catalogue.site;

import hue.captains.singapura.js.homing.core.CssClass;
import hue.captains.singapura.js.homing.core.CssGroup;

import java.util.List;

/**
 * The listing page's sheet: layout alone - the widget it hosts wears the design's
 * words. The MPA's slot, the whole of what the chrome leaves it; a bar of the views
 * it offers; and under it the box the widget fills.
 */
public record ListingStyles() implements CssGroup<ListingStyles> {

    public static final ListingStyles INSTANCE = new ListingStyles();

    /**
     * The MPA's slot, as the listing lays it out: the whole of what the page leaves
     * it, not the reading column's - the bar, then the widget's box, one under the
     * other. Unlayered, so it outranks the column the slot wears in the MPA's layout layer.
     */
    public record cl_page() implements CssClass<ListingStyles> {
        @Override public String body() { return """
            position: relative;
            flex: 1 1 auto;
            display: flex;
            flex-direction: column;
            gap: 12px;
            width: auto;
            max-width: none;
            min-height: 0;
            margin: 0;
            padding: 12px 24px 16px;
            box-sizing: border-box;
            overflow: hidden;
            """;
        }
    }

    /** The views the page offers, in a row. */
    public record cl_bar() implements CssClass<ListingStyles> {
        @Override public String body() { return """
            display: flex;
            flex: 0 0 auto;
            align-items: center;
            gap: 6px;
            """;
        }
    }

    /** The box the widget is lent: the rest of the slot, for it to fill; never scrolled - the widget scrolls inside. */
    public record cl_host() implements CssClass<ListingStyles> {
        @Override public String body() { return """
            position: relative;
            flex: 1 1 auto;
            min-height: 0;
            overflow: hidden;
            """;
        }
    }

    @Override
    public List<CssClass<ListingStyles>> cssClasses() { return List.of(new cl_page(), new cl_bar(), new cl_host()); }
}
