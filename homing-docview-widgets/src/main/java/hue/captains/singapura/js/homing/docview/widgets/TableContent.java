package hue.captains.singapura.js.homing.docview.widgets;

import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.workspace.content.ContentParty;
import hue.captains.singapura.js.homing.workspace.content.Item;
import hue.captains.singapura.js.homing.workspace.content.Param;
import hue.captains.singapura.js.homing.workspace.parties.PartyType;

import java.util.List;

/**
 * A table, a content type: columns and rows, every cell markdown text. What a table widget shows.
 */
public sealed interface TableContent {

    /** A column: its title, markdown text, and its alignment - left, center, right, or empty. */
    record Column(String title, String align) {}

    /** A cell: markdown text, its spans, its badge (success, warning, error, or empty), its alignment and emphasis (strong, muted, or empty). */
    record Cell(String text, int colSpan, int rowSpan, String badge, String align, String emphasis) {}

    /** A row: its cells, in the columns' order. */
    record Row(List<Cell> cells) {}

    /** A table: its columns, its rows, and its caption - empty when it has none. */
    record Table(List<Column> columns, List<Row> rows, String caption) {}

    record Wanted(List<Param> params) implements TableContent {}
    record Fetch(List<Item> items) implements TableContent {}
    record Loaded(List<Param> params, Table content) implements TableContent {}
    record Failed(List<Param> params, String why) implements TableContent {}
    record Content(List<Param> params, Table content) implements TableContent {}
    record Unavailable(List<Param> params, String why) implements TableContent {}

    /** The type: {@code table}, served as {@code TABLE}; its steward DocView's, which reads it from the doc. */
    PartyType<TableContent> TYPE = ContentParty.type("table", TableContent.class)
            .servedFrom(new ModuleImports<>(List.of(new TableContentModule.TABLE()), TableContentModule.INSTANCE))
            .withSteward(new ModuleImports<>(List.of(new TableStewardModule.TableSteward()), TableStewardModule.INSTANCE));
}
