package hue.captains.singapura.js.homing.docview.widgets;

import hue.captains.singapura.js.homing.core.ModuleImports;
import hue.captains.singapura.js.homing.workspace.content.ContentParty;
import hue.captains.singapura.js.homing.workspace.content.Item;
import hue.captains.singapura.js.homing.workspace.content.Param;
import hue.captains.singapura.js.homing.workspace.parties.PartyType;

import java.util.List;

/**
 * An image, a content type: SVG drawn inline - so it takes the theme's colours - or a raster fetched by its address. What an image widget shows.
 */
public sealed interface ImageContent {

    /** An image: SVG markup, or a raster's address; its accessible name, and its caption - empty when it has none. */
    record Image(String svg, String src, String alt, String caption) {}

    record Wanted(List<Param> params) implements ImageContent {}
    record Fetch(List<Item> items) implements ImageContent {}
    record Loaded(List<Param> params, Image content) implements ImageContent {}
    record Failed(List<Param> params, String why) implements ImageContent {}
    record Content(List<Param> params, Image content) implements ImageContent {}
    record Unavailable(List<Param> params, String why) implements ImageContent {}

    /** The type: {@code image}, served as {@code IMAGE}; its steward DocView's, which reads it from the doc. */
    PartyType<ImageContent> TYPE = ContentParty.type("image", ImageContent.class)
            .servedFrom(new ModuleImports<>(List.of(new ImageContentModule.IMAGE()), ImageContentModule.INSTANCE))
            .withSteward(new ModuleImports<>(List.of(new ImageStewardModule.ImageSteward()), ImageStewardModule.INSTANCE));
}
