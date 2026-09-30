package hue.captains.singapura.js.homing.docview.site;

import hue.captains.singapura.tao.http.action.TypedContent;

/** A doc route's reply: JSON text. */
public record DocJson(String body) implements TypedContent {
    @Override public String contentType() { return "application/json; charset=utf-8"; }
}
