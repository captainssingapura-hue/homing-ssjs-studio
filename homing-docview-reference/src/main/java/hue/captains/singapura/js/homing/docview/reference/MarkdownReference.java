package hue.captains.singapura.js.homing.docview.reference;

import hue.captains.singapura.js.homing.studio.base.ClasspathMarkdownDoc;

import java.util.UUID;

/**
 * The markdown reference: a markdown doc on the classpath - the kind most docs are - holding
 * every construct the markdown reader meets, each section saying what it exercises. Its text is
 * {@code MarkdownReference.md}, beside this class under {@code docs/}.
 */
@SuppressWarnings("deprecation")
public record MarkdownReference() implements ClasspathMarkdownDoc {

    public static final MarkdownReference INSTANCE = new MarkdownReference();

    @Override public UUID uuid() { return UUID.fromString("5d2b8f2e-6a41-4c3e-9f0b-1d7e3c9a4b01"); }

    @Override public String title() { return "Markdown reference"; }

    @Override public String summary() { return "Every construct the markdown reader meets: prose, tables and code between it, headings of every kind."; }
}
