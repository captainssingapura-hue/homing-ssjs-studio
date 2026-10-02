package hue.captains.singapura.js.homing.docview.tree;

import hue.captains.singapura.js.homing.workspace.groups.core.models.TreePlacement.Name;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.Normalizer;
import java.util.HashSet;
import java.util.HexFormat;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

/**
 * A node's name: its segment of the path, and of the address a reader shares. The rules:
 *
 * <ul>
 *   <li>a rigid doc's author may name a node; the name is used as written;</li>
 *   <li>otherwise, the heading lower-cased, accents dropped, runs of {@code a-z0-9} joined by {@code -};</li>
 *   <li>a name that comes out empty is {@code section};</li>
 *   <li>over {@value #CUT} characters, it is cut at a word, then {@code -} and 6 hex of a digest of the whole;</li>
 *   <li>a sibling with the same name takes {@code -2}, {@code -3}, counted among siblings only.</li>
 * </ul>
 *
 * A name depends on its heading alone and on the siblings before it, so a section's address
 * changes only when its heading, or one above it, is renamed.
 */
public final class NodeNames {

    private NodeNames() {}

    /** The longest a name made from a heading is, before its digest. */
    public static final int CUT = 40;

    /** What a heading with nothing namable in it is called. */
    public static final String EMPTY = "section";

    /** A heading's name, before its siblings are counted. */
    public static String of(String heading) {
        Objects.requireNonNull(heading, "heading");
        String slug = slug(heading);
        if (slug.isEmpty()) return EMPTY;
        if (slug.length() <= CUT) return slug;
        String cut = slug.substring(0, CUT);
        if (slug.charAt(CUT) != '-') {   // cut inside a word: back to the word's start
            int at = cut.lastIndexOf('-');
            if (at > 0) cut = cut.substring(0, at);
        }
        return cut.replaceAll("-+$", "") + "-" + digest(slug);
    }

    /** Six hex of a SHA-256 of the whole name. */
    static String digest(String whole) {
        try {
            byte[] d = MessageDigest.getInstance("SHA-256").digest(whole.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(d).substring(0, 6);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 is always there", e);
        }
    }

    /** The heading lower-cased, accents dropped, runs of {@code a-z0-9} joined by {@code -}: the whole, before any cut. */
    static String slug(String heading) {
        String plain = Normalizer.normalize(heading, Normalizer.Form.NFD).replaceAll("\\p{M}+", "").toLowerCase(Locale.ROOT);
        return plain.replaceAll("[^a-z0-9]+", "-").replaceAll("^-+|-+$", "");
    }

    /** Whether a heading's name is cut, and so carries a digest of the whole. */
    public static boolean cuts(String heading) { return slug(heading).length() > CUT; }

    /** The names of one node's children, in order: each unique among them, a repeat taking {@code -2}, {@code -3}. */
    public static final class Siblings {

        private final Set<String> taken = new HashSet<>();

        /** The name a child is given: its own, unless a sibling before it has it. */
        public Name take(String name) {
            Objects.requireNonNull(name, "name");
            String given = name;
            for (int n = 2; !taken.add(given); n++) {
                String suffix = "-" + n;
                String base = name.length() + suffix.length() > 48 ? name.substring(0, 48 - suffix.length()).replaceAll("-+$", "") : name;
                given = base + suffix;
            }
            return Name.of(given);
        }
    }
}
