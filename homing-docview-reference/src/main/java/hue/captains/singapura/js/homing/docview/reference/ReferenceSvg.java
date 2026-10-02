package hue.captains.singapura.js.homing.docview.reference;

import hue.captains.singapura.js.homing.studio.base.SvgSource;

import java.util.UUID;

/** A small SVG the reference docs show: two boxes and an arrow, drawn in the current colour. */
public record ReferenceSvg() implements SvgSource {

    public static final ReferenceSvg INSTANCE = new ReferenceSvg();

    @Override public UUID uuid() { return UUID.fromString("5d2b8f2e-6a41-4c3e-9f0b-1d7e3c9a4b05"); }

    @Override public String title() { return "A heading and its leaf"; }

    @Override
    public String contents() {
        return """
            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 240 60" role="img">
              <rect x="4" y="14" width="80" height="32" rx="4" fill="none" stroke="currentColor"/>
              <text x="44" y="35" text-anchor="middle" font-size="12" fill="currentColor">heading</text>
              <path d="M88 30 H146" stroke="currentColor" marker-end="url(#arrow)"/>
              <defs><marker id="arrow" viewBox="0 0 8 8" refX="7" refY="4" markerWidth="8" markerHeight="8" orient="auto">
                <path d="M0 0 L8 4 L0 8 z" fill="currentColor"/></marker></defs>
              <rect x="150" y="14" width="80" height="32" rx="4" fill="none" stroke="currentColor"/>
              <text x="190" y="35" text-anchor="middle" font-size="12" fill="currentColor">leaf</text>
            </svg>""";
    }
}
