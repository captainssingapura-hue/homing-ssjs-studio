package hue.captains.singapura.js.homing.docview.reference;

import hue.captains.singapura.js.homing.studio.base.ClasspathMarkdownDoc;
import hue.captains.singapura.js.homing.studio.base.DocReference;
import hue.captains.singapura.js.homing.studio.base.ExternalReference;
import hue.captains.singapura.js.homing.studio.base.Reference;

import java.util.List;
import java.util.UUID;

/**
 * The markdown reference: a markdown doc on the classpath - the kind most docs are - holding
 * every construct the markdown reader meets, each section saying what it exercises. Its text is
 * {@code MarkdownReference.md}, beside this class under {@code docs/}.
 *
 * <p>Its references are one of each kind a reference can resolve to: two docs placed beside it,
 * one off the site, one no catalogue places - each cited in its Citations section, the rigid one
 * from a table's cell as well - and one declared and never cited.</p>
 */
@SuppressWarnings("deprecation")
public record MarkdownReference() implements ClasspathMarkdownDoc {

    public static final MarkdownReference INSTANCE = new MarkdownReference();

    @Override public UUID uuid() { return UUID.fromString("5d2b8f2e-6a41-4c3e-9f0b-1d7e3c9a4b01"); }

    @Override public String title() { return "Markdown reference"; }

    @Override public String summary() { return "Every construct the markdown reader meets: prose, tables and code between it, headings of every kind."; }

    @Override
    public List<Reference> references() {
        return List.of(
                new DocReference("rigid", ReferenceDocs.RIGID),
                new DocReference("composed", ReferenceDocs.COMPOSED),
                new ExternalReference("commonmark", "https://commonmark.org", "CommonMark", "The markdown specification the reader follows."),
                new DocReference("unplaced", ReferenceDocs.UNPLACED),
                new DocReference("named-rigid", ReferenceDocs.NAMED_RIGID));
    }
}
