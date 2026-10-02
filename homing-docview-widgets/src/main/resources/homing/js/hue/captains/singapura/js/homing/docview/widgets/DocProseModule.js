// =============================================================================
// DocProse — a doc's prose part: markdown text, drawn from its tokens - paragraphs,
// lists, quotes, and what is inline in them - every element through its own
// party. A ContentWidget of the type prose.
//
//   new DocProse(container, params)   params: { doc, key }
// =============================================================================

class DocProse extends ContentWidget {
    constructor(container, params) { super(container, params, PROSE, "prose"); }

    _draw(content, branch, into) { new MarkdownDom(branch).blocks(content.text, into); }
}
