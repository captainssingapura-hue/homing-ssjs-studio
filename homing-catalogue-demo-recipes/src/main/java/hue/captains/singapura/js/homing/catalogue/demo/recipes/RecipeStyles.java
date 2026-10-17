package hue.captains.singapura.js.homing.catalogue.demo.recipes;

import hue.captains.singapura.js.homing.core.CssClass;
import hue.captains.singapura.js.homing.core.CssGroup;

import java.util.List;


/** A recipe card's sheet, in the design's words: a raised card, a heading, what goes in and how. */
public record RecipeStyles() implements CssGroup<RecipeStyles> {

    public static final RecipeStyles INSTANCE = new RecipeStyles();

    /** The card: raised, one column. */
    public record rc_root() implements CssClass<RecipeStyles> {
        @Override public String body() { return """
            display: flex;
            flex-direction: column;
            gap: 8px;
            max-width: 38rem;
            margin: 24px 32px;
            padding: 20px 24px;
            box-sizing: border-box;
            """;
        }
    }

    public record rc_title() implements CssClass<RecipeStyles> {
        @Override public String body() { return "margin: 0;\n"; }
    }

    public record rc_serves() implements CssClass<RecipeStyles> {
        @Override public String body() { return "margin: 0;\n"; }
    }

    public record rc_heading() implements CssClass<RecipeStyles> {
        @Override public String body() { return "margin: 8px 0 0;\n"; }
    }

    public record rc_list() implements CssClass<RecipeStyles> {
        @Override public String body() { return """
            margin: 0;
            padding-left: 1.4em;
            line-height: 1.6;
            """;
        }
    }

    /** The timer's clock: the minutes and seconds left, as a heading, its digits all one width. */
    public record rc_clock() implements CssClass<RecipeStyles> {
        @Override public String body() { return """
            margin: 8px 0;
            font-variant-numeric: tabular-nums;
            """;
        }
    }

    /** The timer's buttons, in a row. */
    public record rc_row() implements CssClass<RecipeStyles> {
        @Override public String body() { return """
            display: flex;
            gap: 8px;
            """;
        }
    }

    @Override
    public List<CssClass<RecipeStyles>> cssClasses() {
        return List.of(new rc_root(), new rc_title(), new rc_serves(), new rc_heading(), new rc_list(), new rc_clock(), new rc_row());
    }
}
